package org.openengsb.labs.endtoend.karaf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.shell.KarafClientShell;
import org.openengsb.labs.endtoend.karaf.shell.KarafShell;
import org.openengsb.labs.endtoend.karaf.shell.RemoteShell;
import org.openengsb.labs.endtoend.karaf.shell.Shell;

public class KarafService implements Karaf {
    private final String applicationName;
    private final Integer port;
    private final String startCmd;
    private final String clientStartCmd;
    private Process karafProcess;
    private KarafShell karafShell;

    public KarafService(String applicationName, Integer port, String startCmd, String clientStartCmd) {
        this.applicationName = applicationName;
        this.port = port;
        this.startCmd = startCmd;
        this.clientStartCmd = clientStartCmd;
    }

    @Override
    public void start(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        try {
            startKaraf(timeout, timeUnit);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startKaraf(Long timeout, TimeUnit timeUnit) throws TimeoutException, FileNotFoundException,
            IOException {
        new File(this.startCmd).setExecutable(true);

        ProcessBuilder pb = new ProcessBuilder(this.startCmd);
        // TODO Set working dir? pb.directory(new File("myDir"));

        this.karafProcess = pb.start();
        this.karafShell = new KarafShell(this.karafProcess.getOutputStream(), this.karafProcess.getInputStream(),
                this.applicationName);
        this.karafShell.waitForPrompt(timeout, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        this.karafShell.execute("system:shutdown");
        this.karafShell.execute("yes");
        this.karafShell.close();

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> waitFor = service.submit(new ShutdownWorker(this.karafProcess));
        try {
            waitFor.get(timeout, timeUnit);
        } catch (TimeoutException e) {
            waitFor.cancel(true);
            this.karafProcess.destroy();
            throw e;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
    }

    private class ShutdownWorker implements Callable<Integer> {
        private Process process;

        public ShutdownWorker(Process process) {
            this.process = process;
        }

        @Override
        public Integer call() throws IOException, InterruptedException {
            try {
                return this.process.waitFor();
            } catch (InterruptedException e) {
                return 0;
            }
        }
    }

    @Override
    public void kill() {
        this.karafProcess.destroy();
    }

    @Override
    public Shell getShell() {
        return this.karafShell;
    }

    @Override
    public RemoteShell login(String user, String pass, Long timeout, TimeUnit timeUnit) throws TimeoutException {
        KarafClientShell shell = new KarafClientShell(this.clientStartCmd);
        shell.login(this.applicationName, "localhost", this.port, user, pass, timeout, timeUnit);

        return shell;
    }
}

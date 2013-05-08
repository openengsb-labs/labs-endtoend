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

import org.openengsb.labs.endtoend.api.Karaf;
import org.openengsb.labs.endtoend.api.RemoteShell;
import org.openengsb.labs.endtoend.api.Shell;
import org.openengsb.labs.endtoend.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.endtoend.karaf.shell.KarafClientShell;
import org.openengsb.labs.endtoend.karaf.shell.KarafShell;

public class KarafService implements Karaf {
    private final String startCmd;
    private Process karafProcess;
    private KarafShell karafShell;
    private String clientStartCmd;

    public KarafService(final String startCmd, final String clientStartCmd) {
        this.startCmd = startCmd;
        this.clientStartCmd = clientStartCmd;
    }

    @Override
    public void start(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        try {
            startKaraf(timeout, timeUnit);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startKaraf(Long timeout, TimeUnit timeUnit) throws TimeoutException, FileNotFoundException,
            IOException {
        new File(this.startCmd).setExecutable(true);

        ProcessBuilder pb = new ProcessBuilder(this.startCmd);
        // TODO Set working dir? pb.directory(new File("myDir"));

        this.karafProcess = pb.start();
        this.karafShell = new KarafShell(this.karafProcess.getOutputStream(), this.karafProcess.getInputStream());
        this.karafShell.waitForPrompt(timeout, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        this.karafShell.execute("osgi:shutdown");
        this.karafShell.execute("yes");
        this.karafShell.close();

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> waitFor = service.submit(new ShutdownWorker(this.karafProcess));
        try {
            waitFor.get(timeout, timeUnit);
        } catch (TimeoutException e) {
            this.karafProcess.destroy();
            throw e;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }

    private class ShutdownWorker implements Callable<Integer> {
        private Process process;

        public ShutdownWorker(Process process) {
            this.process = process;
        }

        @Override
        public Integer call() throws IOException, InterruptedException {
            return this.process.waitFor();
        }
    }

    @Override
    public Shell getShell() {
        return this.karafShell;
    }

    @Override
    public RemoteShell login(String user, String pass, Long timeout, TimeUnit timeUnit) throws TimeoutException {
        KarafClientShell shell = new KarafClientShell(this.clientStartCmd, new KarafPromptRecognizer(user, "root"));
        shell.login(user, pass, timeout, timeUnit);

        return shell;
    }
}

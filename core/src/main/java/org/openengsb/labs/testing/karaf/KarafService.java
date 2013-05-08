package org.openengsb.labs.testing.karaf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.testing.api.Karaf;
import org.openengsb.labs.testing.api.RemoteShell;
import org.openengsb.labs.testing.api.Shell;
import org.openengsb.labs.testing.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.testing.karaf.shell.KarafClientShell;
import org.openengsb.labs.testing.karaf.shell.KarafShell;

public class KarafService implements Karaf {
    private final String startCmd;
    private final KarafPromptRecognizer karafPromptRecognizer = new KarafPromptRecognizer("karaf", "root");
    private Process karafProcess;
    private KarafShell karafShell;
    private String clientStartCmd;

    public KarafService(final String startCmd, final String clientStartCmd) {
        this.startCmd = startCmd;
        this.clientStartCmd = clientStartCmd;
    }

    @Override
    public void start(Long timeout, TimeUnit timeUnit)
        throws TimeoutException {
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

        new File(this.startCmd).setExecutable(true); // TODO: Preserve permissions!

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
    }

    @Override
    public Shell getShell() {
        return this.karafShell;
    }

    @Override
    public RemoteShell login(String user, String pass, Long timeout, TimeUnit timeUnit) throws TimeoutException {
        KarafClientShell shell = new KarafClientShell(this.clientStartCmd, this.karafPromptRecognizer); // TODO Create
                                                                                                        // karafPromptRecognizer
                                                                                                        // for client.
        shell.login(user, pass, timeout, timeUnit);
        return shell;
    }
}

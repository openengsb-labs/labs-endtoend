package org.openengsb.labs.endtoend.karaf;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.configuration.KarafConfiguration;
import org.openengsb.labs.endtoend.karaf.shell.KarafClientShell;
import org.openengsb.labs.endtoend.karaf.shell.KarafShell;
import org.openengsb.labs.endtoend.karaf.shell.RemoteShell;
import org.openengsb.labs.endtoend.karaf.shell.Shell;
import org.openengsb.labs.endtoend.util.TimeoutableProcess;

public class KarafService implements Karaf {
    private final KarafConfiguration karafConfiguration;
    private TimeoutableProcess karafProcess;
    private KarafShell karafShell;

    public KarafService(KarafConfiguration karafConfiguration) {
        this.karafConfiguration = karafConfiguration;
    }

    @Override
    public void start(Long timeout, TimeUnit timeUnit) throws CommandTimeoutException {
        this.karafConfiguration.getStartCmd().setExecutable(true);
        try {
            ProcessBuilder pb = new ProcessBuilder(this.karafConfiguration.getStartCmd().getAbsolutePath());
            // TODO Set working dir? pb.directory(new File("myDir"));
            this.karafProcess = new TimeoutableProcess(pb.start());
        } catch (IOException e) {
            throw new KarafException("Could not start karaf.", e);
        }
        this.karafShell = new KarafShell(this.karafProcess.getOutputStream(), this.karafProcess.getInputStream(),
                this.karafConfiguration.getApplicationName());
        try {
            this.karafShell.waitForPrompt(timeout, timeUnit);
        } catch (TimeoutException e) {
            throw new CommandTimeoutException("", e);
        }
    }

    @Override
    public void shutdown(Long timeout, TimeUnit timeUnit) throws CommandTimeoutException {
        this.karafShell.execute("system:shutdown");
        this.karafShell.execute("yes");
        this.karafShell.close();

        try {
            this.karafProcess.waitFor(timeout, timeUnit);
        } catch (TimeoutException e) {
            this.karafProcess.destroy();
            throw new CommandTimeoutException("shutdown", e);
        }
    }

    @Override
    public Shell getShell() {
        return this.karafShell;
    }

    @Override
    public RemoteShell login(String username, String pass, Long timeout, TimeUnit timeUnit)
            throws CommandTimeoutException {
        KarafClientShell shell = new KarafClientShell(this.karafConfiguration.getClientStartCmd());
        shell.login(this.karafConfiguration.getApplicationName(), this.karafConfiguration.getSshHost(),
                this.karafConfiguration.getSshPort(), username, pass, timeout, timeUnit);

        return shell;
    }
}

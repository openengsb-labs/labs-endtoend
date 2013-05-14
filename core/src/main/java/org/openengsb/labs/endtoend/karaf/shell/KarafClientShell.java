package org.openengsb.labs.endtoend.karaf.shell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.endtoend.karaf.output.OutputHandler;
import org.openengsb.labs.endtoend.util.TimeoutableProcess;

public class KarafClientShell implements RemoteShell {
    private final String startCmd;
    private PrintWriter pw;
    private TimeoutableProcess process;
    private OutputHandler outputHandler;

    public KarafClientShell(final String startCmd) {
        this.startCmd = startCmd;
    }

    public void login(String applicationName, String host, Integer port, String user, String pass, Long timeout,
            TimeUnit timeUnit) throws CommandTimeoutException {
        startClient(applicationName, host, port, user, pass);

        try {
            outputHandler.waitForPrompt(timeout, timeUnit);
        } catch (TimeoutException e) {
            killClient();
            throw new CommandTimeoutException("login", e);
        }
    }

    @Override
    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        outputHandler.waitForPrompt(timeout, timeUnit);
    }

    private void startClient(String applicationName, String host, Integer port, String user, String pass) {
        new File(this.startCmd).setExecutable(true);

        ProcessBuilder processBuilder = new ProcessBuilder(this.startCmd, "-a", port.toString(), "-h", host, "-u", user);
        processBuilder.redirectError(new File("/Users/Dominik/test.txt"));
        try {
            this.process = new TimeoutableProcess(processBuilder.start());
        } catch (IOException e) {
            throw new IllegalStateException("Could not start client.", e);
        }

        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream())));
        if (!pass.isEmpty()) {
            this.pw.println(pass);
            this.pw.flush();
        }
        this.outputHandler = new OutputHandler(new InputStreamReader(this.process.getInputStream()),
                new KarafPromptRecognizer(user, applicationName));
    }

    private void killClient() {
        try {
            stopClient(0L, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Expected.
        }
    }

    private void stopClient(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        this.pw.close();
        this.outputHandler.shutdown();
        try {
            this.process.waitFor(timeout, timeUnit);
        } catch (TimeoutException e) {
            this.process.destroy();
            throw e;
        }
    }

    @Override
    public void logout(Long timeout, TimeUnit timeUnit) throws CommandTimeoutException {
        this.pw.println("logout");
        this.pw.flush();
        try {
            stopClient(timeout, timeUnit);
        } catch (TimeoutException e) {
            throw new CommandTimeoutException("logout", e);
        }
    }

    @Override
    public String execute(String command, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException {
        this.pw.println(command);
        this.pw.flush();
        try {
            return this.outputHandler.getOutput(timeout, timeUnit);
        } catch (TimeoutException e) {
            throw new CommandTimeoutException(command, e);
        }
    }

    @Override
    public void execute(String command) {
        this.pw.println(command);
        this.pw.flush();
    }
}

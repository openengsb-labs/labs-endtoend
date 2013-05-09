package org.openengsb.labs.endtoend.karaf.shell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.KarafException;
import org.openengsb.labs.endtoend.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.endtoend.karaf.output.OutputHandler;

public class KarafClientShell implements RemoteShell {
    private final String startCmd;
    private PrintWriter pw;
    private Process process;
    private OutputHandler outputHandler;

    public KarafClientShell(final String startCmd) {
        this.startCmd = startCmd;
    }

    public void login(String applicationName, String host, Integer port, String user, String pass, Long timeout,
            TimeUnit timeUnit) throws TimeoutException {
        startClient(applicationName, host, port, user, pass);

        try {
            outputHandler.waitForPrompt(timeout, timeUnit);
        } catch (TimeoutException e) {
            stopClient();
            throw e;
        }
    }

    @Override
    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        outputHandler.waitForPrompt(timeout, timeUnit);
    }

    private void startClient(String applicationName, String host, Integer port, String user, String pass) {
        new File(this.startCmd).setExecutable(true);

        ProcessBuilder processBuilder = new ProcessBuilder(this.startCmd, "-a", port.toString(), "-h", host, "-u", user);
        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream())));
        if (!pass.isEmpty()) {
            this.pw.println(pass);
            this.pw.flush();
        }
        this.outputHandler = new OutputHandler(new InputStreamReader(this.process.getInputStream()),
                new KarafPromptRecognizer(user, applicationName));
    }

    private void stopClient() {
        this.pw.close();
        this.outputHandler.shutdown();
    }

    @Override
    public void logout() throws KarafException {
        this.pw.println("logout");
        this.pw.flush();
        stopClient();
    }

    @Override
    public String execute(String command, Long timeout, TimeUnit timeUnit) throws TimeoutException {
        this.pw.println(command);
        this.pw.flush();
        return this.outputHandler.getOutput(timeout, timeUnit);
    }

    @Override
    public void execute(String command) {
        this.pw.println(command);
        this.pw.flush();
    }
}

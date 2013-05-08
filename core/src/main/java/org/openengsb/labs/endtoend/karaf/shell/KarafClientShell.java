package org.openengsb.labs.endtoend.karaf.shell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.api.RemoteShell;
import org.openengsb.labs.endtoend.karaf.KarafException;
import org.openengsb.labs.endtoend.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.endtoend.karaf.output.OutputHandler;

public class KarafClientShell implements RemoteShell {
    private static String PROPERTY_KARAF_CLIENT_CMD = "karaf.client.cmd";

    private PrintWriter pw;
    private Process process;
    private OutputHandler outputHandler;
    private final KarafPromptRecognizer karafPromptRecognizer;
    private final String startCmd;

    public KarafClientShell(final String startCmd, KarafPromptRecognizer karafPromptRecognizer) {
        this.startCmd = startCmd;
        this.karafPromptRecognizer = karafPromptRecognizer;
    }

    public void login(String user, String pass, Long timeout, TimeUnit timeUnit) throws TimeoutException {
        startClient(user, pass);
        outputHandler.recognize(timeout, timeUnit, this.karafPromptRecognizer);
    }

    @Override
    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        outputHandler.recognize(timeout, timeUnit, this.karafPromptRecognizer);
    }

    private void startClient(String user, String pass) {
        new File(this.startCmd).setExecutable(true); // TODO: Preserve permissions!

        ProcessBuilder processBuilder =
            new ProcessBuilder(this.startCmd, "-a", "8101", "-h", "localhost", "-u", user, "-p", pass);
        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream())));
        this.outputHandler = new OutputHandler(new InputStreamReader(this.process.getInputStream()),
            karafPromptRecognizer);
    }

    private void stopClient() {
        this.pw.close();
        this.outputHandler.shutdown();
    }

    @Override
    public void logout() throws KarafException {
        this.pw.println("osgi:shutdown");
        this.pw.println("yes");
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

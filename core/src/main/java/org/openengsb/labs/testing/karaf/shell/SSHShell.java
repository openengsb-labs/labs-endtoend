package org.openengsb.labs.testing.karaf.shell;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.testing.api.RemoteShell;
import org.openengsb.labs.testing.karaf.KarafException;
import org.openengsb.labs.testing.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.testing.karaf.output.OutputHandler;
import org.openengsb.labs.testing.recognizer.Recognizer;

public class SSHShell implements RemoteShell {
    private PrintWriter pw;
    private Process process;
    private OutputHandler outputHandler;
    private final Recognizer karafPromptRecognizer;

    public SSHShell(KarafPromptRecognizer karafPromptRecognizer) {
        this.karafPromptRecognizer = karafPromptRecognizer;
    }

    public void login(String user, String pass, Long timeout, TimeUnit timeUnit) throws TimeoutException {
        startClient();
        outputHandler.recognize(timeout, timeUnit, this.karafPromptRecognizer);
    }

    @Override
    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        outputHandler.recognize(timeout, timeUnit, this.karafPromptRecognizer);
    }

    private void startClient() {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";

        ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath,
            org.apache.felix.karaf.client.Main.class.getName());
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
        return this.outputHandler.getOutput(timeout, timeUnit);
    }

    @Override
    public void execute(String command) {
        this.pw.println(command);
    }
}

package org.openengsb.testing.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.openengsb.testing.api.User;

public class RemoteUser implements User {

    private PrintWriter pw;
    private Process process;
    private OutputRecognizer outputRecognizer;
    private final Recognizer karafPromptRecognizer = new KarafPromptRecognizer("karaf", "root");

    @Override
    public void login() throws KarafException {
        startClient();
        Boolean success = outputRecognizer.recognize(10, TimeUnit.SECONDS, this.karafPromptRecognizer);
        if (!success) {
            throw new KarafException(new KarafCommandFailedException());
        }
    }

    @Override
    public void logout() throws KarafException {
        this.pw.println("osgi:shutdown");
        this.pw.println("yes");
        this.pw.flush();
        stopClient();
    }

    private void startClient() {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";

        ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath,
                org.apache.karaf.client.Main.class.getName());
        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream())));
        this.outputRecognizer = new OutputRecognizer(new InputStreamReader(this.process.getInputStream()));
    }

    private void stopClient() {
        this.pw.close();
        this.outputRecognizer.shutdown();
    }
}

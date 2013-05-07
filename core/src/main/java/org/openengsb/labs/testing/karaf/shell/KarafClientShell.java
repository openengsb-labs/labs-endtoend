package org.openengsb.labs.testing.karaf.shell;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.testing.api.RemoteShell;
import org.openengsb.labs.testing.karaf.KarafException;
import org.openengsb.labs.testing.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.testing.karaf.output.OutputHandler;

public class KarafClientShell implements RemoteShell {
    private static final String PROPERTY_FILE_KARAF = "karaf.properties";
    private static String PROPERTY_KARAF_CLIENT_CMD = "karaf.client.cmd";

    private PrintWriter pw;
    private Process process;
    private OutputHandler outputHandler;
    private final KarafPromptRecognizer karafPromptRecognizer;

    public KarafClientShell(KarafPromptRecognizer karafPromptRecognizer) {
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
        Properties karafProperties = null;
        try {
            karafProperties = loadProperties();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String karafCmd = karafProperties.getProperty(PROPERTY_KARAF_CLIENT_CMD);
        ProcessBuilder processBuilder =
            new ProcessBuilder(karafCmd, "-a", "8101", "-h", "localhost", "-u", "karaf", "-p", "karaf");
        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream())));
        this.outputHandler = new OutputHandler(new InputStreamReader(this.process.getInputStream()),
            karafPromptRecognizer);
    }

    private Properties loadProperties() throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_KARAF);
        properties.load(stream);
        stream.close();
        return properties;
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

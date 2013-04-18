package org.openengsb.testing.service;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openengsb.testing.api.Karaf;

public class KarafService implements Karaf {
    private static final String PROPERTY_FILE_KARAF = "karaf.properties";
    private static final String PROPERTY_KARAF_CMD = "karaf.cmd";
    private PrintWriter pw;
    private Process karafProcess;
    private OutputRecognizer outputRecognizer;
    private final Recognizer karafPromptRecognizer = new KarafPromptRecognizer("karaf", "root");

    @Override
    public void start(Integer timeout) throws KarafException {
        try {
            startKaraf();
            Boolean success = outputRecognizer.recognize(10, TimeUnit.SECONDS, this.karafPromptRecognizer);
            if (!success) {
                throw new KarafException(new KarafCommandFailedException());
            }
        } catch (Exception e) {
            throw new KarafException(e);
        }
    }

    private void startKaraf() throws FileNotFoundException, IOException, InterruptedException {
        Properties karafProperties = loadProperties();
        String karafCmd = karafProperties.getProperty(PROPERTY_KARAF_CMD);
        ProcessBuilder pb = new ProcessBuilder(karafCmd);
        // TODO Set working dir? pb.directory(new File("myDir"));

        this.karafProcess = pb.start();

        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.karafProcess.getOutputStream())));
        this.outputRecognizer = new OutputRecognizer(new InputStreamReader(this.karafProcess.getInputStream()));
    }

    @Override
    public void shutdown(Integer timeout) throws KarafException {
        this.pw.println("osgi:shutdown");
        this.pw.println("yes");
        this.pw.flush();
    }

    private Properties loadProperties() throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_KARAF);
        properties.load(stream);
        stream.close();
        return properties;
    }

    @Override
    public void addFeature(String feature) {
    }
}

package org.openengsb.labs.endtoend.testcontext.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ContextConfiguration {
    private static final String PROPERTY_DISTRIBUTION_URI = "distribution.uri";
    private static final String PROPERTY_KARAF_APPNAME = "karaf.appname";
    private static final String PROPERTY_KARAF_PORT = "karaf.port";
    private static final String PROPERTY_KARAF_CMD = "karaf.cmd";
    private static final String PROPERTY_KARAF_CLIENT_CMD = "karaf.client.cmd";

    private final String distributionURI;
    private final String karafAppname;
    private final Integer karafPort;
    private final String karafCmd;
    private final String karafClientCmd;

    public ContextConfiguration(String distributionURI, String karafAppname, Integer karafPort, String karafCmd,
            String karafClientCmd) throws InvalidConfiguration {
        this.distributionURI = distributionURI;
        this.karafAppname = karafAppname;
        this.karafPort = karafPort;
        this.karafCmd = karafCmd;
        this.karafClientCmd = karafClientCmd;

        checkConfiguration();
    }

    private void checkConfiguration() throws InvalidConfiguration {
        String[] mandatory = { this.distributionURI, this.karafAppname, this.karafCmd, this.karafClientCmd };

        for (String m : mandatory) {
            if (m.isEmpty()) {
                throw new InvalidConfiguration("A mandatory property is missing.");
            }
        }
    }

    public static ContextConfiguration loadFromProperties(Properties properties) throws InvalidConfiguration {
        String karafAppname = properties.getProperty(PROPERTY_KARAF_APPNAME, "").trim();
        String karafPortString = null;
        Integer karafPort;
        try {
            karafPortString = properties.getProperty(PROPERTY_KARAF_PORT, "").trim();
            karafPort = Integer.parseInt(karafPortString);
        } catch (NumberFormatException e) {
            throw new InvalidConfiguration("Invalid value for property " + PROPERTY_KARAF_PORT + ": " + karafPortString);
        }
        String karafCmd = properties.getProperty(PROPERTY_KARAF_CMD, "").trim();
        String karafClientCmd = properties.getProperty(PROPERTY_KARAF_CLIENT_CMD, "").trim();
        String distributionURI = properties.getProperty(PROPERTY_DISTRIBUTION_URI, "").trim();

        return new ContextConfiguration(distributionURI, karafAppname, karafPort, karafCmd, karafClientCmd);
    }

    public static ContextConfiguration loadFromFile(File file) throws FileNotFoundException, InvalidConfiguration {
        Properties properties = new Properties();
        try (InputStream stream = new FileInputStream(file)) {
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading configuration from context file.", e);
        }

        return loadFromProperties(properties);
    }

    public String getDistributionURI() {
        return distributionURI;
    }

    public String getKarafAppname() {
        return karafAppname;
    }

    public Integer getKarafPort() {
        return karafPort;
    }

    public String getKarafCmd() {
        return karafCmd;
    }

    public String getKarafClientCmd() {
        return karafClientCmd;
    }
}

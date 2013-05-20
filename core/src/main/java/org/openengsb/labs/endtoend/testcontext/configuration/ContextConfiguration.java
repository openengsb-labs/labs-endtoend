package org.openengsb.labs.endtoend.testcontext.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ContextConfiguration {
    private static final String PROPERTY_DISTRIBUTION_URI = "distribution.uri";
    private static final String PROPERTY_KARAF_ROOT = "karaf.root";
    private static final String PROPERTY_KARAF_APPNAME = "karaf.appname";
    private static final String PROPERTY_KARAF_HOST = "karaf.host";
    private static final String PROPERTY_KARAF_PORT = "karaf.port";
    private static final String PROPERTY_KARAF_CMD = "karaf.cmd";
    private static final String PROPERTY_KARAF_CLIENT_CMD = "karaf.client.cmd";

    private final String distributionURI;
    private final String karafRoot;
    private final String karafAppname;
    private final String karafHost;
    private final Integer karafPort;
    private final File karafCmd;
    private final File karafClientCmd;

    public ContextConfiguration(String distributionURI, String karafRoot, String karafAppname, String karafHost,
            Integer karafPort, File karafCmd, File karafClientCmd) throws InvalidConfigurationException {
        this.distributionURI = distributionURI;
        this.karafRoot = karafRoot;
        this.karafAppname = karafAppname;
        this.karafHost = karafHost;
        this.karafPort = karafPort;
        this.karafCmd = karafCmd;
        this.karafClientCmd = karafClientCmd;

        checkConfiguration();
    }

    private void checkConfiguration() throws InvalidConfigurationException {
        String[] mandatory = { this.distributionURI };

        for (String m : mandatory) {
            if (m.isEmpty()) {
                throw new InvalidConfigurationException("A mandatory property is missing.");
            }
        }
    }

    public static ContextConfiguration loadFromProperties(Properties properties) throws InvalidConfigurationException {
        String distributionURI = properties.getProperty(PROPERTY_DISTRIBUTION_URI, "").trim();
        String karafRoot = properties.getProperty(PROPERTY_KARAF_ROOT, "").trim();
        String karafAppname = properties.getProperty(PROPERTY_KARAF_APPNAME, "").trim();
        String karafHost = properties.getProperty(PROPERTY_KARAF_HOST, "").trim();

        String karafPortString = null;
        Integer karafPort = null;
        karafPortString = properties.getProperty(PROPERTY_KARAF_PORT, "").trim();
        if (!karafPortString.isEmpty()) {
            try {
                karafPort = Integer.parseInt(karafPortString);
            } catch (NumberFormatException e) {
                throw new InvalidConfigurationException("Invalid value for property " + PROPERTY_KARAF_PORT + ": "
                        + karafPortString);
            }
        }

        File karafCmd = new File(properties.getProperty(PROPERTY_KARAF_CMD, "").trim());
        File karafClientCmd = new File(properties.getProperty(PROPERTY_KARAF_CLIENT_CMD, "").trim());

        return new ContextConfiguration(distributionURI, karafRoot, karafAppname, karafHost, karafPort, karafCmd,
                karafClientCmd);
    }

    public static ContextConfiguration loadFromFile(File file) throws FileNotFoundException, InvalidConfigurationException {
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

    public String getKarafRoot() {
        return karafRoot;
    }

    public String getKarafAppname() {
        return karafAppname;
    }

    public String getKarafHost() {
        return karafHost;
    }

    public Integer getKarafPort() {
        return karafPort;
    }

    public File getKarafCmd() {
        return karafCmd;
    }

    public File getKarafClientCmd() {
        return karafClientCmd;
    }
}

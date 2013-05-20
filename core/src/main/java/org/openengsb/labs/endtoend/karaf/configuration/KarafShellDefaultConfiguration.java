package org.openengsb.labs.endtoend.karaf.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KarafShellDefaultConfiguration {

    private static final String CONFIG_FILE_NAME = "org.apache.karaf.shell.cfg";

    private static final String PROPERTY_SSH_PORT = "sshPort";
    private static final String PROPERTY_SSH_HOST = "sshHost";

    private static final String DEFAULT_SSH_PORT = "";
    private static final String DEFAULT_SSH_HOST = "";

    private final Integer sshPort;
    private final String sshHost;

    public KarafShellDefaultConfiguration(Integer sshPort, String sshHost) {
        this.sshPort = sshPort;
        this.sshHost = sshHost;
    }

    public static KarafShellDefaultConfiguration loadFromEtcDir(File etcDir)
            throws InvalidKarafDefaultConfigurationException {
        etcDir = new File(etcDir, CONFIG_FILE_NAME);

        Properties properties = new Properties();
        try (InputStream stream = new FileInputStream(etcDir)) {
            properties.load(stream);
        } catch (FileNotFoundException e) {
            throw new InvalidKarafDefaultConfigurationException(e);
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading Karaf shell config file.", e);
        }

        return loadFromProperties(properties);
    }

    public static KarafShellDefaultConfiguration loadFromProperties(Properties properties)
            throws InvalidKarafDefaultConfigurationException {
        Integer sshPort = readIntegerProperty(properties, PROPERTY_SSH_PORT, DEFAULT_SSH_PORT);
        String sshHost = properties.getProperty(PROPERTY_SSH_HOST, DEFAULT_SSH_HOST).trim();

        return new KarafShellDefaultConfiguration(sshPort, sshHost);
    }

    private static Integer readIntegerProperty(Properties properties, String name, String defaultValue)
            throws InvalidKarafDefaultConfigurationException {
        Integer integerValue = null;
        String stringValue = properties.getProperty(name, defaultValue).trim();
        if (!stringValue.isEmpty()) {
            try {
                integerValue = Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                throw new InvalidKarafDefaultConfigurationException("Invalid value for integer property " + name + ": "
                        + stringValue);
            }
        }

        return integerValue;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public String getSshHost() {
        return sshHost;
    }
}

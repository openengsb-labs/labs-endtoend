package org.openengsb.labs.endtoend.karaf.configuration;

import java.io.File;

public class KarafConfiguration {
    private static final File DEFAULT_KARAF_DIR = new File("");
    private static final String DEFAULT_APPLICATION_NAME = "karaf";
    private static final File DEFAULT_CLIENT_START_CMD = new File("bin/client");
    private static final File DEFAULT_START_CMD = new File("bin/karaf");

    private static final File KARAF_ETC_DIR = new File("etc");

    private final File karafDir;
    private final String applicationName;
    private final String sshHost;
    private final Integer sshPort;
    private final File startCmd;
    private final File clientStartCmd;

    public KarafConfiguration(File karafDir, String applicationName, String sshHost, Integer sshPort, File startCmd,
            File clientStartCmd) {
        this.karafDir = karafDir;
        this.applicationName = applicationName;
        this.sshHost = sshHost;
        this.sshPort = sshPort;
        this.startCmd = startCmd;
        this.clientStartCmd = clientStartCmd;

    }

    public static KarafConfiguration createKarafConfigurartion(File karafDir, String applicationName, String sshHost,
            Integer sshPort, File startCmd, File clientStartCmd) throws InvalidKarafConfigurationException {

        if (karafDir.getAbsolutePath().isEmpty()) {
            karafDir = DEFAULT_KARAF_DIR;
        }

        KarafShellDefaultConfiguration karafShellConfiguration = null;
        try {
            karafShellConfiguration = KarafShellDefaultConfiguration.loadFromEtcDir(new File(karafDir, KARAF_ETC_DIR
                    .getPath()));
        } catch (InvalidKarafDefaultConfigurationException e) {
            throw new InvalidKarafConfigurationException(e);
        }

        if (applicationName.isEmpty()) {
            applicationName = DEFAULT_APPLICATION_NAME;
        }

        if (sshHost.isEmpty()) {
            sshHost = karafShellConfiguration.getSshHost();
        }

        if (null == sshPort) {
            sshPort = karafShellConfiguration.getSshPort();
        }

        if (startCmd.getPath().isEmpty()) {
            startCmd = DEFAULT_START_CMD;
        }
        startCmd = new File(karafDir, startCmd.getPath());

        if (clientStartCmd.getPath().isEmpty()) {
            clientStartCmd = DEFAULT_CLIENT_START_CMD;
        }
        clientStartCmd = new File(karafDir, clientStartCmd.getPath());

        return new KarafConfiguration(karafDir, applicationName, sshHost, sshPort, startCmd, clientStartCmd);
    }

    public File getKarafDir() {
        return karafDir;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getSshHost() {
        return sshHost;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public File getStartCmd() {
        return startCmd;
    }

    public File getClientStartCmd() {
        return clientStartCmd;
    }

    public static File getDefaultKarafDir() {
        return DEFAULT_KARAF_DIR;
    }

    public static String getDefaultApplicationName() {
        return DEFAULT_APPLICATION_NAME;
    }

    public static File getDefaultClientStartCmd() {
        return DEFAULT_CLIENT_START_CMD;
    }

    public static File getDefaultStartCmd() {
        return DEFAULT_START_CMD;
    }
}

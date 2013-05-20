package org.openengsb.labs.endtoend.karaf.configuration;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KarafShellDefaultConfiguration.class)
public class KarafConfigurationTests {

    private static final String SSH_HOST = "localhost";
    private static final Integer SSH_PORT = 8101;

    @Before
    public void setup() throws InvalidKarafDefaultConfigurationException {
        KarafShellDefaultConfiguration mockedDefaultConfig = new KarafShellDefaultConfiguration(SSH_PORT, SSH_HOST);

        PowerMockito.mockStatic(KarafShellDefaultConfiguration.class);
        PowerMockito.when(KarafShellDefaultConfiguration.loadFromEtcDir(Mockito.any(File.class))).thenReturn(
                mockedDefaultConfig);
    }

    @Test
    public void testMissingValuesShouldBeReplacedByDefaults() throws InvalidKarafConfigurationException {
        File karafDir = new File("");
        String applicationName = "";
        String sshHost = "";
        Integer sshPort = null;
        File startCmd = new File("");
        File clientStartCmd = new File("");

        KarafConfiguration karafConfiguration = KarafConfiguration.createKarafConfigurartion(karafDir, applicationName,
                sshHost, sshPort, startCmd, clientStartCmd);

        assertEquals(KarafConfiguration.getDefaultKarafDir(), karafConfiguration.getKarafDir());
        assertEquals(KarafConfiguration.getDefaultApplicationName(), karafConfiguration.getApplicationName());
        assertEquals(SSH_HOST, karafConfiguration.getSshHost());
        assertEquals(SSH_PORT, karafConfiguration.getSshPort());
        assertEquals(new File(KarafConfiguration.getDefaultKarafDir(), KarafConfiguration.getDefaultStartCmd()
                .getPath()), karafConfiguration.getStartCmd());
        assertEquals(new File(KarafConfiguration.getDefaultKarafDir(), KarafConfiguration.getDefaultClientStartCmd()
                .getPath()), karafConfiguration.getClientStartCmd());
    }

    @Test
    public void testValidValuesShouldBeKept() throws InvalidKarafConfigurationException {
        File karafDir = new File("krf");
        String applicationName = "openengsb()";
        String sshHost = "localhost";
        Integer sshPort = 8101;
        File startCmd = new File("bin/openengsb");
        File clientStartCmd = new File("bin/client");

        KarafConfiguration karafConfiguration = KarafConfiguration.createKarafConfigurartion(karafDir, applicationName,
                sshHost, sshPort, startCmd, clientStartCmd);

        assertEquals(karafDir, karafConfiguration.getKarafDir());
        assertEquals(applicationName, karafConfiguration.getApplicationName());
        assertEquals(sshHost, karafConfiguration.getSshHost());
        assertEquals(sshPort, karafConfiguration.getSshPort());
        assertEquals(new File(karafDir, startCmd.getPath()), karafConfiguration.getStartCmd());
        assertEquals(new File(karafDir, clientStartCmd.getPath()), karafConfiguration.getClientStartCmd());
    }
}

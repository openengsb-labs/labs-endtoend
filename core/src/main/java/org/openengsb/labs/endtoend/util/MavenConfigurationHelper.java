package org.openengsb.labs.endtoend.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.ops4j.pax.url.maven.commons.MavenConfiguration;
import org.ops4j.pax.url.maven.commons.MavenConfigurationImpl;
import org.ops4j.pax.url.maven.commons.MavenConstants;
import org.ops4j.pax.url.maven.commons.MavenSettings;
import org.ops4j.pax.url.maven.commons.MavenSettingsImpl;
import org.ops4j.pax.url.mvn.ServiceConstants;
import org.ops4j.util.property.PropertiesPropertyResolver;

public class MavenConfigurationHelper {

    /**
     * Load settings.xml file and apply custom properties.
     */
    public static MavenConfiguration getConfig(final File settingsFile, final Properties props) throws Exception {

        props.setProperty(ServiceConstants.PID + MavenConstants.PROPERTY_SETTINGS_FILE, settingsFile.toURI()
                .toASCIIString());

        final MavenConfigurationImpl config = new MavenConfigurationImpl(new PropertiesPropertyResolver(props),
                ServiceConstants.PID);

        final MavenSettings settings = new MavenSettingsImpl(settingsFile.toURI().toURL());

        config.setSettings(settings);

        return config;

    }

    /**
     * Discover maven home from executable on PATH, using conventions.
     */
    public static File getMavenHome() throws Exception {
        final String command;
        switch (OS.current()) {
        case LINUX:
        case MAC:
            command = "mvn";
            break;
        case WINDOWS:
            command = "mvn.bat";
            break;
        default:
            throw new IllegalStateException("invalid o/s");
        }
        String pathVar = System.getenv("PATH");
        String[] pathArray = pathVar.split(File.pathSeparator);
        for (String path : pathArray) {
            File file = new File(path, command);
            if (file.exists() && file.isFile() && file.canExecute()) {
                /** unwrap symbolic links */
                File exec = file.getCanonicalFile();
                /** assume ${maven.home}/bin/exec convention */
                File home = exec.getParentFile().getParentFile();
                return home;
            }
        }
        throw new IllegalStateException("Maven home not found.");
    }

    /**
     * Load default user configuration form user settings.xml with custom properties.
     */
    public static MavenConfiguration getUserConfig(final Properties props) throws Exception {
        return getConfig(getUserSettings(), props);
    }

    /**
     * Load default maven settings from user home.
     */
    public static File getUserSettings() throws IOException {
        return new File(System.getProperty("user.home"), ".m2/settings.xml");
    }

}

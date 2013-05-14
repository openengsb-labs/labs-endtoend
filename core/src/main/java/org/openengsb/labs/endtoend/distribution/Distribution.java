package org.openengsb.labs.endtoend.distribution;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.karaf.Karaf;
import org.openengsb.labs.endtoend.karaf.KarafService;

public class Distribution {
    private static final long DEFAULT_SHUTDOWN_TIMEOUT_SECONDS = 20L;

    private final ExtractedDistribution extractedDistribution;

    private final Karaf karaf;

    public Distribution(ExtractedDistribution extractedDistribution, String karafAppname, Integer karafPort,
            String karafCmd, String karafClientCmd) {
        this.extractedDistribution = extractedDistribution;
        String prefix = extractedDistribution.getDistributionDir() + File.separator;
        this.karaf = new KarafService(karafAppname, karafPort, prefix + karafCmd, prefix + karafClientCmd);
    }

    public Karaf getKaraf() {
        return this.karaf;
    }

    public void delete() throws IOException {
        try {
            this.karaf.shutdown(DEFAULT_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (CommandTimeoutException e) {
            // Karaf was forcefully killed.
        }

        this.extractedDistribution.delete();
    }
}

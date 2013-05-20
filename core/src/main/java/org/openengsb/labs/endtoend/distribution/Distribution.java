package org.openengsb.labs.endtoend.distribution;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.karaf.Karaf;

public class Distribution {
    private static final long DEFAULT_SHUTDOWN_TIMEOUT_SECONDS = 20L;

    private final ExtractedDistribution extractedDistribution;

    private final Karaf karaf;

    public Distribution(ExtractedDistribution extractedDistribution, Karaf karaf) {
        this.extractedDistribution = extractedDistribution;
        this.karaf = karaf;
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

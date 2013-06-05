package org.openengsb.labs.endtoend.distribution;

import org.openengsb.labs.endtoend.karaf.Karaf;

public class Distribution {
    private final ExtractedDistribution extractedDistribution;

    private final Karaf karaf;

    public Distribution(ExtractedDistribution extractedDistribution, Karaf karaf) {
        this.extractedDistribution = extractedDistribution;
        this.karaf = karaf;
    }

    public Karaf getKaraf() {
        return this.karaf;
    }

    public ExtractedDistribution getExtractedDistribution() {
        return extractedDistribution;
    }
}

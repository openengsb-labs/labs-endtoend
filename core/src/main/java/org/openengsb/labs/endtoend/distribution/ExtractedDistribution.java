package org.openengsb.labs.endtoend.distribution;

import java.io.File;

public class ExtractedDistribution {
    private final File distributionDir;

    public ExtractedDistribution(File distributionDir) {
        this.distributionDir = distributionDir;
    }

    public File getDistributionDir() {
        return distributionDir;
    }
}

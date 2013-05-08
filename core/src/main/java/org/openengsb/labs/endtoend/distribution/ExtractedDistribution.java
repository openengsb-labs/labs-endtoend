package org.openengsb.labs.endtoend.distribution;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ExtractedDistribution {
    private final File distributionDir;

    public ExtractedDistribution(File distributionDir) {
        this.distributionDir = distributionDir;
    }

    public void delete() throws IOException {
        FileUtils.deleteDirectory(distributionDir);
    }

    public File getDistributionDir() {
        return distributionDir;
    }
}

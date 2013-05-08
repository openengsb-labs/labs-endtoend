package org.openengsb.labs.testing.distribution;

import java.io.File;
import java.io.IOException;

import org.openengsb.labs.testing.testcontext.TestContextID;

public class Distribution {
    private final DistributionExtractor distributionExtractor;
    private final TestContextID testContextID;
    private final File distributionFile;

    public Distribution(DistributionExtractor distributionExtractor, TestContextID testContextID, File distributionFile) {
        this.distributionExtractor = distributionExtractor;
        this.testContextID = testContextID;
        this.distributionFile = distributionFile;
    }

    public ExtractedDistribution extract() throws IOException, UnsupportedArchiveTypeException {
        return this.distributionExtractor.getExtractedDistribution(this);
    }

    public TestContextID getTestContextID() {
        return testContextID;
    }

    public File getDistributionFile() {
        return distributionFile;
    }
}

package org.openengsb.labs.endtoend.testcontext;

import java.io.File;
import java.io.IOException;

import org.openengsb.labs.endtoend.api.Karaf;
import org.openengsb.labs.endtoend.distribution.Distribution;
import org.openengsb.labs.endtoend.distribution.ExtractedDistribution;
import org.openengsb.labs.endtoend.distribution.UnsupportedArchiveTypeException;
import org.openengsb.labs.endtoend.karaf.KarafService;

public class TestContext {
    private final Distribution distribution;
    private final String karafCmd;
    private final String karafClientCmd;

    private ExtractedDistribution extractedDistribution;

    public TestContext(Distribution distribution, String karafCmd, String karafClientCmd) {
        this.distribution = distribution;
        this.karafCmd = karafCmd;
        this.karafClientCmd = karafClientCmd;
    }

    public Karaf getKaraf() {
        String prefix = this.extractedDistribution.getDistributionDir() + File.separator;
        return new KarafService(prefix + this.karafCmd, prefix + this.karafClientCmd);
    }

    public void setup() throws IOException, UnsupportedArchiveTypeException {
        this.extractedDistribution = distribution.extract();
    }

    public void teardown() throws IOException {
        this.extractedDistribution.delete();
    }
}

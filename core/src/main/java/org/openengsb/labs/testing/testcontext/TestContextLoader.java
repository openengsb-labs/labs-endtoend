package org.openengsb.labs.testing.testcontext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openengsb.labs.testing.distribution.Distribution;
import org.openengsb.labs.testing.distribution.DistributionExtractor;
import org.openengsb.labs.testing.distribution.UnsupportedArchiveTypeException;

public class TestContextLoader {
    private static final String PROPERTY_DISTRIBUTION_FILE = "distribution.file";
    private static final String PROPERTY_KARAF_CMD = "karaf.cmd";
    private static final String PROPERTY_KARAF_CLIENT_CMD = "karaf.client.cmd";

    private final DistributionExtractor distributionExtractor;
    private final Map<TestContextID, TestContext> testContexts = new HashMap<TestContextID, TestContext>();

    public TestContextLoader(DistributionExtractor distributionExtractor) {
        this.distributionExtractor = distributionExtractor;
    }

    public void loadContexts(Set<File> configFiles) throws FileNotFoundException, IOException,
        UnsupportedArchiveTypeException {

        for (File file : configFiles) {
            Properties karafProperties = loadProperties(file);
            String karafCmd = karafProperties.getProperty(PROPERTY_KARAF_CMD);
            String karafClientCmd = karafProperties.getProperty(PROPERTY_KARAF_CMD);
            File distributionFile = new File(karafProperties.getProperty(PROPERTY_DISTRIBUTION_FILE));

            TestContextID testContextID = new TestContextID(file);
            Distribution distribution = createDistribution(testContextID, distributionFile);

            testContexts.put(testContextID, new TestContext(distribution, karafCmd, karafClientCmd));
        }
    }

    private Distribution createDistribution(TestContextID testContextID, File distributionFile) {
        return new Distribution(this.distributionExtractor, testContextID, distributionFile);
    }

    private Properties loadProperties(File file) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        FileInputStream stream = new FileInputStream(file);
        properties.load(stream);
        stream.close();
        return properties;
    }

    public TestContext getTestContext(TestContextID name) {
        return testContexts.get(name);
    }
}

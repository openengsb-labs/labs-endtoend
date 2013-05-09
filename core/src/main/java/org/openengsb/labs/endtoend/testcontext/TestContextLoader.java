package org.openengsb.labs.endtoend.testcontext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openengsb.labs.endtoend.distribution.Distribution;
import org.openengsb.labs.endtoend.distribution.DistributionExtractor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class TestContextLoader {
    private static final String FILE_ENDING = "properties";
    private static final String FILE_PREFIX = "endtoend";
    private static final Pattern PATTERN_FILE = Pattern.compile("^" + FILE_PREFIX + "(?:\\.(.*))?\\.(.*)\\.(.*)\\."
            + FILE_ENDING + "$");
    private static final String PROPERTY_DISTRIBUTION_FILE = "distribution.file";
    private static final String PROPERTY_KARAF_CMD = "karaf.cmd";
    private static final String PROPERTY_KARAF_CLIENT_CMD = "karaf.client.cmd";

    private final DistributionExtractor distributionExtractor;
    private final Map<TestContextID, TestContext> testContexts = new HashMap<TestContextID, TestContext>();

    public TestContextLoader(DistributionExtractor distributionExtractor) {
        this.distributionExtractor = distributionExtractor;
    }

    public void loadContexts() throws FileNotFoundException, IOException, InvalidContextFileFoundException {
        Set<File> endToEndFilesFromResources = getEndToEndFilesFromResources();
        loadContexts(endToEndFilesFromResources);
    }

    private Set<File> getEndToEndFilesFromResources() throws IOException {
        PathMatchingResourcePatternResolver res = new PathMatchingResourcePatternResolver();
        Resource[] resources = res.getResources(FILE_PREFIX + ".*.*." + FILE_ENDING);
        HashSet<File> files = new HashSet<File>();
        for (Resource r : resources) {
            files.add(r.getFile());
        }
        return files;
    }

    public void loadContexts(Set<File> configFiles) throws FileNotFoundException, IOException,
            InvalidContextFileFoundException {
        for (File file : configFiles) {
            Properties karafProperties = loadProperties(file);
            String karafCmd = karafProperties.getProperty(PROPERTY_KARAF_CMD);
            String karafClientCmd = karafProperties.getProperty(PROPERTY_KARAF_CLIENT_CMD);
            File distributionFile = new File(karafProperties.getProperty(PROPERTY_DISTRIBUTION_FILE));

            TestContextID testContextID = parseContextFilename(file.getName());
            Distribution distribution = createDistribution(testContextID, distributionFile);

            testContexts.put(testContextID, new TestContext(distribution, karafCmd, karafClientCmd));
        }
    }

    private TestContextID parseContextFilename(String filename) throws InvalidContextFileFoundException {
        Matcher m = PATTERN_FILE.matcher(filename);

        if (!m.find()) {
            throw new InvalidContextFileFoundException(filename);
        }

        return new TestContextID(m.group(1), m.group(2), m.group(3));
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

    public TestContext getTestContext(String contextName) {
        String osName = System.getProperty("os.name").toLowerCase().replace(" ", "");
        String osArch = System.getProperty("os.arch").toLowerCase();

        TestContextID id = new TestContextID(contextName, osName, osArch);
        return testContexts.get(id);
    }

    public TestContext getDefaultTestContext() throws NoContextFileForSystemFoundException {
        String osName = System.getProperty("os.name").toLowerCase().replace(" ", "");
        String osArch = System.getProperty("os.arch").toLowerCase();

        TestContextID testContextID = new TestContextID(osName, osArch);

        TestContext testContext = this.testContexts.get(testContextID);
        if (null == testContext) {
            throw new NoContextFileForSystemFoundException(osName, osArch);
        }

        return testContext;
    }
}

package org.openengsb.labs.endtoend.testcontext.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openengsb.labs.endtoend.distribution.extractor.DistributionExtractor;
import org.openengsb.labs.endtoend.distribution.resolver.DistributionResolver;
import org.openengsb.labs.endtoend.testcontext.TestContext;
import org.openengsb.labs.endtoend.testcontext.TestContextID;
import org.openengsb.labs.endtoend.testcontext.configuration.ContextConfiguration;
import org.openengsb.labs.endtoend.testcontext.configuration.InvalidConfiguration;
import org.openengsb.labs.endtoend.util.Arch;
import org.openengsb.labs.endtoend.util.OS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class TestContextLoader {
    private static final String FILE_ENDING = "properties";
    private static final String FILE_PREFIX = "endtoend";
    private static final Pattern PATTERN_FILE = Pattern.compile("^" + FILE_PREFIX + "(?:\\.(.*))?\\.(.*)\\.(.*)\\."
            + FILE_ENDING + "$");

    private final DistributionExtractor distributionExtractor;
    private final Map<TestContextID, TestContext> testContexts = new HashMap<TestContextID, TestContext>();
    private final DistributionResolver distributionResolver;

    public TestContextLoader(DistributionResolver distributionResolver, DistributionExtractor distributionExtractor) {
        this.distributionResolver = distributionResolver;
        this.distributionExtractor = distributionExtractor;
    }

    public void loadContexts() throws FileNotFoundException, IOException, InvalidContextFileFound {
        Set<File> endToEndFilesFromResources = getEndToEndFilesFromResources();
        loadContexts(endToEndFilesFromResources);
    }

    public void loadContexts(Set<File> configFiles) throws FileNotFoundException, IOException, InvalidContextFileFound {
        for (File file : configFiles) {
            ContextConfiguration contextConfiguration;
            TestContextID testContextID;

            try {
                contextConfiguration = ContextConfiguration.loadFromFile(file);
                testContextID = parseContextID(file.getName());
            } catch (InvalidConfiguration e) {
                throw new InvalidContextFileFound(file.getName(), e);
            } catch (InvalidContextFileName e) {
                throw new InvalidContextFileFound(file.getName(), e);
            }

            testContexts.put(testContextID, new TestContext(testContextID, this.distributionResolver,
                    this.distributionExtractor, contextConfiguration));
        }
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

    private TestContextID parseContextID(String filename) throws InvalidContextFileName {
        Matcher m = PATTERN_FILE.matcher(filename);

        if (!m.find()) {
            throw new InvalidContextFileName(filename);
        }

        return new TestContextID(m.group(1), OS.fromString(m.group(2)), Arch.fromString(m.group(3)));
    }

    public TestContext getTestContext(String contextName) {
        OS osName = OS.current();
        Arch osArch = Arch.current();

        TestContextID id = new TestContextID(contextName, osName, osArch);
        return testContexts.get(id);
    }

    public TestContext getDefaultTestContext() throws NoContextFileForSystemFoundException {
        OS osName = OS.current();
        Arch osArch = Arch.current();

        TestContextID testContextID = new TestContextID(osName, osArch);

        TestContext testContext = this.testContexts.get(testContextID);
        if (null == testContext) {
            throw new NoContextFileForSystemFoundException(osName, osArch);
        }

        return testContext;
    }
}

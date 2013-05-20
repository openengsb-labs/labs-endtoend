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
import org.openengsb.labs.endtoend.testcontext.configuration.InvalidConfigurationException;
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

    /**
     * Load all available contexts.
     * 
     * @exception IllegalStateException If the contexts could not be loaded.
     */
    public void loadContexts() {
        Set<File> endToEndFilesFromResources;
        try {
            endToEndFilesFromResources = getEndToEndFilesFromResources();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load contexts.", e);
        }
        loadContexts(endToEndFilesFromResources);
    }

    /**
     * Load only the given set of context files.
     * 
     * @param contextFiles The set of context file to be loaded.
     * @exception IllegalArgumentException If one of the given files has an invalid name.
     * 
     */
    public void loadContexts(Set<File> contextFiles) {
        for (File file : contextFiles) {
            ContextConfiguration contextConfiguration;
            TestContextID testContextID;

            try {
                testContextID = parseContextID(file.getName());
                contextConfiguration = ContextConfiguration.loadFromFile(file);
            } catch (InvalidConfigurationException e) {
                throw new IllegalStateException("Invalid configuration in file: " + file.getName(), e);
            } catch (InvalidContextFileName e) {
                throw new IllegalArgumentException("Invalid context file name given: " + file.getName());
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("No context file with given name found: " + file.getName(), e);
            }

            testContexts.put(testContextID, new TestContext(testContextID, this.distributionResolver,
                    this.distributionExtractor, contextConfiguration));
        }
    }

    private Set<File> getEndToEndFilesFromResources() throws IOException {
        PathMatchingResourcePatternResolver res = new PathMatchingResourcePatternResolver();
        Resource[] resources = res.getResources("classpath*:" + FILE_PREFIX + ".*.*." + FILE_ENDING);
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

    /**
     * Returns the context with the given name. Contexts have to be loaded with {@link #loadContexts()} or
     * {@link #loadContexts(Set)} beforehand.
     * 
     * @param contextName
     * @return Context with the given name.
     * @exception IllegalArgumentException If no context with the given name is available.
     */
    public TestContext getTestContext(String contextName) {
        OS osName = OS.current();
        Arch osArch = Arch.current();

        TestContextID id = new TestContextID(contextName, osName, osArch);
        TestContext testContext = testContexts.get(id);
        if (null == testContext) {
            throw new IllegalArgumentException("No context with this name available: " + contextName);
        }

        return testContext;
    }

    /**
     * Returns the default context for the current operating system and architecture. The corresponding file name has to
     * be of the form endtoend.*os*.*arch*.properties. For possible values of *os* and *arch* see the classes {@link OS}
     * and {@link Arch}. Contexts have to be loaded with {@link #loadContexts()} or {@link #loadContexts(Set)}
     * beforehand.
     * 
     * @return Default context for the current operating system and architecture.
     * @exception IllegalStateException If no context for the current operating system and architecture is available.
     */
    public TestContext getDefaultTestContext() {
        OS osName = OS.current();
        Arch osArch = Arch.current();

        TestContextID testContextID = new TestContextID(osName, osArch);

        TestContext testContext = this.testContexts.get(testContextID);
        if (null == testContext) {
            throw new IllegalStateException("No context for current operation system and architecture found: " + osName
                    + ", " + osArch);
        }

        return testContext;
    }
}

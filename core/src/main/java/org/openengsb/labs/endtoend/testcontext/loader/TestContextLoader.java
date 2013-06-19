package org.openengsb.labs.endtoend.testcontext.loader;

import com.google.common.base.Joiner;
import org.openengsb.labs.endtoend.distribution.extractor.DistributionExtractor;
import org.openengsb.labs.endtoend.distribution.resolver.DistributionResolver;
import org.openengsb.labs.endtoend.testcontext.TestContext;
import org.openengsb.labs.endtoend.testcontext.configuration.ContextConfiguration;
import org.openengsb.labs.endtoend.testcontext.configuration.InvalidConfigurationException;
import org.openengsb.labs.endtoend.util.Arch;
import org.openengsb.labs.endtoend.util.OS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestContextLoader {
    private static final String FILE_PREFIX = "endtoend";
    private static final String FILE_POSTFIX = "properties";

    private final DistributionExtractor distributionExtractor;
    private final DistributionResolver distributionResolver;

    private final TestContextNode testContexts = new TestContextNode();

    public TestContextLoader(DistributionResolver distributionResolver, DistributionExtractor distributionExtractor) {
        this.distributionResolver = distributionResolver;
        this.distributionExtractor = distributionExtractor;
    }

    /**
     * Load all available contexts.
     *
     * @throws IllegalStateException If the contexts could not be loaded.
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
     * @throws IllegalArgumentException If one of the given files has an invalid name.
     */
    public void loadContexts(Set<File> contextFiles) {
        for (File file : contextFiles) {
            ContextConfiguration contextConfiguration;

            String[] split = file.getName().split("\\.");
            if (split.length < 3 || !(split[0].equals(FILE_PREFIX)) || !(split[split.length - 1].equals(FILE_POSTFIX))) {
                throw new IllegalArgumentException("Wrong file format");
            }

            try {
                contextConfiguration = ContextConfiguration.loadFromFile(file);
            } catch (InvalidConfigurationException e) {
                throw new IllegalStateException("Invalid configuration in file: " + file.getName(), e);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("No context file with given name found: " + file.getName(), e);
            }

            List<String> id = Arrays.asList(split).subList(1, split.length - 1);

            testContexts.insertIntoTree(
                    id.iterator(),
                    new TestContext(Joiner.on("").join(id), this.distributionResolver, this.distributionExtractor, contextConfiguration)
            );
        }
    }

    private Set<File> getEndToEndFilesFromResources() throws IOException {
        PathMatchingResourcePatternResolver res = new PathMatchingResourcePatternResolver();
        Resource[] resources = res.getResources("classpath*:" + FILE_PREFIX + ".*.*." + FILE_POSTFIX);
        HashSet<File> files = new HashSet<File>();
        for (Resource r : resources) {
            files.add(r.getFile());
        }
        return files;
    }

    /**
     * Returns the context with the given name. Contexts have to be loaded with {@link #loadContexts()} or
     * {@link #loadContexts(Set)} beforehand.
     *
     * @param contextName
     * @return Context with the given name.
     * @throws IllegalArgumentException If no context with the given name is available.
     */
    public TestContext getTestContext(String contextName) {
        OS osName = OS.current();
        Arch osArch = Arch.current();

        TestContext testContext = testContexts.loadFromTree(
                Arrays.asList(new String[]{contextName, osName.toString(), osArch.toString()}).iterator()
        );

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
     * @throws IllegalStateException If no context for the current operating system and architecture is available.
     */
    public TestContext getDefaultTestContext() {
        OS osName = OS.current();
        Arch osArch = Arch.current();

        TestContext testContext = testContexts.loadFromTree(
                Arrays.asList(new String[]{osName.toString(), osArch.toString()}).iterator()
        );

        if (null == testContext) {
            throw new IllegalStateException("No context for current operation system and architecture found: " + osName
                    + ", " + osArch);
        }

        return testContext;
    }
}

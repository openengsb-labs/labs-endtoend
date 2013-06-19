package org.openengsb.labs.endtoend.testcontext;

import org.openengsb.labs.endtoend.distribution.Distribution;
import org.openengsb.labs.endtoend.distribution.ExtractedDistribution;
import org.openengsb.labs.endtoend.distribution.ResolvedDistribution;
import org.openengsb.labs.endtoend.distribution.extractor.DistributionExtractor;
import org.openengsb.labs.endtoend.distribution.resolver.DistributionResolver;
import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.karaf.ConfigurationManipulator;
import org.openengsb.labs.endtoend.karaf.Karaf;
import org.openengsb.labs.endtoend.karaf.KarafService;
import org.openengsb.labs.endtoend.karaf.configuration.InvalidKarafConfigurationException;
import org.openengsb.labs.endtoend.karaf.configuration.KarafConfiguration;
import org.openengsb.labs.endtoend.testcontext.configuration.ContextConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class TestContext {
    private static final long DEFAULT_SHUTDOWN_TIMEOUT_SECONDS = 30L;

    private String id;

    private final DistributionExtractor extractor;
    private final DistributionResolver resolver;

    private final ContextConfiguration configuration;

    private Distribution distribution;

    public TestContext(String id, DistributionResolver resolver, DistributionExtractor extractor, ContextConfiguration configuration) {
        this.id = id;
        this.resolver = resolver;
        this.extractor = extractor;
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    /**
     * Sets up the context. I.e. extracting the distribution, etc..
     *
     * @throws IllegalStateException        If context has already been set up.
     * @throws TestContextTeardownException If the context could not be set up.
     */
    public void setup(ConfigurationManipulator... configurations) {
        if (isSetup()) {
            throw new IllegalStateException("Context already setup.");
        }

        try {
            ResolvedDistribution resolvedDistribution;
            resolvedDistribution = this.resolver.resolveDistribution(this.configuration.getDistributionURI());

            ExtractedDistribution extractedDistribution = this.extractor.getExtractedDistribution(this,
                    resolvedDistribution);

            new ConfigurationManipulator() {
                @Override
                public void manipulate(File karafRootFolder) {
                    File file = new File(new File(karafRootFolder, "etc"), "system.properties");
                    Properties properties = new Properties();
                    try {
                        try (FileInputStream in = new FileInputStream(file)) {
                            properties.load(in);
                        }
                        properties.setProperty("jline.terminal", "none");
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            properties.store(out, null);
                        }
                    } catch (IOException e) {
                        throw new IllegalStateException("Jline terminal couldnt be turned off.");
                    }
                }
            }.manipulate(extractedDistribution.getDistributionDir());
            for (ConfigurationManipulator configurationManipulator : configurations) {
                configurationManipulator.manipulate(extractedDistribution.getDistributionDir());
            }

            Karaf karaf = createKarafService(extractedDistribution);
            this.distribution = new Distribution(extractedDistribution, karaf);
        } catch (Exception e) {
            throw new TestContextSetupException(e);
        }
    }

    private Karaf createKarafService(ExtractedDistribution extractedDistribution)
            throws InvalidKarafConfigurationException {

        File karafDir = new File(extractedDistribution.getDistributionDir(), this.configuration.getKarafRoot());

        KarafConfiguration karafConfigurartion = KarafConfiguration.createKarafConfigurartion(karafDir,
                this.configuration.getKarafAppname(), this.configuration.getKarafHost(),
                this.configuration.getKarafPort(), this.configuration.getKarafCmd(),
                this.configuration.getKarafClientCmd());

        return new KarafService(karafConfigurartion);
    }

    /**
     * Returns the distribution ready to be used.
     *
     * @return The distribution
     * @throws IllegalStateException If the context has not been set up yet.
     */
    public Distribution getDistribution() {
        if (!isSetup()) {
            throw new IllegalStateException("Context not yet set up.");
        }
        return this.distribution;
    }

    /**
     * Tears down the context. I.e. deleting the distribution, etc..
     *
     * @throws IllegalStateException        If context has not been set up yet.
     * @throws TestContextTeardownException If the context could not be teared down.
     */
    public void teardown() {
        if (!isSetup()) {
            throw new IllegalStateException("Context not yet set up.");
        }

        try {
            try {
                Karaf karaf = this.distribution.getKaraf();
                karaf.shutdown(DEFAULT_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (CommandTimeoutException e) {
                // Karaf was forcefully killed.
            }

            ExtractedDistribution extractedDistribution = this.distribution.getExtractedDistribution();
            this.extractor.deleteDistribution(extractedDistribution);
            this.distribution = null;
        } catch (Exception e) {
            throw new TestContextTeardownException(e);
        }
    }

    public boolean isSetup() {
        return null != this.distribution;
    }
}

package org.openengsb.labs.endtoend.testcontext;

import org.openengsb.labs.endtoend.distribution.Distribution;
import org.openengsb.labs.endtoend.distribution.ExtractedDistribution;
import org.openengsb.labs.endtoend.distribution.ResolvedDistribution;
import org.openengsb.labs.endtoend.distribution.extractor.DistributionExtractor;
import org.openengsb.labs.endtoend.distribution.resolver.DistributionResolver;
import org.openengsb.labs.endtoend.testcontext.configuration.ContextConfiguration;

public class TestContext {
    private final TestContextID id;

    private final DistributionExtractor extractor;
    private final DistributionResolver resolver;

    private final ContextConfiguration configuration;

    private Distribution distribution;

    public TestContext(TestContextID id, DistributionResolver resolver, DistributionExtractor extractor,
            ContextConfiguration configuration) {
        this.id = id;
        this.resolver = resolver;
        this.extractor = extractor;
        this.configuration = configuration;
    }

    /**
     * Sets up the context. I.e. extracting the distribution, etc..
     * 
     * @exception IllegalStateException If context has already been set up.
     * @exception TestContextTeardownException If the context could not be set up.
     */
    public void setup() {
        if (isSetup()) {
            throw new IllegalStateException("Context already setup.");
        }

        try {
            ResolvedDistribution resolvedDistribution;
            resolvedDistribution = this.resolver.resolveDistribution(this.configuration.getDistributionURI());

            ExtractedDistribution extractedDistribution = this.extractor.getExtractedDistribution(this,
                    resolvedDistribution);
            this.distribution = new Distribution(extractedDistribution, this.configuration.getKarafAppname(),
                    this.configuration.getKarafPort(), this.configuration.getKarafCmd(),
                    this.configuration.getKarafClientCmd());
        } catch (Exception e) {
            throw new TestContextSetupException(e);
        }
    }

    /**
     * Returns the distribution ready to be used.
     * 
     * @return The distribution
     * @exception IllegalStateException If the context has not been set up yet.
     */
    public Distribution getDistribution() {
        if (!isSetup()) {
            throw new IllegalStateException("Context not yet set up.");
        }
        return this.distribution;
    }

    /**
     * Thears down the context. I.e. deleting the distribution, etc..
     * 
     * @exception IllegalStateException If context has not been set up yet.
     * @exception TestContextTeardownException If the context could not be teared down.
     */
    public void teardown() {
        if (!isSetup()) {
            throw new IllegalStateException("Context not yet set up.");
        }

        try {
            this.distribution.delete();
        } catch (Exception e) {
            throw new TestContextTeardownException(e);
        }
    }

    public TestContextID getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestContext other = (TestContext) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

    public boolean isSetup() {
        return null != this.distribution;
    }
}

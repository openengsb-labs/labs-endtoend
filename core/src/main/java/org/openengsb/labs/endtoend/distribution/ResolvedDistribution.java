package org.openengsb.labs.endtoend.distribution;

import java.io.File;

public class ResolvedDistribution {
    private final File distributionFile;

    public ResolvedDistribution(File distributionFile) {
        this.distributionFile = distributionFile;
    }

    public File getDistributionFile() {
        return distributionFile;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((distributionFile == null) ? 0 : distributionFile.hashCode());
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
        ResolvedDistribution other = (ResolvedDistribution) obj;
        if (distributionFile == null) {
            if (other.distributionFile != null)
                return false;
        } else if (!distributionFile.equals(other.distributionFile))
            return false;
        return true;
    }
}

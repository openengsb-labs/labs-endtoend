package org.openengsb.labs.testing.distribution;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.taskdefs.Untar;
import org.apache.tools.ant.taskdefs.Untar.UntarCompressionMethod;

public class DistributionExtractor {
    private static final String TAR_GZ = "tar.gz";
    private final Map<Distribution, ExtractedDistribution> uncompressedDistributions =
        new HashMap<Distribution, ExtractedDistribution>();
    private final File destinationDir;

    public DistributionExtractor(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    public ExtractedDistribution getExtractedDistribution(Distribution distribution) throws IOException,
        UnsupportedArchiveTypeException {
        ExtractedDistribution extractedDistribution = this.uncompressedDistributions.get(distribution);
        if (null == extractedDistribution) {
            File distributionDir =
                new File(this.destinationDir, distribution.getTestContextID().toString());
            uncompress(distribution.getDistributionFile(), distributionDir);
            extractedDistribution = new ExtractedDistribution(distributionDir);
            this.uncompressedDistributions.put(distribution, extractedDistribution);
        }

        return extractedDistribution;
    }

    private void uncompress(File distributionFile, File destinationDir) throws IOException,
        UnsupportedArchiveTypeException {

        if (distributionFile.getName().toLowerCase().endsWith(TAR_GZ)) {
            uncompressTarGZ(distributionFile, destinationDir);
        } else {
            throw new UnsupportedArchiveTypeException();
        }
    }

    private void uncompressTarGZ(File tarFile, File dest) throws IOException {
        Untar untar = new Untar();
        untar.setSrc(tarFile);
        untar.setDest(dest);
        UntarCompressionMethod compression = new UntarCompressionMethod();
        compression.setValue("gzip");
        untar.setCompression(compression);
        untar.setOverwrite(true);
        untar.execute();
    }

}

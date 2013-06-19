package org.openengsb.labs.endtoend.distribution.extractor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openengsb.labs.endtoend.distribution.ExtractedDistribution;
import org.openengsb.labs.endtoend.distribution.ResolvedDistribution;
import org.openengsb.labs.endtoend.testcontext.TestContext;
import org.openengsb.labs.endtoend.util.BinaryKey;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class DistributionExtractor {

    private final BiMap<BinaryKey<TestContext, ResolvedDistribution>, ExtractedDistribution> uncompressedDistributions = HashBiMap
            .create();
    private final File destinationRoot;

    public DistributionExtractor(File destinationRoot) {
        this.destinationRoot = destinationRoot;
    }

    public ExtractedDistribution getExtractedDistribution(TestContext testContext, ResolvedDistribution distribution)
            throws UnsupportedArchiveTypeException, DistributionExtractionException {

        BinaryKey<TestContext, ResolvedDistribution> key = new BinaryKey<TestContext, ResolvedDistribution>(
                testContext, distribution);

        ExtractedDistribution extractedDistribution = this.uncompressedDistributions.get(key);
        if (null == extractedDistribution) {
            String destinationDir = testContext.getId().toString();
            File distributionDir = new File(this.destinationRoot, destinationDir);

            try {
                extractDistribution(distribution.getDistributionFile().toURI().toURL(), distributionDir);
            } catch (MalformedURLException e) {
                throw new DistributionExtractionException(e);
            } catch (IOException e) {
                throw new DistributionExtractionException(e);
            }

            // makeScriptsInBinExec(new File(distributionDir + "/bin"));

            extractedDistribution = new ExtractedDistribution(distributionDir);
            this.uncompressedDistributions.put(key, extractedDistribution);
        }

        return extractedDistribution;
    }

    public void deleteDistribution(ExtractedDistribution distribution) throws IOException {
        File distributionDir = distribution.getDistributionDir();
        this.uncompressedDistributions.inverse().remove(distribution);
        FileUtils.deleteDirectory(distributionDir);
    }

    private void extractDistribution(URL sourceDistribution, File targetFolder) throws IOException, UnsupportedArchiveTypeException {
        if (sourceDistribution.getProtocol().equals("file")) {
            if (sourceDistribution.getFile().indexOf(".zip") > 0) {
                extractZipDistribution(sourceDistribution, targetFolder);
            } else if (sourceDistribution.getFile().indexOf(".tar.gz") > 0) {
                extractTarGzDistribution(sourceDistribution, targetFolder);
            } else {
                throw new IllegalStateException(
                        "Unknow packaging of distribution; only zip or tar.gz could be handled.");
            }
            return;
        }
        if (sourceDistribution.toExternalForm().indexOf("/zip") > 0) {
            extractZipDistribution(sourceDistribution, targetFolder);
        } else if (sourceDistribution.toExternalForm().indexOf("/tar.gz") > 0) {
            extractTarGzDistribution(sourceDistribution, targetFolder);
        } else {
            throw new UnsupportedArchiveTypeException("Unknow packaging of distribution; only zip or tar.gz could be handled.");
        }
    }

    private void extract(ArchiveInputStream is, File targetDir) throws IOException {
        try {
            if (targetDir.exists()) {
                FileUtils.forceDelete(targetDir);
            }
            targetDir.mkdirs();
            ArchiveEntry entry = is.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                name = name.substring(name.indexOf("/") + 1);
                File file = new File(targetDir, name);
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    OutputStream os = new FileOutputStream(file);
                    try {
                        IOUtils.copy(is, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
                entry = is.getNextEntry();
            }
        } finally {
            is.close();
        }
    }

    private void extractTarGzDistribution(URL sourceDistribution, File targetFolder) throws IOException,
            FileNotFoundException {
        File uncompressedFile = File.createTempFile("uncompressedTarGz-", ".tar");
        extractGzArchive(sourceDistribution.openStream(), uncompressedFile);
        extract(new TarArchiveInputStream(new FileInputStream(uncompressedFile)), targetFolder);
        FileUtils.forceDelete(uncompressedFile);
    }

    private void extractZipDistribution(URL sourceDistribution, File targetFolder) throws IOException {
        extract(new ZipArchiveInputStream(sourceDistribution.openStream()), targetFolder);
    }

    private void extractGzArchive(InputStream tarGz, File tar) throws IOException {
        BufferedInputStream in = new BufferedInputStream(tarGz);
        FileOutputStream out = new FileOutputStream(tar);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
        final byte[] buffer = new byte[1000];
        int n = 0;
        while (-1 != (n = gzIn.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.close();
        gzIn.close();
    }

    private void makeScriptsInBinExec(File karafBin) {
        if (!karafBin.exists()) {
            return;
        }
        File[] files = karafBin.listFiles();
        for (File file : files) {
            file.setExecutable(true);
        }
    }
}

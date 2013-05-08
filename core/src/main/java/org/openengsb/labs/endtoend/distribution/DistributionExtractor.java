package org.openengsb.labs.endtoend.distribution;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DistributionExtractor {
    private final Map<Distribution, ExtractedDistribution> uncompressedDistributions = new HashMap<Distribution, ExtractedDistribution>();
    private final File destinationDir;

    public DistributionExtractor(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    public ExtractedDistribution getExtractedDistribution(Distribution distribution) throws IOException,
            UnsupportedArchiveTypeException {
        ExtractedDistribution extractedDistribution = this.uncompressedDistributions.get(distribution);
        if (null == extractedDistribution) {
            File distributionDir = new File(this.destinationDir, distribution.getTestContextID().toString());
            extractDistribution(distribution.getDistributionFile().toURI().toURL(), distributionDir);
            // makeScriptsInBinExec(new File(distributionDir + "/bin"));
            extractedDistribution = new ExtractedDistribution(distributionDir);
            this.uncompressedDistributions.put(distribution, extractedDistribution);
        }

        return extractedDistribution;
    }

    private void extractDistribution(URL sourceDistribution, File targetFolder) throws IOException {
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
            throw new IllegalStateException("Unknow packaging of distribution; only zip or tar.gz could be handled.");
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

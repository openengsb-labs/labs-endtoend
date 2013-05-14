package org.openengsb.labs.endtoend.distribution.resolver;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openengsb.labs.endtoend.distribution.ResolvedDistribution;
import org.openengsb.labs.endtoend.util.MavenConfigurationHelper;
import org.ops4j.pax.url.mvn.internal.AetherBasedResolver;

public class DistributionResolver {
    private static Pattern mvnVlessPattern = Pattern.compile("^mvn-vless:(.+):(.+):(.*):(.*)$");

    private final AetherBasedResolver aetherBasedResolver;

    public DistributionResolver() {
        try {
            this.aetherBasedResolver = new AetherBasedResolver(MavenConfigurationHelper.getUserConfig(new Properties()));
        } catch (Exception e) {
            throw new IllegalStateException("AetherBasedResolver could not be instantiated.", e);
        }
    }

    public ResolvedDistribution resolveDistribution(String distributionUrl) throws InvalidDistributionURI,
            DistributionResolveException {
        Matcher m = mvnVlessPattern.matcher(distributionUrl);
        if (!m.find()) {
            throw new InvalidDistributionURI(distributionUrl);
        }

        String groupId = m.group(1);
        String articleId = m.group(2);
        String type = m.group(3);
        String classifier = m.group(4);

        String version = readVersion();

        File distributionFile;
        try {
            distributionFile = aetherBasedResolver.resolveFile(groupId, articleId, classifier, type, version);
        } catch (IOException e) {
            throw new DistributionResolveException(e);
        }

        return new ResolvedDistribution(distributionFile);
    }

    private String readVersion() {
        return "3.0.0-SNAPSHOT"; // TODO Read real version.
    }
}

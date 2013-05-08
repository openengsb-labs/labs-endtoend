package org.openengsb.labs.testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.testing.api.Karaf;
import org.openengsb.labs.testing.api.RemoteShell;
import org.openengsb.labs.testing.distribution.DistributionExtractor;
import org.openengsb.labs.testing.distribution.UnsupportedArchiveTypeException;
import org.openengsb.labs.testing.karaf.KarafException;
import org.openengsb.labs.testing.testcontext.TestContext;
import org.openengsb.labs.testing.testcontext.TestContextID;
import org.openengsb.labs.testing.testcontext.TestContextLoader;

public class App {
    private static final String PROPERTY_FILE_MAC = getCurrentDir()
            + "/test_contexts/karaf/context_karaf_mac.properties";
    private static final String EXTRACTION_DIR = getCurrentDir()
            + "/tmp";

    public static void main(String[] args) throws KarafException, FileNotFoundException, IOException,
        UnsupportedArchiveTypeException {

        DistributionExtractor ds = new DistributionExtractor(new File(EXTRACTION_DIR));

        TestContextLoader testContextLoader = new TestContextLoader(ds);

        File propertyFileMac = new File(PROPERTY_FILE_MAC);
        testContextLoader
            .loadContexts(new HashSet<File>(Arrays.asList(new File[]{ propertyFileMac })));

        TestContext context = testContextLoader.getTestContext(new TestContextID(propertyFileMac));
        context.setup();

        Karaf k = context.getKaraf();
        try {
            System.out.println("Starting Karaf...");
            k.start(10L, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RemoteShell shell = null;
        try {
            System.out.println("Remote login...");
            shell = k.login("root", "pw", 10L,
                TimeUnit.SECONDS);
            System.out.println("Executing list command...");
            String response = shell.execute("list",
                10L, TimeUnit.SECONDS);
            System.out.println(response);
            System.out.println("Logout...");
            shell.logout();
        } catch (TimeoutException e1) {
            e1.printStackTrace();
        }

        try {
            System.out.println("Stopping Karaf...");
            k.shutdown(10L, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        context.teardown();

        System.out.println("Finished.");

    }

    private static File getCurrentDir() {
        String dir = System.getProperty("user.dir");
        return new File(dir);
    }
}

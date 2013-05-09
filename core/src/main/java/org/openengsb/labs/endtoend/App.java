package org.openengsb.labs.endtoend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.api.Karaf;
import org.openengsb.labs.endtoend.api.RemoteShell;
import org.openengsb.labs.endtoend.distribution.DistributionExtractor;
import org.openengsb.labs.endtoend.distribution.UnsupportedArchiveTypeException;
import org.openengsb.labs.endtoend.karaf.KarafException;
import org.openengsb.labs.endtoend.testcontext.InvalidContextFileFoundException;
import org.openengsb.labs.endtoend.testcontext.NoContextFileForSystemFoundException;
import org.openengsb.labs.endtoend.testcontext.TestContext;
import org.openengsb.labs.endtoend.testcontext.TestContextLoader;

public class App {
    private static final String EXTRACTION_DIR = getCurrentDir() + "/tmp";

    public static void main(String[] args) throws KarafException, FileNotFoundException, IOException,
            UnsupportedArchiveTypeException, NoContextFileForSystemFoundException, InvalidContextFileFoundException {
        new App().exampleTest();
    }

    private static File getCurrentDir() {
        String dir = System.getProperty("user.dir");
        return new File(dir);
    }

    private void exampleTest() throws FileNotFoundException, IOException, InvalidContextFileFoundException,
            NoContextFileForSystemFoundException, UnsupportedArchiveTypeException, KarafException {
        DistributionExtractor ds = new DistributionExtractor(new File(EXTRACTION_DIR));

        TestContextLoader testContextLoader = new TestContextLoader(ds);
        testContextLoader.loadContexts();
        TestContext context;

        System.out.println("Run some tests on default context:");
        context = testContextLoader.getDefaultTestContext();
        runTestsWithContext(context);

        System.out.println("Run the same tests on a different context:");
        context = testContextLoader.getTestContext("somethingspecial");
        runTestsWithContext(context);
    }

    private void runTestsWithContext(TestContext context) throws IOException, UnsupportedArchiveTypeException,
            KarafException {
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
            shell = k.login("karaf", "karaf", 10L, TimeUnit.SECONDS);
            System.out.println("Executing list command...");
            String response = shell.execute("list", 10L, TimeUnit.SECONDS);
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
}

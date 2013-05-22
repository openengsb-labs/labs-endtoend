package org.openengsb.labs.endtoend;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openengsb.labs.endtoend.distribution.extractor.DistributionExtractor;
import org.openengsb.labs.endtoend.distribution.resolver.DistributionResolver;
import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.karaf.Karaf;
import org.openengsb.labs.endtoend.karaf.shell.RemoteShell;
import org.openengsb.labs.endtoend.karaf.shell.Shell;
import org.openengsb.labs.endtoend.testcontext.TestContext;
import org.openengsb.labs.endtoend.testcontext.loader.TestContextLoader;

public class App {
    private static final String EXTRACTION_DIR = getCurrentDir() + "/tmp";

    public static void main(String[] args) throws Exception {
        new App().exampleTest();
    }

    private static File getCurrentDir() {
        String dir = System.getProperty("user.dir");
        return new File(dir);
    }

    private void exampleTest() {
        DistributionResolver dr = new DistributionResolver();
        DistributionExtractor ds = new DistributionExtractor(new File(EXTRACTION_DIR));

        TestContextLoader testContextLoader = new TestContextLoader(dr, ds);
        testContextLoader.loadContexts();
        TestContext context;

        System.out.println("Run some tests on default context:");
        context = testContextLoader.getDefaultTestContext();
        runTestsWithContext(context);

        System.out.println("Run the same tests on a different context:");
        context = testContextLoader.getTestContext("somethingspecial");
        runTestsWithContext(context);
    }

    private void runTestsWithContext(TestContext context) {
        context.setup();

        Karaf k = context.getDistribution().getKaraf();
        try {
            System.out.println("Starting Karaf...");
            k.start(2L, TimeUnit.MINUTES);
        } catch (CommandTimeoutException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Executing list command (local shell)...");
        Shell shell = k.getShell();
        try {
            String response = null;
            response = shell.execute("list", 120L, TimeUnit.SECONDS);
            System.out.println(response);
        } catch (CommandTimeoutException e) {
            System.out.println(e.getMessage());
        }

        RemoteShell remoteShell = null;
        try {
            System.out.println("Remote login...");
            remoteShell = k.login("karaf", "", 120L, TimeUnit.SECONDS);
            System.out.println("Executing list command (remote shell)...");
            String response = remoteShell.execute("list", 10L, TimeUnit.SECONDS);
            System.out.println(response);
            System.out.println("Logout...");
            remoteShell.logout(10L, TimeUnit.SECONDS);
        } catch (CommandTimeoutException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println("Stopping Karaf...");
            k.shutdown(10L, TimeUnit.SECONDS);
        } catch (CommandTimeoutException e) {
            System.out.println(e.getMessage());
        }

        context.teardown();

        System.out.println("Finished.");
    }
}

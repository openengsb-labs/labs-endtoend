package org.openengsb.labs.endtoend;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openengsb.labs.endtoend.distribution.extractor.DistributionExtractor;
import org.openengsb.labs.endtoend.distribution.resolver.DistributionResolver;
import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.karaf.Karaf;
import org.openengsb.labs.endtoend.karaf.shell.RemoteShell;
import org.openengsb.labs.endtoend.karaf.shell.Shell;
import org.openengsb.labs.endtoend.testcontext.TestContext;
import org.openengsb.labs.endtoend.testcontext.loader.TestContextLoader;

public class ExecuteListCommandTest {
    private static final String LIST_COMMAND = "list";
    private static final String EXTRACTION_DIR = System.getProperty("java.io.tmpdir");
    private static final Long DEFAULT_TIMEOUT = 2L;
    private static final TimeUnit MINUTES = TimeUnit.MINUTES;

    private static TestContextLoader testContextLoader;
    private static TestContext defaultContext;
    private static TestContext somethingspecialContext;

    @BeforeClass
    public static void setupBeforeClass() {
        DistributionResolver dr = new DistributionResolver();
        DistributionExtractor ds = new DistributionExtractor(new File(EXTRACTION_DIR));

        testContextLoader = new TestContextLoader(dr, ds);
        testContextLoader.loadContexts();

        defaultContext = testContextLoader.getDefaultTestContext();
        somethingspecialContext = testContextLoader.getTestContext("somethingspecial");
    }

    @Test
    public void executeListCommandInLocalShellWithDefaultContext() throws CommandTimeoutException {
        executeListCommandInLocalShell(defaultContext);
    }

    @Test
    public void executeListCommandInRemoteShellWithDefaultContext() throws CommandTimeoutException {
        executeListCommandinRemoteShell(defaultContext);
    }

    @Test
    public void executeListCommandInLocalShellWithSomethingSpecialContext() throws CommandTimeoutException {
        executeListCommandInLocalShell(somethingspecialContext);
    }

    @Test
    public void executeListCommandInRemoteShellWithSomethingSpecialContext() throws CommandTimeoutException {
        executeListCommandinRemoteShell(somethingspecialContext);
    }

    private void executeListCommandInLocalShell(TestContext context) throws CommandTimeoutException {
        context.setup();

        Karaf k = context.getDistribution().getKaraf();
        k.start(DEFAULT_TIMEOUT, MINUTES);

        Shell shell = k.getShell();
        shell.execute(LIST_COMMAND, DEFAULT_TIMEOUT, MINUTES);

        k.shutdown(DEFAULT_TIMEOUT, MINUTES);

        context.teardown();
    }

    private void executeListCommandinRemoteShell(TestContext context) throws CommandTimeoutException {
        context.setup();

        Karaf k = context.getDistribution().getKaraf();
        k.start(DEFAULT_TIMEOUT, MINUTES);

        RemoteShell remoteShell = k.login("karaf", "", DEFAULT_TIMEOUT, MINUTES);
        remoteShell.execute(LIST_COMMAND, DEFAULT_TIMEOUT, MINUTES);

        k.shutdown(DEFAULT_TIMEOUT, MINUTES);

        context.teardown();
    }
}

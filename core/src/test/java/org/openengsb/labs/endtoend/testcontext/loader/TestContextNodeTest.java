package org.openengsb.labs.endtoend.testcontext.loader;

import org.junit.Test;
import org.openengsb.labs.endtoend.testcontext.TestContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TestContextNodeTest {

    @Test
    public void testFindsInsertedElementOnSameHierarchyAgain() throws Exception {
        List<String> path = Arrays.asList(new String[]{"first", "second", "third"});
        TestContext testContext = new TestContext(null, null, null, null);

        TestContextNode testContextNode = new TestContextNode();
        testContextNode.insertIntoTree(path.iterator(), testContext);
        TestContext foundSearchContext = testContextNode.loadFromTree(path.iterator());

        assertThat(foundSearchContext, is(testContext));
    }

    @Test
    public void testFindsElementOnHigherHierarchyAgain() throws Exception {
        List<String> pathInsert = Arrays.asList(new String[]{"first", "second"});
        List<String> pathQuery = Arrays.asList(new String[]{"first", "second", "third"});
        TestContext testContext = new TestContext(null, null, null, null);

        TestContextNode testContextNode = new TestContextNode();
        testContextNode.insertIntoTree(pathInsert.iterator(), testContext);
        TestContext foundSearchContext = testContextNode.loadFromTree(pathQuery.iterator());

        assertThat(foundSearchContext, is(testContext));
    }

    @Test
    public void testDoesNotFindElementsOnLowerHierarchyAgain() throws Exception {
        List<String> pathInsert = Arrays.asList(new String[]{"first", "second", "third"});
        List<String> pathQuery = Arrays.asList(new String[]{"first", "second"});
        TestContext testContext = new TestContext(null, null, null, null);

        TestContextNode testContextNode = new TestContextNode();
        testContextNode.insertIntoTree(pathInsert.iterator(), testContext);
        TestContext foundSearchContext = testContextNode.loadFromTree(pathQuery.iterator());

        assertThat(foundSearchContext, is(nullValue()));
    }

}

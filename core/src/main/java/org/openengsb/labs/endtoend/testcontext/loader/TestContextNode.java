package org.openengsb.labs.endtoend.testcontext.loader;

import org.openengsb.labs.endtoend.testcontext.TestContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestContextNode {

    private String context;
    private Map<String, TestContextNode> childNodes = new HashMap<>();
    private TestContext testContext;

    public void insertIntoTree(Iterator<String> hierarchy, TestContext context) {
        if (!hierarchy.hasNext()) {
            this.testContext = context;
            return;
        }
        String next = hierarchy.next();
        if (!childNodes.containsKey(next)) {
            childNodes.put(next, new TestContextNode());
        }
        childNodes.get(next).insertIntoTree(hierarchy, context);
    }

    public TestContext loadFromTree(Iterator<String> hierarchy) {
        if (!hierarchy.hasNext()) {
            return testContext;
        }

        String next = hierarchy.next();
        if (!childNodes.containsKey(next)) {
            return testContext;
        }

        TestContext loadedContext = childNodes.get(next).loadFromTree(hierarchy);
        if (loadedContext != null) {
            return loadedContext;
        }

        return testContext;
    }


}

package org.openengsb.labs.endtoend.testcontext;

public class TestContextTeardownException extends RuntimeException {

    private static final long serialVersionUID = -3839466901736264765L;

    public TestContextTeardownException(Throwable e) {
        super(e);
    }

}

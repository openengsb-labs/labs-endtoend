package org.openengsb.labs.endtoend.distribution.extractor;

public class UnsupportedArchiveTypeException extends Exception {
    private static final long serialVersionUID = 187779926740911996L;

    public UnsupportedArchiveTypeException(String message) {
        super(message);
    }
}

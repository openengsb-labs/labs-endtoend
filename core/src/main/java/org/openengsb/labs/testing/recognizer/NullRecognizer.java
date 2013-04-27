package org.openengsb.labs.testing.recognizer;


public class NullRecognizer implements Recognizer {
    @Override
    public boolean recognize(String input) {
        return false;
    }
}

package org.openengsb.labs.endtoend.recognizer;


public class NullRecognizer implements Recognizer {
    @Override
    public boolean recognize(String input) {
        return false;
    }
}

package org.openengsb.labs.testing.recognizer;


public class StringRecognizer implements Recognizer {

    private final String recognitionString;

    public StringRecognizer(String recognitionString) {
        this.recognitionString = recognitionString;
    }

    @Override
    public boolean recognize(String input) {
        return input.contains(recognitionString);
    }
}

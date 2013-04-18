package org.openengsb.testing.service;

public class OrRecognizer implements Recognizer {

    private final Recognizer recognizer1;
    private final Recognizer recognizer2;

    public OrRecognizer(Recognizer recognizer1, Recognizer recognizer2) {
        this.recognizer1 = recognizer1;
        this.recognizer2 = recognizer2;
    }

    @Override
    public boolean recognize(String input) {
        return recognizer1.recognize(input) || recognizer2.recognize(input);
    }

}

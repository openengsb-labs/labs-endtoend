package org.openengsb.labs.testing.recognizer;


public class AndRecognizer implements Recognizer {

    private final Recognizer recognizer1;
    private final Recognizer recognizer2;

    public AndRecognizer(Recognizer recognizer1, Recognizer recognizer2) {
        this.recognizer1 = recognizer1;
        this.recognizer2 = recognizer2;
    }

    @Override
    public boolean recognize(String input) {
        return recognizer1.recognize(input) && recognizer2.recognize(input);
    }

}

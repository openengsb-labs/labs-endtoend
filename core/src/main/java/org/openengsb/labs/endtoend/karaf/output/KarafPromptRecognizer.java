package org.openengsb.labs.endtoend.karaf.output;

import org.openengsb.labs.endtoend.recognizer.Recognizer;

public class KarafPromptRecognizer implements Recognizer {
    private final String prompt;

    public KarafPromptRecognizer(String username, String application) {
        this.prompt = username + "@" + application + "> ";
    }

    @Override
    public boolean recognize(String input) {
        return input.endsWith(this.prompt);
    }

    public String getPrompt() {
        return prompt;
    }
}

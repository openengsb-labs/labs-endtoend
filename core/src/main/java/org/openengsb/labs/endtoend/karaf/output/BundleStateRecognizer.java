package org.openengsb.labs.endtoend.karaf.output;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.openengsb.labs.endtoend.recognizer.Recognizer;

public class BundleStateRecognizer implements Recognizer {

    private String bundleName;
    private String state;
    
    public BundleStateRecognizer(String bundleName, String state) {
        this.bundleName = bundleName;
        this.state = state;
    }
    
    @Override
    public boolean recognize(String input) {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        try {
            String line = reader.readLine();
            Pattern pattern = Pattern.compile("] ");
            while (line != null) {
                if (line.startsWith("[")) {
                    String[] tokens = pattern.split(line);
                    if (tokens[3].startsWith(bundleName)) {
                        return state.equals(tokens[1].substring(1).trim());
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException ex) { 
        }
        return false;
    }

}

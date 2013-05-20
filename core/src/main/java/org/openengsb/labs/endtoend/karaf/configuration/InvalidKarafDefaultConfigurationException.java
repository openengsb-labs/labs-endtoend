package org.openengsb.labs.endtoend.karaf.configuration;

import java.io.FileNotFoundException;

public class InvalidKarafDefaultConfigurationException extends Exception {

    private static final long serialVersionUID = -7821496121029191307L;

    public InvalidKarafDefaultConfigurationException(String message) {
        super(message);
    }

    public InvalidKarafDefaultConfigurationException(FileNotFoundException e) {
        super(e);
    }

}

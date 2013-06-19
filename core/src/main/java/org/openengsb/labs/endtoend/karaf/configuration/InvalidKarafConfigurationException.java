package org.openengsb.labs.endtoend.karaf.configuration;

public class InvalidKarafConfigurationException extends Exception {

    private static final long serialVersionUID = -7821496121029191307L;

    public InvalidKarafConfigurationException(InvalidKarafDefaultConfigurationException e) {
        super(e);
    }

}

package org.openengsb.labs.endtoend.karaf;

import java.io.File;

/**
 * Provides a simple entry point for manipulating configuration and other files in the distribution
 */
public interface ConfigurationManipulator {

    /**
     * Allows the test class to manipulate a specific test folder.
     */
    void manipulate(File karafRootFolder);

}

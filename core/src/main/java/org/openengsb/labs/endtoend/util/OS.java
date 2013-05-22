package org.openengsb.labs.endtoend.util;

import java.io.File;

public enum OS {

    LINUX("linux"),

    MAC("mac"),

    WINDOWS("win");

    private final String term;

    private OS(String term) {
        this.term = term;
    }

    /**
     * Find current O/S.
     */
    public static OS current() {
        String name = System.getProperty("os.name").toLowerCase();
        for (OS known : OS.values()) {
            if (name.contains(known.term)) {
                return known;
            }
        }
        throw new UnsupportedOperationException("Unknown O/S: " + name);
    }

    public static OS fromString(String term) {
        for (OS known : OS.values()) {
            if (term.equalsIgnoreCase(known.term)) {
                return known;
            }
        }
        throw new UnsupportedOperationException("Unknown O/S: " + term);
    }

    @Override
    public String toString() {
        return this.term;
    }
    
    /**
     * @return the user home directory
     */
    public static File getUserHome() {
        return new File(System.getProperty("user.home"));
    }
    
    /**
     * 
     * @param fileName the filename relative to the user home folder
     * @return the file object
     */
    public static File getFileInUserHome(String fileName) {
        return new File(System.getProperty("user.home"), fileName);
    }
}

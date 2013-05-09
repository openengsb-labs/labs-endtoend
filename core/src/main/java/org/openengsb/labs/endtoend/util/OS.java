package org.openengsb.labs.endtoend.util;

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
}

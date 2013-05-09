package org.openengsb.labs.endtoend.util;

public enum Arch {

    X86("x86"),

    X86_64("x86_64"),

    AMD64("amd64");

    private final String term;

    private Arch(String term) {
        this.term = term;
    }

    /**
     * Find current architecture.
     */
    public static Arch current() {
        String name = System.getProperty("os.arch").toLowerCase();
        for (Arch known : Arch.values()) {
            if (name.equals(known.term)) {
                return known;
            }
        }
        throw new UnsupportedOperationException("Unknown architecture: " + name);
    }

    public static Arch fromString(String term) {
        for (Arch known : Arch.values()) {
            if (term.equalsIgnoreCase(known.term)) {
                return known;
            }
        }
        throw new UnsupportedOperationException("Unknown architecture: " + term);
    }

    @Override
    public String toString() {
        return this.term;
    }

}

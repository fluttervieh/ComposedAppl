package at.fhv.se.collabnotes.domain.model;

public class VersionMismatchException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public VersionMismatchException(int actual, int expected) {
        super("Mismatch between actual [" + actual + "] and expected [" + expected +"] version");
    }
}

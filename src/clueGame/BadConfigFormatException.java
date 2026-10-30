package clueGame;

public class BadConfigFormatException extends Exception {
    private static final long serialVersionUID = 1L;

    public BadConfigFormatException() {
        super("The configuration file has an invalid format.");
    }

    public BadConfigFormatException(String message) {
        super(message);
    }
}

package main;

public class NoUserException extends Exception {
    public NoUserException() {
        super();
    }

    public NoUserException(String message) {
        super(message);
    }
}

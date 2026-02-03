package exception;

/**
 * This is the custom exception class for the interpreter.
 * It will be thrown when any error occurs during execution,
 * such as division by zero, variable not declared, type mismatches, etc.
 */
public class MyException extends Exception {
    public MyException(String message) {
        super(message);
    }
}
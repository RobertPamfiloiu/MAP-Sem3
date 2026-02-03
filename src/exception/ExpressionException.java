package exception;

/**
 * For errors during expression evaluation (div by zero, type errors).
 */
public class ExpressionException extends MyException {
    public ExpressionException(String message) {
        super(message);
    }
}
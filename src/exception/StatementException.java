package exception;

/**
 * For errors during statement execution (var not declared, type mismatch).
 */
public class StatementException extends MyException {
    public StatementException(String message) {
        super(message);
    }
}
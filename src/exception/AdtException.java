package exception;

/**
 * For errors related to ADT operations (Stack, Dictionary, List).
 */
public class AdtException extends MyException {
    public AdtException(String message) {
        super(message);
    }
}
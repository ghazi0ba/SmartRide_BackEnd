package esprit.driver.exception;

/**
 * Custom Exception for Driver-related errors
 */
public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(String message) {
        super(message);
    }
}

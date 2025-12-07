package Class.Project.CarRental.exception;


  //Thrown when entity validation fails.

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

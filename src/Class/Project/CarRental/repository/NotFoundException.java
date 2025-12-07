package Class.Project.CarRental.repository;

// Thrown when entity is not found in repository.
public class NotFoundException extends Exception {
    public NotFoundException(String message) { super(message); }
}

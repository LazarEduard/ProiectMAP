package Class.Project.CarRental.domain;

/**
 * Generic Identifiable interface for entities.
 * @param <ID> the type of the identifier (e.g., Long, String, UUID)
 */
public interface Identifiable<ID> {
    ID getId();
    void setId(ID id);
}
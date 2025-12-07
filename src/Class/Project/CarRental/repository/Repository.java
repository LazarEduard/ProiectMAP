package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Identifiable;

import java.util.List;
import java.util.Optional;

/**
  Repository interface for identifiable entities.
  @param <ID>       the identifier type (for example Long or String)
  @param <Entity>   the entity type which must implement Identifiable<ID>
 */
public interface Repository<ID, Entity extends Identifiable<ID>> {
    Entity create(Entity entity);
    Optional<Entity> findById(ID id);
    List<Entity> findAll();
    Entity update(Entity entity) throws NotFoundException;
    void deleteById(ID id) throws NotFoundException;
    boolean existsById(ID id);
}

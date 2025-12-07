package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Identifiable;

import java.util.*;
import java.util.function.Supplier;

/**
   In-memory repository implementation that stores entities in a Map.

  @param <ID>      the identifier type
  @param <Entity>  the entity type (must implement Identifiable<ID>)
 */
public class InMemoryRepository<ID, Entity extends Identifiable<ID>>
        implements Repository<ID, Entity> {

    protected final Map<ID, Entity> storage = new HashMap<>();
    private final Supplier<ID> idSupplier;

    /**
      Construct a repository that uses the given ID supplier for new entities.
      @param idSupplier supplier that returns a fresh unique ID each time create() is called
     */
    public InMemoryRepository(Supplier<ID> idSupplier) {
        this.idSupplier = Objects.requireNonNull(idSupplier, "idSupplier must not be null");
    }

    @Override
    public synchronized Entity create(Entity entity) {
        ID id = idSupplier.get();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public synchronized Optional<Entity> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public synchronized List<Entity> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public synchronized Entity update(Entity entity) throws NotFoundException {
        ID id = entity.getId();
        if (id == null || !storage.containsKey(id)) {
            throw new NotFoundException("Entity with id " + id + " not found.");
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public synchronized void deleteById(ID id) throws NotFoundException {
        if (!storage.containsKey(id)) {
            throw new NotFoundException("Entity with id " + id + " not found.");
        }
        storage.remove(id);
    }

    @Override
    public synchronized boolean existsById(ID id) {
        return storage.containsKey(id);
    }
}

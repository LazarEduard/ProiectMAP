package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Identifiable;
import Class.Project.CarRental.filter.AbstractFilter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
  Wrapper around any Repository that can return filtered results using an AbstractFilter.

  @param <ID>      identifier type
  @param <Entity>  entity type
 */
public class FilteredRepository<ID, Entity extends Identifiable<ID>>
        implements Repository<ID, Entity> {

    private final Repository<ID, Entity> delegateRepository;

    public FilteredRepository(Repository<ID, Entity> delegateRepository) {
        this.delegateRepository = Objects.requireNonNull(delegateRepository, "delegateRepository must not be null");
    }

    /**
     Return all entities that match the given filter.
     */
    public List<Entity> findAllByFilter(AbstractFilter<Entity> filter) {
        return delegateRepository.findAll().stream()
                .filter(filter::matches)
                .collect(Collectors.toList());
    }

    //CRUD operations

    @Override
    public Entity create(Entity entity) { return delegateRepository.create(entity); }

    @Override
    public java.util.Optional<Entity> findById(ID id) { return delegateRepository.findById(id); }

    @Override
    public List<Entity> findAll() { return delegateRepository.findAll(); }

    @Override
    public Entity update(Entity entity) throws NotFoundException { return delegateRepository.update(entity); }

    @Override
    public void deleteById(ID id) throws NotFoundException { delegateRepository.deleteById(id); }

    @Override
    public boolean existsById(ID id) { return delegateRepository.existsById(id); }
}

package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Car;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * In-memory Car repository that reuses the generic InMemoryRepository
 * and adds car-specific query implementations.
 */
public class InMemoryCarRepository extends InMemoryRepository<Long, Car> implements CarRepository {

    public InMemoryCarRepository(Supplier<Long> idSupplier) {
        super(idSupplier);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturer) {
        String normalized = manufacturer == null ? "" : manufacturer.trim();
        return findAll().stream()
                .filter(car -> car.getMake() != null && car.getMake().trim().equalsIgnoreCase(normalized))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> findByModel(String model) {
        String normalized = model == null ? "" : model.trim();
        return findAll().stream()
                .filter(car -> car.getModel() != null && car.getModel().trim().equalsIgnoreCase(normalized))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> findByRentalPriceBelow(double maximumPrice) {
        return findAll().stream()
                .filter(car -> car.getRentalPrice() <= maximumPrice)
                .collect(Collectors.toList());
    }
}

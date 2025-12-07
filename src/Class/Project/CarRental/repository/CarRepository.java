package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Car;
import java.util.List;

/**
 * Car-specific repository. Extends the generic Repository to reuse CRUD,
 * and adds domain-specific queries for cars.
 */
public interface CarRepository extends Repository<Long, Car> {
    List<Car> findByManufacturer(String manufacturer);
    List<Car> findByModel(String model);
    List<Car> findByRentalPriceBelow(double maximumPrice);
}

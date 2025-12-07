package Class.Project.CarRental.service;

import Class.Project.CarRental.domain.Car;
import Class.Project.CarRental.repository.CarRepository;
import Class.Project.CarRental.repository.NotFoundException;

import java.util.List;
import java.util.Optional;

public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car createCar(String make, String model, double price) {
        Car car = new Car(make, model, price);
        return carRepository.create(car);
    }

    public List<Car> listAll() { return carRepository.findAll(); }

    public Optional<Car> getById(Long id) { return carRepository.findById(id); }

    public Car updateCar(Long id, String make, String model, double price) throws NotFoundException {
        Car carToUpdate = new Car(id, make, model, price);
        return carRepository.update(carToUpdate);
    }

    public void deleteCar(Long id) throws NotFoundException { carRepository.deleteById(id); }

    public boolean exists(Long id) { return carRepository.existsById(id); }

    // Domain query convenience methods
    public List<Car> findByManufacturer(String manufacturer) {
        return carRepository.findByManufacturer(manufacturer);
    }

    public List<Car> findByModel(String model) {
        return carRepository.findByModel(model);
    }

    public List<Car> findByRentalPriceBelow(double maximumPrice) {
        return carRepository.findByRentalPriceBelow(maximumPrice);
    }
}

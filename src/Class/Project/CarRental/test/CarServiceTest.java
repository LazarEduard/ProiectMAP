package Class.Project.CarRental.test;

import Class.Project.CarRental.domain.Car;
import Class.Project.CarRental.repository.InMemoryCarRepository;
import Class.Project.CarRental.repository.NotFoundException;
import Class.Project.CarRental.service.CarService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


public class CarServiceTest {

    private CarService carService;
    private InMemoryCarRepository carRepository;

    @BeforeEach
      public void setup() {
        // We use a simple counter for IDs in tests
        AtomicLong counter = new AtomicLong(1);

        // Initialize the repository and service
        carRepository = new InMemoryCarRepository(counter::getAndIncrement);
        carService = new CarService(carRepository);
    }

    @Test
    public void testCreateCar() {
        // Action: Create a new car
        Car createdCar = carService.createCar("Toyota", "Corolla", 50.0);

        // Verification
        Assertions.assertNotNull(createdCar.getId(), "Car ID should be generated automatically");
        Assertions.assertEquals("Toyota", createdCar.getMake());
        Assertions.assertEquals("Corolla", createdCar.getModel());
        Assertions.assertEquals(50.0, createdCar.getRentalPrice());

        // Verify it is actually in the list
        Assertions.assertEquals(1, carService.listAll().size());
    }

    @Test
    public void testFindById() {
        // Create a car
        Car car = carService.createCar("Ford", "Focus", 45.0);
        Long carId = car.getId();

        //Find it
        Optional<Car> foundCar = carService.getById(carId);

        // Verification
        Assertions.assertTrue(foundCar.isPresent(), "Car should be found");
        Assertions.assertEquals("Ford", foundCar.get().getMake());
    }

    @Test
    public void testUpdateCar() throws NotFoundException {
        // Setup
        Car car = carService.createCar("BMW", "X5", 100.0);
        Long id = car.getId();

        // Update the price and model
        Car updatedCar = carService.updateCar(id, "BMW", "X5 M", 120.0);

        // Verification
        Assertions.assertEquals("X5 M", updatedCar.getModel());
        Assertions.assertEquals(120.0, updatedCar.getRentalPrice());

        // Double check by retrieving from repo again
        Car fromRepo = carService.getById(id).get();
        Assertions.assertEquals(120.0, fromRepo.getRentalPrice());
    }

    @Test
    public void testDeleteCar() throws NotFoundException {
        Car car = carService.createCar("Audi", "A4", 60.0);
        Long id = car.getId();

        //Delete it
        carService.deleteCar(id);

        // Verification
        Assertions.assertFalse(carService.exists(id), "Car should not exist after deletion");
        Assertions.assertTrue(carService.getById(id).isEmpty());
    }

    @Test
    public void testDeleteNonExistentCarThrowsException() {
        // Expect an Exception when deleting a fake ID
        Assertions.assertThrows(NotFoundException.class, () -> {
            carService.deleteCar(999L);
        });
    }

    @Test
    public void testFindByManufacturer() {
        // Setup: Add multiple cars
        carService.createCar("Toyota", "Camry", 55.0);
        carService.createCar("Honda", "Civic", 40.0);
        carService.createCar("Toyota", "Yaris", 30.0);

        // Search for "Toyota"
        List<Car> toyotas = carService.findByManufacturer("Toyota");

        Assertions.assertEquals(2, toyotas.size(), "Should find exactly 2 Toyotas");

        //Search Case Insensitive
        List<Car> hondas = carService.findByManufacturer("honda");
        Assertions.assertEquals(1, hondas.size(), "Should find Honda regardless of case");
    }

    @Test
    public void testFindByRentalPriceBelow() {
        carService.createCar("Cheap", "Car", 20.0);
        carService.createCar("Expensive", "Car", 100.0);
        List<Car> cheapCars = carService.findByRentalPriceBelow(50.0);

        Assertions.assertEquals(1, cheapCars.size());
        Assertions.assertEquals("Cheap", cheapCars.get(0).getMake());
    }
}
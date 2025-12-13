package Class.Project.CarRental.ui;

import Class.Project.CarRental.config.Settings;
import Class.Project.CarRental.domain.Car;
import Class.Project.CarRental.domain.Reservation;
import Class.Project.CarRental.exception.ValidationException;
import Class.Project.CarRental.filter.CarManufacturerFilter;
import Class.Project.CarRental.filter.CarPriceRangeFilter;
import Class.Project.CarRental.filter.ReservationCustomerFilter;
import Class.Project.CarRental.filter.ReservationDateRangeFilter;
import Class.Project.CarRental.repository.*;
import Class.Project.CarRental.service.CarService;
import Class.Project.CarRental.service.ReservationService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongPredicate;
import java.util.function.Supplier;

/**
 * Main console UI that allows CRUD operations for Cars and Reservations.
 */
public class RentalConsoleUI {

    private final CarService carService;
    private final ReservationService reservationService;
    private final Scanner scanner = new Scanner(System.in);

    public RentalConsoleUI(CarService carService, ReservationService reservationService) {
        this.carService = carService;
        this.reservationService = reservationService;
    }

    public static void main(String[] args) {
        // 1. Initialize Settings
        Settings settings = Settings.getInstance();
        String repositoryType = settings.getRepositoryType(); // "memory", "text", or "binary"

        System.out.println("Starting application using repository type: " + repositoryType);

        // 2. Setup ID Generators (AtomicLong allows us to update the counter later)
        AtomicLong carIdCounter = new AtomicLong(1L);
        AtomicLong reservationIdCounter = new AtomicLong(1L);

        Supplier<Long> carIdSupplier = carIdCounter::getAndIncrement;
        Supplier<Long> reservationIdSupplier = reservationIdCounter::getAndIncrement;

        // 3. Instantiate the correct Repositories based on Settings
        CarRepository carRepository;
        ReservationRepository reservationRepository;

        switch (repositoryType.toLowerCase()) {
            case "binary":
                carRepository = new BinaryCarRepository(carIdSupplier, settings.getCarFile());
                reservationRepository = new BinaryReservationRepository(reservationIdSupplier, settings.getReservationFile());
                break;
            case "text":
                carRepository = new CarTextRepository(carIdSupplier, settings.getCarFile());
                reservationRepository = new ReservationTextRepository(reservationIdSupplier, settings.getReservationFile());
                break;
            case "memory":
            default:
                carRepository = new InMemoryCarRepository(carIdSupplier);
                reservationRepository = new InMemoryReservationRepository(reservationIdSupplier);

                // If in memory, let's prepopulate some data for testing
                if (carRepository.findAll().isEmpty()) {
                    carRepository.create(new Car("Toyota", "Corolla", 35.00));
                    carRepository.create(new Car("Ford", "Focus", 40.50));
                    carRepository.create(new Car("BMW", "3 Series", 85.00));
                    carRepository.create(new Car("Volkswagen", "Golf", 50.00));
                    carRepository.create(new Car("Renault", "Clio", 32.75));
                }
                break;
        }

        // 4. Synchronize ID Counters
        // If we loaded data from a file, we must ensure the next ID we generate is higher than the max existing ID.
        long maxCarId = carRepository.findAll().stream()
                .mapToLong(Car::getId)
                .max()
                .orElse(0L);
        carIdCounter.set(maxCarId + 1);

        long maxResId = reservationRepository.findAll().stream()
                .mapToLong(Reservation::getId)
                .max()
                .orElse(0L);
        reservationIdCounter.set(maxResId + 1);


        // 5. Initialize Services
        CarService carService = new CarService(carRepository);

        // carExistenceChecker uses carService.exists(Long)
        LongPredicate carExistenceChecker = carService::exists;
        ReservationService reservationService = new ReservationService(reservationRepository, carExistenceChecker);

        // 6. Start UI
        RentalConsoleUI ui = new RentalConsoleUI(carService, reservationService);
        ui.runMainMenu();
    }

    private void runMainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Car Rental Manager ===");
            System.out.println("1) Manage Cars");
            System.out.println("2) Manage Reservations");
            System.out.println("3) Exit");
            String choice = prompt("Choice");
            switch (choice) {
                case "1": runCarMenu(); break;
                case "2": runReservationMenu(); break;
                case "3": running = false; break;
                default: System.out.println("Unknown choice."); break;
            }
        }
        System.out.println("Goodbye!");
    }

    // ------------ Car menu --------------
    private void runCarMenu() {
        boolean inCarMenu = true;
        while (inCarMenu) {
            System.out.println("\n--- Manage Cars ---");
            System.out.println("1) List all cars");
            System.out.println("2) View car by id");
            System.out.println("3) Create car");
            System.out.println("4) Update car");
            System.out.println("5) Delete car");
            System.out.println("6) Filter cars by manufacturer");
            System.out.println("7) Filter cars by price range");
            System.out.println("8) Back");
            String choice = prompt("Choice");
            switch (choice) {
                case "1": listAllCars(); break;
                case "2": viewCarById(); break;
                case "3": createCar(); break;
                case "4": updateCar(); break;
                case "5": deleteCar(); break;
                case "6": filterCarsByManufacturer(); break;
                case "7": filterCarsByPriceRange(); break;
                case "8": inCarMenu = false; break;
                default: System.out.println("Unknown choice."); break;
            }
        }
    }

    private void listAllCars() {
        List<Car> cars = carService.listAll();
        if (cars.isEmpty()) {
            System.out.println("No cars available.");
            return;
        }
        System.out.println("Cars:");
        cars.forEach(car -> System.out.println("  " + car));
    }

    private void viewCarById() {
        Long id = readLong("Car id");
        if (id == null) return;
        Optional<Car> maybeCar = carService.getById(id);
        System.out.println(maybeCar.map(Object::toString).orElse("Car not found."));
    }

    private void createCar() {
        try {
            String manufacturer = prompt("Manufacturer");
            String model = prompt("Model");
            Double dailyRate = readDouble("Daily rental price");
            if (manufacturer.isEmpty() || model.isEmpty() || dailyRate == null) {
                System.out.println("Aborted: missing fields.");
                return;
            }
            if (dailyRate < 0) throw new ValidationException("Daily rate must be non-negative.");
            Car created = carService.createCar(manufacturer, model, dailyRate);
            System.out.println("Created: " + created);
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private void updateCar() {
        Long id = readLong("Car id to update");
        if (id == null) return;
        if (!carService.exists(id)) {
            System.out.println("No car with id " + id);
            return;
        }
        try {
            String manufacturer = prompt("Manufacturer");
            String model = prompt("Model");
            Double dailyRate = readDouble("Daily rental price");
            if (manufacturer.isEmpty() || model.isEmpty() || dailyRate == null) {
                System.out.println("Aborted: missing fields.");
                return;
            }
            if (dailyRate < 0) throw new ValidationException("Daily rate must be non-negative.");
            Car updated = carService.updateCar(id, manufacturer, model, dailyRate);
            System.out.println("Updated: " + updated);
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating car: " + e.getMessage());
        }
    }

    private void deleteCar() {
        Long id = readLong("Car id to delete");
        if (id == null) return;
        try {
            carService.deleteCar(id);
            System.out.println("Deleted car id " + id);
        } catch (Exception e) {
            System.out.println("Error deleting car: " + e.getMessage());
        }
    }

    private void filterCarsByManufacturer() {
        String manufacturer = prompt("Manufacturer to filter by");
        CarManufacturerFilter filter = new CarManufacturerFilter(manufacturer);
        carService.listAll().stream()
                .filter(filter::matches)
                .forEach(car -> System.out.println("  " + car));
    }


    private void filterCarsByPriceRange() {
        Double minimumPrice = readDouble("Minimum price");
        Double maximumPrice = readDouble("Maximum price");
        if (minimumPrice == null || maximumPrice == null) {
            System.out.println("Aborted: invalid input.");
            return;
        }
        try {
            CarPriceRangeFilter filter = new CarPriceRangeFilter(minimumPrice, maximumPrice);
            carService.listAll().stream()
                    .filter(filter::matches)
                    .forEach(car -> System.out.println("  " + car));
        } catch (IllegalArgumentException ex) {
            System.out.println("Invalid range: " + ex.getMessage());
        }
    }


    // ------------ Reservation menu -------------
    private void runReservationMenu() {
        boolean inReservationMenu = true;
        while (inReservationMenu) {
            System.out.println("\n--- Manage Reservations ---");
            System.out.println("1) List all reservations");
            System.out.println("2) View reservation by id");
            System.out.println("3) Create reservation");
            System.out.println("4) Update reservation");
            System.out.println("5) Delete reservation");
            System.out.println("6) Filter reservations by customer name");
            System.out.println("7) Filter reservations by date range (overlap)");
            System.out.println("8) Back");
            String choice = prompt("Choice");
            switch (choice) {
                case "1": listAllReservations(); break;
                case "2": viewReservationById(); break;
                case "3": createReservation(); break;
                case "4": updateReservation(); break;
                case "5": deleteReservation(); break;
                case "6": filterReservationsByCustomer(); break;
                case "7": filterReservationsByDateRange(); break;
                case "8": inReservationMenu = false; break;
                default: System.out.println("Unknown choice."); break;
            }
        }
    }

    private void listAllReservations() {
        List<Reservation> reservations = reservationService.listAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations.");
            return;
        }
        System.out.println("Reservations:");
        reservations.forEach(reservation -> System.out.println("  " + reservation));
    }

    private void viewReservationById() {
        Long id = readLong("Reservation id");
        if (id == null) return;
        Optional<Reservation> maybeReservation = reservationService.getById(id);
        System.out.println(maybeReservation.map(Object::toString).orElse("Reservation not found."));
    }

    private void createReservation() {
        try {
            Long carId = readLong("Car id to reserve");
            if (carId == null) return;
            String customerName = prompt("Customer name");
            LocalDate startDate = readDate("Start date (YYYY-MM-DD)");
            LocalDate endDate = readDate("End date (YYYY-MM-DD)");
            Reservation created = reservationService.createReservation(carId, customerName, startDate, endDate);
            System.out.println("Created: " + created);
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error creating reservation: " + e.getMessage());
        }
    }

    private void updateReservation() {
        Long id = readLong("Reservation id to update");
        if (id == null) return;
        if (!reservationService.exists(id)) {
            System.out.println("No reservation with id " + id);
            return;
        }
        try {
            Long carId = readLong("Car id to reserve");
            if (carId == null) return;
            String customerName = prompt("Customer name");
            LocalDate startDate = readDate("Start date (YYYY-MM-DD)");
            LocalDate endDate = readDate("End date (YYYY-MM-DD)");
            Reservation updated = reservationService.updateReservation(id, carId, customerName, startDate, endDate);
            System.out.println("Updated: " + updated);
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating reservation: " + e.getMessage());
        }
    }

    private void deleteReservation() {
        Long id = readLong("Reservation id to delete");
        if (id == null) return;
        try {
            reservationService.deleteReservation(id);
            System.out.println("Deleted reservation id " + id);
        } catch (Exception e) {
            System.out.println("Error deleting reservation: " + e.getMessage());
        }
    }

    private void filterReservationsByCustomer() {
        String customer = prompt("Customer name");
        var filter = new ReservationCustomerFilter(customer);
        reservationService.listAllReservations().stream()
                .filter(filter::matches)
                .forEach(reservation -> System.out.println("  " + reservation));
    }

    private void filterReservationsByDateRange() {
        LocalDate from = readDate("From date (YYYY-MM-DD)");
        LocalDate to = readDate("To date (YYYY-MM-DD)");
        if (from == null || to == null) {
            System.out.println("Aborted: invalid dates.");
            return;
        }
        try {
            var filter = new ReservationDateRangeFilter(from, to);
            reservationService.listAllReservations().stream()
                    .filter(filter::matches)
                    .forEach(reservation -> System.out.println("  " + reservation));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date range: " + e.getMessage());
        }
    }

    // ----------------- Helpers -----------------

    private String prompt(String message) {
        System.out.print(message + ": ");
        return scanner.nextLine().trim();
    }

    private Long readLong(String message) {
        String inputstring = prompt(message);
        if (inputstring.isEmpty()) {
            System.out.println("No value entered.");
            return null;
        }
        try {
            return Long.parseLong(inputstring);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return null;
        }
    }

    private Double readDouble(String message) {
        String inputstring = prompt(message);
        if (inputstring.isEmpty()) return null;
        try {
            return Double.parseDouble(inputstring);
        } catch (NumberFormatException e) {
            System.out.println("Invalid decimal number.");
            return null;
        }
    }

    private LocalDate readDate(String message) {
        String inputstring = prompt(message);
        if (inputstring.isEmpty()) {
            System.out.println("No date entered.");
            return null;
        }
        try {
            return LocalDate.parse(inputstring); //(YYYY-MM-DD)
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return null;
        }
    }
}
package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Car;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CarTextRepository extends InMemoryRepository<Long, Car> implements CarRepository {

    private final String filePath;

    public CarTextRepository(Supplier<Long> idSupplier, String filePath) {
        super(idSupplier);
        this.filePath = filePath;
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                // Format: id,make,model,price
                long id = Long.parseLong(parts[0]);
                String make = parts[1];
                String model = parts[2];
                double price = Double.parseDouble(parts[3]);

                Car car = new Car(id, make, model, price);
                this.storage.put(id, car);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load cars from file: " + filePath, exception);
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Car car : findAll()) {
                String line = String.format("%d,%s,%s,%.2f",
                        car.getId(),
                        car.getMake(),
                        car.getModel(),
                        car.getRentalPrice());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to save cars to file: " + filePath, exception);
        }
    }

    @Override
    public Car create(Car car) {
        // Call the parent InMemory logic to add to map
        Car createdCar = super.create(car);
        // Persist to file
        saveToFile();
        return createdCar;
    }

    @Override
    public Car update(Car car) throws NotFoundException {
        Car updatedCar = super.update(car);
        saveToFile();
        return updatedCar;
    }

    @Override
    public void deleteById(Long id) throws NotFoundException {
        super.deleteById(id);
        saveToFile();
    }

    // Domain specific queries (Same as InMemoryCarRepository)

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
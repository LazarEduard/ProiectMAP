package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Car;

import java.io.*;
import java.util.*;

public class CarTextRepository {

    private final Map<Long, Car> storage = new HashMap<>();
    private long nextId = 1L;
    private final String filePath;

    public CarTextRepository(String filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\t", -1);
                // expect: id, make, model, price
                long id = Long.parseLong(parts[0]);
                String make = parts.length > 1 ? parts[1] : "";
                String model = parts.length > 2 ? parts[2] : "";
                double price = parts.length > 3 && !parts[3].isEmpty() ? Double.parseDouble(parts[3]) : 0.0;
                Car car = new Car(id, make, model, price);
                storage.put(id, car);
                if (id >= nextId) nextId = id + 1;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cars from " + filePath, e);
        }
    }

    private void saveToFile() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Car car : storage.values()) {
                String line = String.format("%d\t%s\t%s\t%.2f",
                        car.getId(),
                        safe(car.getMake()),
                        safe(car.getModel()),
                        car.getRentalPrice());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save cars to " + filePath, e);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\t", " "); // avoid tabs in fields
    }



    public synchronized Car create(Car car) {
        long id = nextId++;
        car.setId(id);
        storage.put(id, car);
        saveToFile();
        return car;
    }

    // returns the Car or null if not found
    public synchronized Car findById(long id) {
        return storage.get(id);
    }

    public synchronized List<Car> findAll() {
        return new ArrayList<>(storage.values());
    }

    //throws IllegalArgumentException
    public synchronized Car update(Car car) {
        Long id = car.getId();
        if (id == null || !storage.containsKey(id)) {
            throw new IllegalArgumentException("Car with id " + id + " not found.");
        }
        storage.put(id, car);
        saveToFile();
        return car;
    }

    public synchronized void deleteById(long id) {
        if (!storage.containsKey(id)) throw new IllegalArgumentException("Car with id " + id + " not found.");
        storage.remove(id);
        saveToFile();
    }

    public synchronized boolean existsById(long id) {
        return storage.containsKey(id);
    }
}

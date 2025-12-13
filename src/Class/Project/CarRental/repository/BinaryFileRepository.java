package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Identifiable;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

 //A generic repository that saves data to a binary file using Java Serialization.
 //Extends InMemoryRepository to reuse the Map storage mechanism.
public class BinaryFileRepository<ID, Entity extends Identifiable<ID> & Serializable>
        extends InMemoryRepository<ID, Entity> {

    private final String fileName;

    public BinaryFileRepository(Supplier<ID> idSupplier, String fileName) {
        super(idSupplier);
        this.fileName = fileName;
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(fileName);
        // If the file does not exist, we start with an empty repository.
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            // Read the entire map from the file
            Map<ID, Entity> dataFromFile = (Map<ID, Entity>) objectInputStream.readObject();
            // Clear current storage and add all loaded data
            storage.clear();
            storage.putAll(dataFromFile);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load data from binary file: " + fileName, e);
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            // Write the entire storage map to the file
            objectOutputStream.writeObject(storage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to binary file: " + fileName, e);
        }
    }

    // Override modify methods to save changes to file immediately

    @Override
    public Entity create(Entity entity) {
        Entity createdEntity = super.create(entity);
        saveToFile();
        return createdEntity;
    }

    @Override
    public Entity update(Entity entity) throws NotFoundException {
        Entity updatedEntity = super.update(entity);
        saveToFile();
        return updatedEntity;
    }

    @Override
    public void deleteById(ID id) throws NotFoundException {
        super.deleteById(id);
        saveToFile();
    }
}
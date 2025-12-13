package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Reservation;

import java.io.*;
import java.time.LocalDate;
import java.util.function.Supplier;

public class ReservationTextRepository extends InMemoryRepository<Long, Reservation> implements ReservationRepository {

    private final String filePath;

    public ReservationTextRepository(Supplier<Long> idSupplier, String filePath) {
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
                // Format: id,carId,customerName,startDate,endDate
                long id = Long.parseLong(parts[0]);
                long carId = Long.parseLong(parts[1]);
                String customerName = parts[2];
                LocalDate startDate = LocalDate.parse(parts[3]);
                LocalDate endDate = LocalDate.parse(parts[4]);

                Reservation reservation = new Reservation(id, carId, customerName, startDate, endDate);
                this.storage.put(id, reservation);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load reservations from file: " + filePath, exception);
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Reservation reservation : findAll()) {
                String line = String.format("%d,%d,%s,%s,%s",
                        reservation.getId(),
                        reservation.getCarId(),
                        reservation.getCustomerName(),
                        reservation.getStartDate().toString(),
                        reservation.getEndDate().toString());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to save reservations to file: " + filePath, exception);
        }
    }

    @Override
    public Reservation create(Reservation reservation) {
        Reservation createdReservation = super.create(reservation);
        saveToFile();
        return createdReservation;
    }

    @Override
    public Reservation update(Reservation reservation) throws NotFoundException {
        Reservation updatedReservation = super.update(reservation);
        saveToFile();
        return updatedReservation;
    }

    @Override
    public void deleteById(Long id) throws NotFoundException {
        super.deleteById(id);
        saveToFile();
    }
}
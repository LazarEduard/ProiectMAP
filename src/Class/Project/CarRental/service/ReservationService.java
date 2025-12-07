package Class.Project.CarRental.service;

import Class.Project.CarRental.domain.Reservation;
import Class.Project.CarRental.exception.ValidationException;
import Class.Project.CarRental.repository.NotFoundException;
import Class.Project.CarRental.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Reservation service with business validation rules.
 * The repository for reservations is injected; the car existence check
 * is performed by calling a provided carExistenceChecker.
 */
public class ReservationService {
    private final Repository<Long, Reservation> reservationRepository;
    // Function to check whether a car with given id exists. We use a simple interface here.
    private final java.util.function.LongPredicate carExistenceChecker;

    public ReservationService(Repository<Long, Reservation> reservationRepository,
                              java.util.function.LongPredicate carExistenceChecker) {
        this.reservationRepository = reservationRepository;
        this.carExistenceChecker = carExistenceChecker;
    }

    public Reservation createReservation(Long carId, String customerName, LocalDate startDate, LocalDate endDate) {
        validateReservationData(carId, customerName, startDate, endDate);
        Reservation reservation = new Reservation(carId, customerName.trim(), startDate, endDate);
        return reservationRepository.create(reservation);
    }

    public List<Reservation> listAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation updateReservation(Long id, Long carId, String customerName, LocalDate startDate, LocalDate endDate)
            throws NotFoundException {
        validateReservationData(carId, customerName, startDate, endDate);
        Reservation toUpdate = new Reservation(id, carId, customerName.trim(), startDate, endDate);
        return reservationRepository.update(toUpdate);
    }

    public void deleteReservation(Long id) throws NotFoundException {
        reservationRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return reservationRepository.existsById(id);
    }

    private void validateReservationData(Long carId, String customerName, LocalDate startDate, LocalDate endDate) {
        if (carId == null) {
            throw new ValidationException("Car id must not be empty.");
        }
        if (!carExistenceChecker.test(carId)) {
            throw new ValidationException("No car exists with id: " + carId);
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new ValidationException("Customer name must not be empty.");
        }
        if (startDate == null || endDate == null) {
            throw new ValidationException("Start date and end date must be provided (format: YYYY-MM-DD).");
        }
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date must be the same or after the start date.");
        }
    }
}

package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Reservation;

import java.util.function.Supplier;

/**
 * In-memory reservation repository. Reuses the generic InMemoryRepository.
 */
public class InMemoryReservationRepository extends InMemoryRepository<Long, Reservation>
        implements ReservationRepository {

    public InMemoryReservationRepository(Supplier<Long> idSupplier) {
        super(idSupplier);
    }

    // You may add Reservation-specific methods here in future (e.g., findByCarId)
}

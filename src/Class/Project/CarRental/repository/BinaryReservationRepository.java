package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Reservation;

import java.util.function.Supplier;

  //Binary implementation for the Reservation Repository.

public class BinaryReservationRepository extends BinaryFileRepository<Long, Reservation>
        implements ReservationRepository {

    public BinaryReservationRepository(Supplier<Long> idSupplier, String fileName) {
        super(idSupplier, fileName);
    }
}
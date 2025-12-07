package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Reservation;

/**
 * Reservation-specific repository. Extends generic Repository
 */
public interface ReservationRepository extends Repository<Long, Reservation> {
    // add domain-specific queries here if needed (e.g., findByCarId)
}

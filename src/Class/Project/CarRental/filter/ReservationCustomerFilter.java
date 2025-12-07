package Class.Project.CarRental.filter;

import Class.Project.CarRental.domain.Reservation;

/**
 * Match reservations by exact (case-insensitive) customer name.
 */
public class ReservationCustomerFilter implements AbstractFilter<Reservation> {
    private final String customerNameToMatch;

    public ReservationCustomerFilter(String customerNameToMatch) {
        this.customerNameToMatch = customerNameToMatch == null ? "" : customerNameToMatch.trim();
    }

    @Override
    public boolean matches(Reservation reservation) {
        if (reservation == null) return false;
        String name = reservation.getCustomerName() == null ? "" : reservation.getCustomerName().trim();
        return !customerNameToMatch.isEmpty() && name.equalsIgnoreCase(customerNameToMatch);
    }
}

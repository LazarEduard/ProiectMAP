package Class.Project.CarRental.filter;

import Class.Project.CarRental.domain.Reservation;

import java.time.LocalDate;


  //Matches reservations that overlap with the given [fromDate, toDate] inclusive date range.

public class ReservationDateRangeFilter implements AbstractFilter<Reservation> {
    private final LocalDate fromDateInclusive;
    private final LocalDate toDateInclusive;

    public ReservationDateRangeFilter(LocalDate fromDateInclusive, LocalDate toDateInclusive) {
        if (fromDateInclusive == null || toDateInclusive == null) {
            throw new IllegalArgumentException("Both from and to dates must be provided.");
        }
        if (toDateInclusive.isBefore(fromDateInclusive)) {
            throw new IllegalArgumentException("toDateInclusive must be same or after fromDateInclusive.");
        }
        this.fromDateInclusive = fromDateInclusive;
        this.toDateInclusive = toDateInclusive;
    }

    @Override
    public boolean matches(Reservation reservation) {
        if (reservation == null) return false;
        LocalDate start = reservation.getStartDate();
        LocalDate end = reservation.getEndDate();
        if (start == null || end == null) return false;
        // Overlap if reservation.start <= toDate && reservation.end >= fromDate
        return !start.isAfter(toDateInclusive) && !end.isBefore(fromDateInclusive);
    }
}

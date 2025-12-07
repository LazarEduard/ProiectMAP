package Class.Project.CarRental.domain;

import java.time.LocalDate;
import java.util.Objects;


  //Reservation entity: id, carId, customerName, startDate, endDate.

public class Reservation implements Identifiable<Long> {
    private Long id;
    private Long carId;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;

    public Reservation() {}

    public Reservation(Long id, Long carId, String customerName, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.carId = carId;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Reservation(Long carId, String customerName, LocalDate startDate, LocalDate endDate) {
        this(null, carId, customerName, startDate, endDate);
    }

    @Override
    public Long getId() { return id; }

    @Override
    public void setId(Long id) { this.id = id; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    @Override
    public String toString() {
        return String.format("Reservation{id=%d, carId=%d, customer='%s', start=%s, end=%s}",
                id, carId, customerName,
                startDate == null ? "null" : startDate.toString(),
                endDate == null ? "null" : endDate.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Reservation)) return false;
        Reservation that = (Reservation) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package Class.Project.CarRental.domain;

import java.util.Objects;

/**
 * Car entity: ID, make, model, rentalPrice.
 */
public class    Car implements Identifiable<Long> {
    private Long id;
    private String make;
    private String model;
    private double rentalPrice;

    public Car() {}

    public Car(Long id, String make, String model, double rentalPrice) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.rentalPrice = rentalPrice;
    }

    public Car(String make, String model, double rentalPrice) {
        this(null, make, model, rentalPrice);
    }

    @Override
    public Long getId() { return id; }

    @Override
    public void setId(Long id) { this.id = id; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getRentalPrice() { return rentalPrice; }
    public void setRentalPrice(double rentalPrice) { this.rentalPrice = rentalPrice; }

    @Override
    public String toString() {
        return String.format("Car{id=%d, make='%s', model='%s', rentalPrice=%.2f}",
                id, make, model, rentalPrice);
    }

    @Override
    public boolean equals(Object car2) {
        if (this == car2) return true;
        if (!(car2 instanceof Car)) return false;
        Car car = (Car) car2;
        //checks equality
        return Objects.equals(id, car.id);
    }

    @Override
    public int hashCode() {
        //generates hash code for id
        return Objects.hashCode(id);
    }
}

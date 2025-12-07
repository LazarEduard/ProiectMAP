package Class.Project.CarRental.filter;

import Class.Project.CarRental.domain.Car;

/**
 * Matches cars whose rental price is inside the inclusive [minimumInclusive, maximumInclusive] range.
 */
public class CarPriceRangeFilter implements AbstractFilter<Car> {

    private final double minimumInclusive;
    private final double maximumInclusive;

    /**
     * Constructs a price-range filter.
     *
     * @param minimumInclusive minimum price (inclusive)
     * @param maximumInclusive maximum price (inclusive)
     * @throws IllegalArgumentException if maximumInclusive < minimumInclusive
     */
    public CarPriceRangeFilter(double minimumInclusive, double maximumInclusive) {
        if (maximumInclusive < minimumInclusive) {
            throw new IllegalArgumentException("maximumInclusive must be >= minimumInclusive");
        }
        this.minimumInclusive = minimumInclusive;
        this.maximumInclusive = maximumInclusive;
    }

    @Override
    public boolean matches(Car car) {
        if (car == null) return false;
        double price = car.getRentalPrice();
        return price >= minimumInclusive && price <= maximumInclusive;
    }
}

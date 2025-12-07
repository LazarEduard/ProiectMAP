package Class.Project.CarRental.filter;

import Class.Project.CarRental.domain.Car;

/**
 * Matches cars whose manufacturer equals the requested manufacturer.
 * Comparison is case-insensitive and trims whitespace.
 */
public class CarManufacturerFilter implements AbstractFilter<Car> {

    private final String manufacturerToMatch;

    /**
     * Create a filter that matches cars by manufacturer.
     *
     * @param manufacturerToMatch the manufacturer to match (null treated as empty)
     */
    public CarManufacturerFilter(String manufacturerToMatch) {
        this.manufacturerToMatch = manufacturerToMatch == null ? "" : manufacturerToMatch.trim();
    }

    @Override
    public boolean matches(Car car) {
        if (car == null) return false;
        String carManufacturer = car.getMake() == null ? "" : car.getMake().trim();
        return !manufacturerToMatch.isEmpty() && carManufacturer.equalsIgnoreCase(manufacturerToMatch);
    }
}

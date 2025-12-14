package Class.Project.CarRental.repository;

import Class.Project.CarRental.domain.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDbRepository extends DatabaseRepository<Long, Reservation>
        implements ReservationRepository {

    public ReservationDbRepository() {
        super("reservations");
    }

    @Override
    public Reservation create(Reservation reservation) {
        String sql = "INSERT INTO reservations (car_id, customer_name, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, reservation.getCarId());
            statement.setString(2, reservation.getCustomerName());
            statement.setString(3, reservation.getStartDate().toString());
            statement.setString(4, reservation.getEndDate().toString());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getLong(1));
                }
            }
            return reservation;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during create reservation", e);
        }
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return Optional.of(extractReservation(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations";
        List<Reservation> list = new ArrayList<>();
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractReservation(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Reservation update(Reservation reservation) throws NotFoundException {
        String sql = "UPDATE reservations SET car_id=?, customer_name=?, start_date=?, end_date=? WHERE id=?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, reservation.getCarId());
            statement.setString(2, reservation.getCustomerName());
            statement.setString(3, reservation.getStartDate().toString());
            statement.setString(4, reservation.getEndDate().toString());
            statement.setLong(5, reservation.getId());

            int updated = statement.executeUpdate();
            if (updated == 0) throw new NotFoundException("Reservation not found: " + reservation.getId());
            return reservation;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) throws NotFoundException {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            int deleted = statement.executeUpdate();
            if (deleted == 0) throw new NotFoundException("Reservation not found: " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM reservations WHERE id = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Reservation extractReservation(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long carId = rs.getLong("car_id");
        String customer = rs.getString("customer_name");
        // Convert SQL Text Date -> Java LocalDate
        LocalDate start = LocalDate.parse(rs.getString("start_date"));
        LocalDate end = LocalDate.parse(rs.getString("end_date"));

        return new Reservation(id, carId, customer, start, end);
    }
}
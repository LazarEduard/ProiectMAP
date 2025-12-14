package Class.Project.CarRental.repository;

import Class.Project.CarRental.config.Settings;
import Class.Project.CarRental.domain.Identifiable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

public abstract class DatabaseRepository<ID, Entity extends Identifiable<ID>>
        implements Repository<ID, Entity> {

    // We keep the table name so subclasses can tell us which table to use
    protected final String tableName;

    public DatabaseRepository(String tableName) {
        this.tableName = tableName;
    }
     //Creates a new connection to the database.

    protected Connection openConnection() {
        try {
            Settings settings = Settings.getInstance();
            String url = settings.getRepositoryLocation();
            String user = settings.getDatabaseUser();
            String password = settings.getDatabasePassword();

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database.", e);
        }
    }
}
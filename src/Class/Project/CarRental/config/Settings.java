package Class.Project.CarRental.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static Settings instance;
    private final Properties properties;

    private Settings() {
        properties = new Properties();
        try {
            properties.load(new FileReader("settings.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load settings.properties", e);
        }
    }

    public static synchronized Settings getInstance() {
        if (instance == null) instance = new Settings();
        return instance;
    }

    public String getRepositoryType() {
        return properties.getProperty("Repository");
    }

    public String getRepositoryLocation() {
        return properties.getProperty("Location");
    }

    public String getDatabaseUser() {
        return properties.getProperty("User", ""); // Default to empty string
    }

    public String getDatabasePassword() {
        return properties.getProperty("Password", ""); // Default to empty string
    }

    // Keep these for backward compatibility if you still want to switch to files
    public String getCarFile() { return properties.getProperty("Cars"); }
    public String getReservationFile() { return properties.getProperty("Reservations"); }
}
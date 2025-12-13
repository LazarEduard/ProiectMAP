package Class.Project.CarRental.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


 //Singleton class to load and access application settings.
public class Settings {

    private static Settings instance;
    private final Properties properties;

    // Private constructor prevents direct instantiation
    private Settings() {
        properties = new Properties();
        try {
            // Load the settings.properties file from the project root
            properties.load(new FileReader("settings.properties"));
        } catch (IOException exception) {
            throw new RuntimeException("Could not load settings.properties file.", exception);
        }
    }


     //Returns the single instance of the Settings class.
    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getRepositoryType() {
        return properties.getProperty("Repository");
    }

    public String getCarFile() {
        return properties.getProperty("Cars");
    }

    public String getReservationFile() {
        return properties.getProperty("Reservations");
    }
}
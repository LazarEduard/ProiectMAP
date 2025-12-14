package Class.Project.CarRental.ui;

import Class.Project.CarRental.config.Settings;
import Class.Project.CarRental.domain.Car;
import Class.Project.CarRental.domain.Reservation;
import Class.Project.CarRental.repository.*;
import Class.Project.CarRental.service.CarService;
import Class.Project.CarRental.service.ReservationService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongPredicate;
import java.util.function.Supplier;

public class CarRentalFX extends Application {

    private CarService carService;
    private ReservationService reservationService;

    // UI Components for Cars
    private TableView<Car> carTable = new TableView<>();
    private ObservableList<Car> carData = FXCollections.observableArrayList();
    private TableView<Reservation> reservationTable = new TableView<>();
    private ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        setupServices();

        TabPane tabPane = new TabPane();

        //Cars
        Tab carsTab = new Tab("Cars", createCarsView());
        carsTab.setClosable(false);

        //Reservations (Placeholder for now)
        Tab reservationsTab = new Tab("Reservations", createReservationsView());
        reservationsTab.setClosable(false);

        // Tab 3: Reports (Placeholder for now)
        Tab reportsTab = new Tab("Reports", new Label("Reports UI goes here"));
        reportsTab.setClosable(false);

        tabPane.getTabs().addAll(carsTab, reservationsTab, reportsTab);

        // 3. SHOW SCENE
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setTitle("Car Rental Manager (JavaFX)");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load initial data
        refreshCarTable();
        refreshReservationTable();
    }

    // --- SETUP BACKEND ---
    private void setupServices() {
        Settings settings = Settings.getInstance();
        String repositoryType = settings.getRepositoryType();

        CarRepository carRepository;
        ReservationRepository reservationRepository;

        // Simple ID counters for Memory/File mode
        AtomicLong carIdCounter = new AtomicLong(1L);
        AtomicLong reservationIdCounter = new AtomicLong(1L);
        Supplier<Long> carIdSupplier = carIdCounter::getAndIncrement;
        Supplier<Long> reservationIdSupplier = reservationIdCounter::getAndIncrement;

        switch (repositoryType.toLowerCase()) {
            case "database":
                carRepository = new CarDbRepository();
                reservationRepository = new ReservationDbRepository();
                break;
            case "binary":
                carRepository = new BinaryCarRepository(carIdSupplier, settings.getCarFile());
                reservationRepository = new BinaryReservationRepository(reservationIdSupplier, settings.getReservationFile());
                break;
            case "text":
                carRepository = new CarTextRepository(carIdSupplier, settings.getCarFile());
                reservationRepository = new ReservationTextRepository(reservationIdSupplier, settings.getReservationFile());
                break;
            default: // Memory
                carRepository = new InMemoryCarRepository(carIdSupplier);
                reservationRepository = new InMemoryReservationRepository(reservationIdSupplier);
        }

        carService = new CarService(carRepository);
        LongPredicate carExistenceChecker = carService::exists;
        reservationService = new ReservationService(reservationRepository, carExistenceChecker);
    }

    // --- UI BUILDER: CARS TAB ---
    private VBox createCarsView() {
        // 1. The Table
        TableColumn<Car, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Car, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<Car, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Car, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("rentalPrice"));

        carTable.getColumns().addAll(idCol, makeCol, modelCol, priceCol);
        carTable.setItems(carData); // Connects the table to our list

        // 2. The Form (Add New Car)
        TextField makeField = new TextField();
        makeField.setPromptText("Make");

        TextField modelField = new TextField();
        modelField.setPromptText("Model");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Button addButton = new Button("Add Car");
        addButton.setOnAction(e -> {
            try {
                String make = makeField.getText();
                String model = modelField.getText();
                double price = Double.parseDouble(priceField.getText());

                carService.createCar(make, model, price);
                refreshCarTable(); // Refresh UI

                // Clear fields
                makeField.clear();
                modelField.clear();
                priceField.clear();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input: " + ex.getMessage());
                alert.show();
            }
        });

        HBox formBox = new HBox(10, makeField, modelField, priceField, addButton);
        formBox.setPadding(new Insets(10));

        // 3. Combine
        VBox layout = new VBox(10, carTable, formBox);
        layout.setPadding(new Insets(10));
        return layout;
    }



    private VBox createReservationsView() {
        // 1. The Table
        TableColumn<Reservation, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Reservation, Long> carIdCol = new TableColumn<>("Car ID");
        carIdCol.setCellValueFactory(new PropertyValueFactory<>("carId"));

        TableColumn<Reservation, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Reservation, String> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Reservation, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        reservationTable.getColumns().addAll(idCol, carIdCol, customerCol, startCol, endCol);
        reservationTable.setItems(reservationData);

        // 2. The Form
        // A Dropdown to select a car (linked to the live car list!)
        ComboBox<Car> carCombo = new ComboBox<>(carData);
        carCombo.setPromptText("Select a Car");
        carCombo.setPrefWidth(150);

        TextField customerField = new TextField();
        customerField.setPromptText("Customer Name");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        Button addButton = new Button("Book");
        addButton.setOnAction(e -> {
            try {
                Car selectedCar = carCombo.getValue();
                if (selectedCar == null) throw new RuntimeException("Please select a car.");

                String customer = customerField.getText();
                if (customer.isEmpty()) throw new RuntimeException("Name is required.");

                if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                    throw new RuntimeException("Select both dates.");
                }

                reservationService.createReservation(
                        selectedCar.getId(),
                        customer,
                        startDatePicker.getValue(),
                        endDatePicker.getValue()
                );

                refreshReservationTable();

                // Reset fields
                customerField.clear();
                startDatePicker.setValue(null);
                endDatePicker.setValue(null);
                carCombo.getSelectionModel().clearSelection();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                alert.show();
            }
        });

        HBox formBox = new HBox(10, carCombo, customerField, startDatePicker, endDatePicker, addButton);
        formBox.setPadding(new Insets(10));

        VBox layout = new VBox(10, reservationTable, formBox);
        layout.setPadding(new Insets(10));
        return layout;
    }

    private void refreshReservationTable() {
        reservationData.clear();
        reservationData.addAll(reservationService.listAllReservations());
    }

    private void refreshCarTable() {
        carData.clear();
        carData.addAll(carService.listAll());
    }
}
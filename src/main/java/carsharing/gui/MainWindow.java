package carsharing.gui;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
import carsharing.repository.Repository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;

public class MainWindow extends JFrame {
    private final Repository repository;

    private final CardLayout cardLayout;
    private final JPanel screenRoot;
    private final JLabel statusLabel;

    private final DefaultListModel<Company> companiesModel;
    private final JList<Company> companiesList;
    private final JLabel managerSummaryLabel;
    private final JButton openCompanyButton;

    private final DefaultListModel<Car> carsModel;
    private final JList<Car> carsList;
    private final JLabel companyTitleLabel;
    private final JLabel companySummaryLabel;
    private final JButton createCarButton;
    private Company selectedCompany;

    private final DefaultListModel<Customer> customersModel;
    private final JList<Customer> customersList;
    private final JLabel customerSelectSummaryLabel;
    private final JButton openCustomerButton;

    private Customer selectedCustomer;
    private final JLabel customerTitleLabel;
    private final JTextArea customerStatusArea;
    private final JButton rentCarButton;
    private final JButton returnCarButton;
    private final JButton showRentedCarButton;

    public MainWindow(Repository repository) {
        this.repository = repository;

        setTitle("Car Rental App");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(940, 620));
        setLocationRelativeTo(null);

        companiesModel = new DefaultListModel<>();
        companiesList = new JList<>(companiesModel);
        managerSummaryLabel = secondaryLabel();
        openCompanyButton = new JButton("Open Company");

        carsModel = new DefaultListModel<>();
        carsList = new JList<>(carsModel);
        companyTitleLabel = pageTitle("Company");
        companySummaryLabel = secondaryLabel();
        createCarButton = new JButton("Add Car");

        customersModel = new DefaultListModel<>();
        customersList = new JList<>(customersModel);
        customerSelectSummaryLabel = secondaryLabel();
        openCustomerButton = new JButton("Open Customer");

        customerTitleLabel = pageTitle("Customer");
        customerStatusArea = new JTextArea();
        rentCarButton = new JButton("Rent Car");
        returnCarButton = new JButton("Return Car");
        showRentedCarButton = new JButton("My Rental");

        cardLayout = new CardLayout();
        screenRoot = new JPanel(cardLayout);

        screenRoot.add(createMainPanel(), "main");
        screenRoot.add(createManagerPanel(), "manager");
        screenRoot.add(createCompanyPanel(), "company");
        screenRoot.add(createCustomerSelectPanel(), "customer-select");
        screenRoot.add(createCustomerPanel(), "customer");

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(6, 14, 8, 14));

        JPanel shell = new JPanel(new BorderLayout());
        shell.add(screenRoot, BorderLayout.CENTER);
        shell.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(shell);

        configureLists();
        configureCustomerStatusArea();
        showMain();
    }

    public void showWindow() {
        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = pagePanel();

        JLabel title = pageTitle("Car Rental");
        JLabel subtitle = secondaryLabel("Choose your role to continue.");

        JButton managerButton = wideButton("Manager Workspace", event -> openManagerWorkspace());
        JButton customerButton = wideButton("Customer Workspace", event -> openCustomerSelection());
        JButton registerButton = wideButton("Register New Customer", event -> {
            if (createCustomer()) {
                openCustomerSelection();
            }
        });
        JButton exitButton = wideButton("Exit", event -> dispose());

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(18));
        panel.add(managerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(customerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(exitButton);

        return wrapInCenter(panel);
    }

    private JPanel createManagerPanel() {
        JPanel panel = contentPanel();

        JLabel title = pageTitle("Manager Workspace");
        panel.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(managerSummaryLabel, BorderLayout.NORTH);

        companiesList.setVisibleRowCount(14);
        center.add(new JScrollPane(companiesList), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel actions = leftActions();
        openCompanyButton.addActionListener(event -> openSelectedCompany());

        JButton createCompanyButton = new JButton("Create Company");
        createCompanyButton.addActionListener(event -> {
            if (createCompany()) {
                refreshCompanies();
                setStatus("Company created.");
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> {
            refreshCompanies();
            setStatus("Company list refreshed.");
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> showMain());

        actions.add(openCompanyButton);
        actions.add(createCompanyButton);
        actions.add(refreshButton);
        actions.add(backButton);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCompanyPanel() {
        JPanel panel = contentPanel();

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(companyTitleLabel);
        top.add(Box.createVerticalStrut(4));
        top.add(companySummaryLabel);
        panel.add(top, BorderLayout.NORTH);

        carsList.setVisibleRowCount(14);
        panel.add(new JScrollPane(carsList), BorderLayout.CENTER);

        JPanel actions = leftActions();
        createCarButton.addActionListener(event -> {
            if (selectedCompany == null) {
                return;
            }
            if (createCar(selectedCompany)) {
                refreshCompanyCars(selectedCompany);
                setStatus("Car added to %s.".formatted(selectedCompany.getName()));
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> {
            if (selectedCompany != null) {
                refreshCompanyCars(selectedCompany);
                setStatus("Car list refreshed.");
            }
        });

        JButton backButton = new JButton("Back to Companies");
        backButton.addActionListener(event -> {
            refreshCompanies();
            cardLayout.show(screenRoot, "manager");
            setStatus("Back to manager workspace.");
        });

        actions.add(createCarButton);
        actions.add(refreshButton);
        actions.add(backButton);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerSelectPanel() {
        JPanel panel = contentPanel();

        JLabel title = pageTitle("Customer Workspace");
        panel.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(customerSelectSummaryLabel, BorderLayout.NORTH);

        customersList.setVisibleRowCount(14);
        center.add(new JScrollPane(customersList), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel actions = leftActions();
        openCustomerButton.addActionListener(event -> openSelectedCustomer());

        JButton registerButton = new JButton("Register Customer");
        registerButton.addActionListener(event -> {
            if (createCustomer()) {
                refreshCustomers();
                setStatus("Customer registered.");
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> {
            refreshCustomers();
            setStatus("Customer list refreshed.");
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> showMain());

        actions.add(openCustomerButton);
        actions.add(registerButton);
        actions.add(refreshButton);
        actions.add(backButton);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = contentPanel();

        panel.add(customerTitleLabel, BorderLayout.NORTH);
        panel.add(createCustomerStatusCard(), BorderLayout.CENTER);

        JPanel actions = leftActions();
        rentCarButton.addActionListener(event -> rentCar());
        returnCarButton.addActionListener(event -> returnCar());
        showRentedCarButton.addActionListener(event -> showRentedCar());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshCustomerStatus());

        JButton backButton = new JButton("Back to Customers");
        backButton.addActionListener(event -> {
            selectedCustomer = null;
            refreshCustomers();
            cardLayout.show(screenRoot, "customer-select");
            setStatus("Back to customer workspace.");
        });

        actions.add(rentCarButton);
        actions.add(returnCarButton);
        actions.add(showRentedCarButton);
        actions.add(refreshButton);
        actions.add(backButton);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerStatusCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel header = new JLabel("Rental Status");
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        card.add(header, BorderLayout.NORTH);

        card.add(new JScrollPane(customerStatusArea), BorderLayout.CENTER);
        return card;
    }

    private void configureLists() {
        companiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        companiesList.setCellRenderer(simpleRenderer(Company::getName));
        companiesList.addListSelectionListener(event -> updateManagerActions());
        companiesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && companiesList.getSelectedValue() != null) {
                    openSelectedCompany();
                }
            }
        });

        carsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carsList.setCellRenderer(simpleRenderer(Car::getName));

        customersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersList.setCellRenderer(simpleRenderer(Customer::getName));
        customersList.addListSelectionListener(event -> updateCustomerSelectActions());
        customersList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && customersList.getSelectedValue() != null) {
                    openSelectedCustomer();
                }
            }
        });
    }

    private void configureCustomerStatusArea() {
        customerStatusArea.setEditable(false);
        customerStatusArea.setFocusable(false);
        customerStatusArea.setLineWrap(true);
        customerStatusArea.setWrapStyleWord(true);
        customerStatusArea.setBackground(UIManager.getColor("Panel.background"));
        customerStatusArea.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
    }

    private void openManagerWorkspace() {
        refreshCompanies();
        cardLayout.show(screenRoot, "manager");
        setStatus("Manager workspace opened.");
    }

    private void openCustomerSelection() {
        refreshCustomers();
        cardLayout.show(screenRoot, "customer-select");
        setStatus("Customer workspace opened.");
    }

    private void showMain() {
        selectedCustomer = null;
        selectedCompany = null;
        cardLayout.show(screenRoot, "main");
        setStatus("Main menu.");
    }

    private void refreshCompanies() {
        companiesModel.clear();
        List<Company> companies = repository.listCompanies();
        for (Company company : companies) {
            companiesModel.addElement(company);
        }

        managerSummaryLabel.setText(companies.isEmpty()
                ? "No companies yet. Create one to start building your fleet."
                : "Total companies: %d".formatted(companies.size()));

        updateManagerActions();
    }

    private void refreshCompanyCars(Company company) {
        selectedCompany = company;
        carsModel.clear();

        List<Car> cars = repository.listCarsByCompanyId(company.getId());
        for (Car car : cars) {
            carsModel.addElement(car);
        }

        companyTitleLabel.setText("Company: %s".formatted(company.getName()));
        companySummaryLabel.setText(cars.isEmpty()
                ? "No cars yet for this company."
                : "Cars in fleet: %d".formatted(cars.size()));

        createCarButton.setEnabled(true);
    }

    private void refreshCustomers() {
        customersModel.clear();
        List<Customer> customers = repository.listCustomers();
        for (Customer customer : customers) {
            customersModel.addElement(customer);
        }

        customerSelectSummaryLabel.setText(customers.isEmpty()
                ? "No customers registered. Register a customer to continue."
                : "Registered customers: %d".formatted(customers.size()));

        updateCustomerSelectActions();
    }

    private void openSelectedCompany() {
        Company company = companiesList.getSelectedValue();
        if (company == null) {
            showInfo("Select a company first.");
            return;
        }
        refreshCompanyCars(company);
        cardLayout.show(screenRoot, "company");
        setStatus("Viewing company: %s".formatted(company.getName()));
    }

    private void openSelectedCustomer() {
        Customer customer = customersList.getSelectedValue();
        if (customer == null) {
            showInfo("Select a customer first.");
            return;
        }

        Customer dbCustomer = repository.findCustomerById(customer.getId());
        if (dbCustomer == null) {
            showInfo("Customer not found. Refreshing list.");
            refreshCustomers();
            return;
        }

        selectedCustomer = dbCustomer;
        refreshCustomerStatus();
        cardLayout.show(screenRoot, "customer");
        setStatus("Opened customer: %s".formatted(selectedCustomer.getName()));
    }

    private boolean createCompany() {
        String companyName = promptRequiredText("Create Company", "Enter company name:");
        if (companyName == null) {
            return false;
        }
        repository.addCompany(companyName);
        showInfo("Company created successfully.");
        return true;
    }

    private boolean createCar(Company company) {
        String carName = promptRequiredText("Add Car", "Enter car name:");
        if (carName == null) {
            return false;
        }
        repository.addCompanyCar(carName, company.getId());
        showInfo("Car added successfully.");
        return true;
    }

    private boolean createCustomer() {
        String customerName = promptRequiredText("Register Customer", "Enter customer name:");
        if (customerName == null) {
            return false;
        }
        repository.addCustomer(customerName);
        showInfo("Customer registered successfully.");
        return true;
    }

    private void rentCar() {
        if (!reloadSelectedCustomer()) {
            return;
        }
        if (selectedCustomer.getRentedCarId() != null) {
            showInfo("This customer already has a rented car.");
            refreshCustomerStatus();
            return;
        }

        List<Company> companies = repository.listCompanies();
        if (companies.isEmpty()) {
            showInfo("No companies available yet.");
            return;
        }

        Company company = chooseFromList("Rent Car", "Choose company:", companies, Company::getName);
        if (company == null) {
            setStatus("Rent flow cancelled.");
            return;
        }

        List<Car> availableCars = repository.listAvailableCarsByCompanyId(company.getId());
        if (availableCars.isEmpty()) {
            showInfo("No available cars in this company.");
            return;
        }

        Car car = chooseFromList("Rent Car", "Choose available car:", availableCars, Car::getName);
        if (car == null) {
            setStatus("Rent flow cancelled.");
            return;
        }

        selectedCustomer.setRentedCarId(car.getId());
        repository.updateCustomer(selectedCustomer);

        showInfo("%s rented '%s'.".formatted(selectedCustomer.getName(), car.getName()));
        refreshCustomerStatus();
        setStatus("Car rented: %s".formatted(car.getName()));
    }

    private void returnCar() {
        if (!reloadSelectedCustomer()) {
            return;
        }
        if (selectedCustomer.getRentedCarId() == null) {
            showInfo("No rented car to return.");
            refreshCustomerStatus();
            return;
        }

        selectedCustomer.setRentedCarId(null);
        repository.updateCustomer(selectedCustomer);
        showInfo("Rented car returned successfully.");

        refreshCustomerStatus();
        setStatus("Car returned.");
    }

    private void showRentedCar() {
        if (!reloadSelectedCustomer()) {
            return;
        }

        Integer rentedCarId = selectedCustomer.getRentedCarId();
        if (rentedCarId == null) {
            showInfo("No rented car.");
            return;
        }

        Car car = repository.findCarById(rentedCarId);
        if (car == null) {
            showInfo("Rented car record not found.");
            refreshCustomerStatus();
            return;
        }

        Company company = repository.findCompanyById(car.getCompanyId());
        String companyName = company == null ? "Unknown" : company.getName();

        showInfo("Customer: %s\nCar: %s\nCompany: %s".formatted(selectedCustomer.getName(), car.getName(), companyName));
        setStatus("Viewed rental details.");
    }

    private void refreshCustomerStatus() {
        if (selectedCustomer == null) {
            customerTitleLabel.setText("Customer");
            customerStatusArea.setText("No customer selected.");
            updateCustomerActions();
            return;
        }

        Customer dbCustomer = repository.findCustomerById(selectedCustomer.getId());
        if (dbCustomer == null) {
            selectedCustomer = null;
            customerTitleLabel.setText("Customer");
            customerStatusArea.setText("Selected customer no longer exists.");
            updateCustomerActions();
            return;
        }

        selectedCustomer = dbCustomer;
        customerTitleLabel.setText("Customer: %s".formatted(selectedCustomer.getName()));

        if (selectedCustomer.getRentedCarId() == null) {
            customerStatusArea.setText("Status: No active rental.\nAction: Use 'Rent Car' to start a rental.");
            updateCustomerActions();
            return;
        }

        Car car = repository.findCarById(selectedCustomer.getRentedCarId());
        if (car == null) {
            customerStatusArea.setText("Status: Rental record exists but car details are unavailable.");
            updateCustomerActions();
            return;
        }

        Company company = repository.findCompanyById(car.getCompanyId());
        String companyName = company == null ? "Unknown" : company.getName();

        customerStatusArea.setText("Status: Active rental\nCar: %s\nCompany: %s".formatted(car.getName(), companyName));
        updateCustomerActions();
    }

    private boolean reloadSelectedCustomer() {
        if (selectedCustomer == null) {
            showInfo("Select a customer first.");
            return false;
        }

        Customer dbCustomer = repository.findCustomerById(selectedCustomer.getId());
        if (dbCustomer == null) {
            showInfo("Customer no longer exists.");
            selectedCustomer = null;
            cardLayout.show(screenRoot, "customer-select");
            refreshCustomers();
            return false;
        }

        selectedCustomer = dbCustomer;
        return true;
    }

    private void updateManagerActions() {
        openCompanyButton.setEnabled(companiesList.getSelectedValue() != null);
    }

    private void updateCustomerSelectActions() {
        openCustomerButton.setEnabled(customersList.getSelectedValue() != null);
    }

    private void updateCustomerActions() {
        boolean hasCustomer = selectedCustomer != null;
        boolean hasRental = hasCustomer && selectedCustomer.getRentedCarId() != null;

        rentCarButton.setEnabled(hasCustomer && !hasRental);
        returnCarButton.setEnabled(hasRental);
        showRentedCarButton.setEnabled(hasRental);
    }

    private String promptRequiredText(String title, String message) {
        String value = JOptionPane.showInputDialog(this, message, title, JOptionPane.PLAIN_MESSAGE);
        if (value == null) {
            return null;
        }

        String text = value.trim();
        if (text.isEmpty()) {
            showInfo("Value cannot be empty.");
            return null;
        }

        return text;
    }

    private <T> T chooseFromList(String title, String message, List<T> values, Function<T, String> labeler) {
        DefaultComboBoxModel<ChoiceItem<T>> model = new DefaultComboBoxModel<>();
        for (T value : values) {
            model.addElement(new ChoiceItem<>(value, labeler.apply(value)));
        }

        JComboBox<ChoiceItem<T>> comboBox = new JComboBox<>(model);
        int option = JOptionPane.showConfirmDialog(
                this,
                new Object[]{message, comboBox},
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return null;
        }

        int selectedIndex = comboBox.getSelectedIndex();
        if (selectedIndex < 0) {
            return null;
        }

        return model.getElementAt(selectedIndex).value();
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private <T> ListCellRenderer<T> simpleRenderer(Function<T, String> labeler) {
        return (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : labeler.apply(value));
            label.setBorder(new EmptyBorder(6, 8, 6, 8));
            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        };
    }

    private JLabel pageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 21f));
        return label;
    }

    private JLabel secondaryLabel() {
        return secondaryLabel("");
    }

    private JLabel secondaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UIManager.getColor("Label.disabledForeground"));
        return label;
    }

    private JButton wideButton(String text, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(340, 42));
        button.addActionListener(actionListener);
        return button;
    }

    private JPanel pagePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        return panel;
    }

    private JPanel contentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JPanel leftActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        return panel;
    }

    private JPanel wrapInCenter(JPanel content) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(content);
        return wrapper;
    }

    private record ChoiceItem<T>(T value, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}

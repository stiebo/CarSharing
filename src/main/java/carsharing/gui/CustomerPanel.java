package carsharing.gui;

import carsharing.business.CustomerService;
import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {

    private final CustomerService customerService;

    private final DefaultListModel<Customer> customerModel = new DefaultListModel<>();
    private final JList<Customer>            customerList  = new JList<>(customerModel);
    private final JLabel                     customerCount = new JLabel();

    // detail area
    private final JLabel  detailName     = new JLabel();
    private final JLabel  rentedCarLabel = new JLabel();
    private final JLabel  rentedCompany  = new JLabel();
    private final JButton rentBtn        = buildAccentButton("Rent a Car",   MainWindow.ACCENT);
    private final JButton returnBtn      = buildAccentButton("Return Car",   new Color(0xD13438));
    private final JPanel  detailCard     = new JPanel();
    private final JLabel  placeholderLbl;

    public CustomerPanel(CustomerService customerService) {
        this.customerService = customerService;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildListColumn(), buildDetailColumn());
        split.setDividerLocation(320);
        split.setResizeWeight(0.35);
        split.setBorder(null);
        split.setDividerSize(1);
        add(split, BorderLayout.CENTER);

        placeholderLbl = buildPlaceholder();

        customerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateDetail(customerList.getSelectedValue());
        });

        refresh();
    }

    // ------------------------------------------------------------------ header

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xE5E5E5)));
        JLabel title = new JLabel("Customers");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(MainWindow.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(24, 28, 16, 28));
        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ------------------------------------------------------------------ list column

    private JPanel buildListColumn() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 12));

        JPanel subHeader = new JPanel(new BorderLayout());
        subHeader.setOpaque(false);
        JLabel lbl = new JLabel("Customer List");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(MainWindow.TEXT_PRIMARY);
        customerCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerCount.setForeground(MainWindow.TEXT_SECONDARY);
        subHeader.add(lbl,           BorderLayout.WEST);
        subHeader.add(customerCount, BorderLayout.EAST);
        subHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        customerList.setCellRenderer(new CustomerCellRenderer());
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerList.setFixedCellHeight(44);

        JScrollPane scroll = new JScrollPane(customerList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE5E5E5)));

        JButton addBtn = buildAccentButton("+ New Customer", MainWindow.ACCENT);
        addBtn.addActionListener(e -> promptAddCustomer());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        btnRow.setOpaque(false);
        btnRow.add(addBtn);

        panel.add(subHeader, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        panel.add(btnRow,    BorderLayout.SOUTH);
        return panel;
    }

    // ------------------------------------------------------------------ detail column

    private JPanel buildDetailColumn() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(0xFAFAFA));
        wrapper.setBorder(new EmptyBorder(20, 12, 20, 24));

        // --- card ---
        detailCard.setLayout(new GridBagLayout());
        detailCard.setBackground(Color.WHITE);
        detailCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5E5E5)),
                new EmptyBorder(24, 28, 24, 28)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridx = 0; gbc.weightx = 1;

        detailName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailName.setForeground(MainWindow.TEXT_PRIMARY);

        JLabel rentedHeader = new JLabel("Rented Car");
        rentedHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rentedHeader.setForeground(MainWindow.TEXT_SECONDARY);
        rentedHeader.setBorder(new EmptyBorder(16, 0, 2, 0));

        rentedCarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rentedCarLabel.setForeground(MainWindow.TEXT_PRIMARY);

        rentedCompany.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rentedCompany.setForeground(MainWindow.TEXT_SECONDARY);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xEEEEEE));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        rentBtn.addActionListener(e -> handleRent());
        returnBtn.addActionListener(e -> handleReturn());
        btnPanel.add(rentBtn);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(returnBtn);

        gbc.gridy = 0; detailCard.add(detailName,    gbc);
        gbc.gridy = 1; detailCard.add(sep,           gbc);
        gbc.gridy = 2; detailCard.add(rentedHeader,  gbc);
        gbc.gridy = 3; detailCard.add(rentedCarLabel,gbc);
        gbc.gridy = 4; detailCard.add(rentedCompany, gbc);
        gbc.gridy = 5; detailCard.add(btnPanel,      gbc);

        detailCard.setVisible(false);

        wrapper.add(detailCard, BorderLayout.NORTH);
        return wrapper;
    }

    private JLabel buildPlaceholder() {
        JLabel lbl = new JLabel("Select a customer to view details");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(MainWindow.TEXT_SECONDARY);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    // ------------------------------------------------------------------ logic

    public void refresh() {
        Customer prev = customerList.getSelectedValue();
        List<Customer> customers = customerService.getCustomers();
        customerModel.clear();
        customers.forEach(customerModel::addElement);
        customerCount.setText(customers.size() + " customers");

        if (prev != null) {
            for (int i = 0; i < customerModel.size(); i++) {
                if (customerModel.get(i).getId() == prev.getId()) {
                    customerList.setSelectedIndex(i);
                    updateDetail(customerModel.get(i));
                    return;
                }
            }
        }
        detailCard.setVisible(false);
    }

    private void updateDetail(Customer c) {
        if (c == null) {
            detailCard.setVisible(false);
            return;
        }
        // always reload fresh from DB
        Customer fresh = customerService.refreshCustomer(c);
        detailName.setText(fresh.getName());

        Car rentedCar = customerService.getRentedCar(fresh);
        if (rentedCar != null) {
            Company co = customerService.getCompanyForCar(rentedCar);
            rentedCarLabel.setText(rentedCar.getName());
            rentedCompany.setText(co != null ? co.getName() : "—");
            rentBtn.setEnabled(false);
            returnBtn.setEnabled(true);
        } else {
            rentedCarLabel.setText("None");
            rentedCompany.setText("");
            rentBtn.setEnabled(true);
            returnBtn.setEnabled(false);
        }
        detailCard.setVisible(true);
        detailCard.revalidate();
    }

    private void handleRent() {
        Customer c = customerList.getSelectedValue();
        if (c == null) return;
        Customer fresh = customerService.refreshCustomer(c);

        List<Company> companies = customerService.getCompanies();
        if (companies.isEmpty()) {
            showInfo("No companies available.");
            return;
        }

        // Step 1: choose company
        Company chosenCompany = (Company) JOptionPane.showInputDialog(this,
                "Choose a company:", "Rent a Car — Step 1/2",
                JOptionPane.PLAIN_MESSAGE, null,
                companies.toArray(), companies.get(0));
        if (chosenCompany == null) return;

        // Step 2: choose car
        List<Car> cars = customerService.getAvailableCarsForCompany(chosenCompany);
        if (cars.isEmpty()) {
            showInfo("No available cars at " + chosenCompany.getName() + ".");
            return;
        }

        Car chosenCar = (Car) JOptionPane.showInputDialog(this,
                "Choose a car from " + chosenCompany.getName() + ":", "Rent a Car — Step 2/2",
                JOptionPane.PLAIN_MESSAGE, null,
                cars.toArray(), cars.get(0));
        if (chosenCar == null) return;

        customerService.rentCarForCustomer(fresh, chosenCar);
        refresh();
    }

    private void handleReturn() {
        Customer c = customerList.getSelectedValue();
        if (c == null) return;
        Customer fresh = customerService.refreshCustomer(c);
        customerService.returnCarForCustomer(fresh);
        refresh();
    }

    private void promptAddCustomer() {
        JTextField field = new JTextField(24);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        Object[] message = {"Enter customer name:", field};
        int result = JOptionPane.showConfirmDialog(this, message, "New Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && !field.getText().isBlank()) {
            customerService.addCustomer(field.getText().trim());
            refresh();
        }
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "CarShare", JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------------ button helper

    private static JButton buildAccentButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 34));
        return btn;
    }

    // ------------------------------------------------------------------ cell renderer

    private class CustomerCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean hasFocus) {
            Customer c = (Customer) value;
            Car rentedCar = customerService.getRentedCar(c);

            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBorder(new EmptyBorder(6, 12, 6, 12));

            JLabel name = new JLabel(c.getName());
            name.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel badge = new JLabel(rentedCar != null ? "● renting" : "");
            badge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            badge.setForeground(rentedCar != null ? new Color(0x107C10) : new Color(0x767676));

            row.add(name,  BorderLayout.CENTER);
            row.add(badge, BorderLayout.EAST);

            if (isSelected) {
                row.setBackground(MainWindow.ACCENT);
                name.setForeground(Color.WHITE);
                badge.setForeground(Color.WHITE);
            } else {
                row.setBackground(index % 2 == 0 ? Color.WHITE : new Color(0xFAFAFA));
                name.setForeground(MainWindow.TEXT_PRIMARY);
            }
            return row;
        }
    }
}

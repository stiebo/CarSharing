package carsharing.gui;

import carsharing.business.CustomerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class CreateCustomerPanel extends JPanel {

    private final CustomerService customerService;
    private final Runnable onCreated;

    private final JTextField nameField = new JTextField(28);
    private final JLabel     statusLbl = new JLabel(" ");

    public CreateCustomerPanel(CustomerService customerService, Runnable onCreated) {
        this.customerService = customerService;
        this.onCreated       = onCreated;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(),   BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------ header

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xE5E5E5)));
        JLabel title = new JLabel("Create Customer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(MainWindow.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(24, 28, 16, 28));
        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ------------------------------------------------------------------ form

    private JPanel buildForm() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Color.WHITE);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5E5E5)),
                new EmptyBorder(36, 40, 36, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(6, 0, 6, 0);

        // title inside card
        JLabel cardTitle = new JLabel("New Customer");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(MainWindow.TEXT_PRIMARY);

        JLabel subTitle = new JLabel("Enter the customer's name below. They will appear in the Customers list.");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitle.setForeground(MainWindow.TEXT_SECONDARY);

        // name field
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(MainWindow.TEXT_PRIMARY);
        nameLabel.setBorder(new EmptyBorder(16, 0, 4, 0));

        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameField.setPreferredSize(new Dimension(0, 36));

        // status label (feedback)
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLbl.setForeground(new Color(0x107C10));
        statusLbl.setBorder(new EmptyBorder(4, 0, 0, 0));

        // button
        JButton createBtn = new JButton("Create Customer");
        createBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        createBtn.setBackground(MainWindow.ACCENT);
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        createBtn.setBorderPainted(false);
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.setPreferredSize(new Dimension(0, 36));
        createBtn.setBorder(new EmptyBorder(8, 20, 8, 20));

        createBtn.addActionListener(e -> handleCreate());
        // also trigger on Enter in the text field
        nameField.addActionListener(e -> handleCreate());

        gbc.gridy = 0; card.add(cardTitle,  gbc);
        gbc.gridy = 1; card.add(subTitle,   gbc);
        gbc.gridy = 2; card.add(nameLabel,  gbc);
        gbc.gridy = 3; card.add(nameField,  gbc);
        gbc.gridy = 4; card.add(statusLbl,  gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(20, 0, 0, 0));
        btnRow.add(createBtn);
        gbc.gridy = 5; card.add(btnRow, gbc);

        // center the card in the outer panel
        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.gridx = 0; outerGbc.gridy = 0;
        outerGbc.fill = GridBagConstraints.NONE;
        outerGbc.anchor = GridBagConstraints.NORTH;
        outerGbc.insets = new Insets(40, 0, 0, 0);
        card.setPreferredSize(new Dimension(460, 300));
        outer.add(card, outerGbc);
        return outer;
    }

    // ------------------------------------------------------------------ logic

    private void handleCreate() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLbl.setForeground(new Color(0xD13438));
            statusLbl.setText("Please enter a name.");
            return;
        }
        customerService.addCustomer(name);
        nameField.setText("");
        statusLbl.setForeground(new Color(0x107C10));
        statusLbl.setText("Customer \"" + name + "\" created successfully.");

        // navigate to customer list after a short delay
        Timer t = new Timer(800, e -> onCreated.run());
        t.setRepeats(false);
        t.start();
    }
}

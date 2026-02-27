package carsharing.gui;

import carsharing.business.ManagerService;
import carsharing.domain.Car;
import carsharing.domain.Company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

public class ManagerPanel extends JPanel {

    private final ManagerService managerService;

    // --- company column ---
    private final DefaultListModel<Company> companyModel = new DefaultListModel<>();
    private final JList<Company>            companyList  = new JList<>(companyModel);
    private final JLabel                    companyCount = new JLabel();

    // --- car column ---
    private final DefaultListModel<Car> carModel = new DefaultListModel<>();
    private final JList<Car>            carList  = new JList<>(carModel);
    private final JLabel                carCount = new JLabel();
    private final JButton               addCarBtn;
    private       Company               selectedCompany = null;

    public ManagerPanel(ManagerService managerService) {
        this.managerService = managerService;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // page title
        add(buildHeader(), BorderLayout.NORTH);

        // two-column content
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildCompanyColumn(), buildCarColumn());
        split.setDividerLocation(380);
        split.setResizeWeight(0.45);
        split.setBorder(null);
        split.setDividerSize(1);
        add(split, BorderLayout.CENTER);

        addCarBtn = findAddCarButton();
        refresh();
    }

    // ------------------------------------------------------------------ header

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xE5E5E5)));

        JLabel title = new JLabel("Manager Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(MainWindow.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(24, 28, 16, 28));

        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ------------------------------------------------------------------ company column

    private JPanel buildCompanyColumn() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 12));

        // sub-header
        JPanel subHeader = new JPanel(new BorderLayout());
        subHeader.setOpaque(false);
        JLabel lbl = new JLabel("Companies");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(MainWindow.TEXT_PRIMARY);
        companyCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        companyCount.setForeground(MainWindow.TEXT_SECONDARY);
        subHeader.add(lbl,           BorderLayout.WEST);
        subHeader.add(companyCount,  BorderLayout.EAST);
        subHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        // list
        companyList.setCellRenderer(new EntityCellRenderer<Company>(c -> c.getName()));
        companyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        companyList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        companyList.setFixedCellHeight(40);
        companyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedCompany = companyList.getSelectedValue();
                refreshCars();
            }
        });

        JScrollPane scroll = new JScrollPane(companyList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE5E5E5)));

        // add button
        JButton addBtn = buildAccentButton("+ New Company");
        addBtn.addActionListener(e -> promptAddCompany());

        panel.add(subHeader, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        btnRow.setOpaque(false);
        btnRow.add(addBtn);
        panel.add(btnRow, BorderLayout.SOUTH);
        return panel;
    }

    // ------------------------------------------------------------------ car column

    private JPanel buildCarColumn() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xFAFAFA));
        panel.setBorder(new EmptyBorder(20, 12, 20, 24));

        JPanel subHeader = new JPanel(new BorderLayout());
        subHeader.setOpaque(false);
        JLabel lbl = new JLabel("Cars");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(MainWindow.TEXT_PRIMARY);
        carCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        carCount.setForeground(MainWindow.TEXT_SECONDARY);
        subHeader.add(lbl,      BorderLayout.WEST);
        subHeader.add(carCount, BorderLayout.EAST);
        subHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        carList.setCellRenderer(new EntityCellRenderer<Car>(c -> c.getName()));
        carList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        carList.setFixedCellHeight(40);
        carList.setBackground(new Color(0xFAFAFA));

        JScrollPane scroll = new JScrollPane(carList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE5E5E5)));
        scroll.getViewport().setBackground(new Color(0xFAFAFA));

        JButton addBtn = buildAccentButton("+ New Car");
        addBtn.setEnabled(false);
        addBtn.setName("addCar");
        addBtn.addActionListener(e -> promptAddCar());

        panel.add(subHeader, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        btnRow.setOpaque(false);
        btnRow.add(addBtn);
        panel.add(btnRow, BorderLayout.SOUTH);
        return panel;
    }

    // ------------------------------------------------------------------ helpers

    private JButton findAddCarButton() {
        // locate the button we named "addCar"
        return findButton(this, "addCar");
    }

    private static JButton findButton(Container c, String name) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JButton b && name.equals(b.getName())) return b;
            if (comp instanceof Container sub) {
                JButton found = findButton(sub, name);
                if (found != null) return found;
            }
        }
        return null;
    }

    private JButton buildAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(MainWindow.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 34));
        return btn;
    }

    // ------------------------------------------------------------------ data

    public void refresh() {
        List<Company> companies = managerService.getCompanies();
        companyModel.clear();
        companies.forEach(companyModel::addElement);
        companyCount.setText(companies.size() + " items");

        // if previously selected company still exists, keep it
        if (selectedCompany != null) {
            for (int i = 0; i < companyModel.size(); i++) {
                if (companyModel.get(i).getId() == selectedCompany.getId()) {
                    companyList.setSelectedIndex(i);
                    break;
                }
            }
        }
        refreshCars();
    }

    private void refreshCars() {
        carModel.clear();
        boolean hasSelection = selectedCompany != null;
        if (hasSelection) {
            List<Car> cars = managerService.getCarsForCompany(selectedCompany);
            cars.forEach(carModel::addElement);
            carCount.setText(cars.size() + " items");
        } else {
            carCount.setText("select a company");
        }
        if (addCarBtn != null) addCarBtn.setEnabled(hasSelection);
    }

    private void promptAddCompany() {
        String name = showInputDialog("Enter company name:");
        if (name != null && !name.isBlank()) {
            managerService.addCompany(name.trim());
            refresh();
        }
    }

    private void promptAddCar() {
        if (selectedCompany == null) return;
        String name = showInputDialog("Enter car name for " + selectedCompany.getName() + ":");
        if (name != null && !name.isBlank()) {
            managerService.addCar(name.trim(), selectedCompany);
            refreshCars();
        }
    }

    private String showInputDialog(String prompt) {
        JTextField field = new JTextField(24);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        Object[] message = {prompt, field};
        int result = JOptionPane.showConfirmDialog(this, message, "CarShare",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION ? field.getText() : null;
    }

    // ------------------------------------------------------------------ cell renderer

    private static class EntityCellRenderer<T> extends DefaultListCellRenderer {
        private final java.util.function.Function<T, String> namer;
        EntityCellRenderer(java.util.function.Function<T, String> namer) { this.namer = namer; }

        @Override
        @SuppressWarnings("unchecked")
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean hasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, namer.apply((T) value), index, isSelected, hasFocus);
            lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
            if (isSelected) {
                lbl.setBackground(MainWindow.ACCENT);
                lbl.setForeground(Color.WHITE);
            }
            return lbl;
        }
    }
}

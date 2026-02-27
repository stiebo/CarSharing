package carsharing.gui;

import carsharing.business.CustomerService;
import carsharing.business.ManagerService;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {

    // Windows 11 accent blue
    public static final Color ACCENT       = new Color(0x0078D4);
    public static final Color ACCENT_HOVER = new Color(0x006CC0);
    public static final Color SIDEBAR_BG   = new Color(0xF3F3F3);
    public static final Color SIDEBAR_ACTIVE  = new Color(0xE0E0E0);
    public static final Color TEXT_PRIMARY = new Color(0x1A1A1A);
    public static final Color TEXT_SECONDARY = new Color(0x5C5C5C);

    private static final String CARD_MANAGER  = "manager";
    private static final String CARD_CUSTOMER = "customer";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel  = new JPanel(cardLayout);

    private ManagerPanel  managerPanel;
    private CustomerPanel customerPanel;

    private JButton activeNavBtn = null;

    public MainWindow(ManagerService managerService, CustomerService customerService) {
        super("CarShare");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setPreferredSize(new Dimension(1100, 680));

        managerPanel  = new ManagerPanel(managerService);
        customerPanel = new CustomerPanel(customerService);

        contentPanel.add(managerPanel,  CARD_MANAGER);
        contentPanel.add(customerPanel, CARD_CUSTOMER);

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(contentPanel,   BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        showCard(CARD_MANAGER);
    }

    // ------------------------------------------------------------------ sidebar

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());

        // --- top: logo area ---
        JPanel logo = new JPanel(new BorderLayout());
        logo.setBackground(SIDEBAR_BG);
        logo.setBorder(new EmptyBorder(20, 20, 12, 20));

        JLabel icon = new JLabel("\uD83D\uDE97");  // 🚗
        icon.setFont(icon.getFont().deriveFont(28f));

        JLabel title = new JLabel("CarShare");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 8, 0, 0));

        JPanel iconRow = new JPanel(new BorderLayout());
        iconRow.setOpaque(false);
        iconRow.add(icon, BorderLayout.WEST);
        iconRow.add(title, BorderLayout.CENTER);
        logo.add(iconRow, BorderLayout.CENTER);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xDDDDDD));

        JPanel logoArea = new JPanel(new BorderLayout());
        logoArea.setOpaque(false);
        logoArea.add(logo, BorderLayout.CENTER);
        logoArea.add(sep, BorderLayout.SOUTH);
        sidebar.add(logoArea, BorderLayout.NORTH);

        // --- nav buttons ---
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(10, 8, 10, 8));

        JButton managerBtn  = createNavButton("Manager",   "\uD83D\uDCC1", CARD_MANAGER);
        JButton customerBtn = createNavButton("Customers", "\uD83D\uDC65", CARD_CUSTOMER);

        nav.add(managerBtn);
        nav.add(Box.createVerticalStrut(4));
        nav.add(customerBtn);
        nav.add(Box.createVerticalGlue());

        sidebar.add(nav, BorderLayout.CENTER);

        // --- bottom: version ---
        JLabel ver = new JLabel("v1.0");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(TEXT_SECONDARY);
        ver.setBorder(new EmptyBorder(12, 20, 14, 20));
        sidebar.add(ver, BorderLayout.SOUTH);

        // activate first button
        setActiveButton(managerBtn);
        return sidebar;
    }

    private JButton createNavButton(String text, String emoji, String card) {
        JButton btn = new JButton(emoji + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(SIDEBAR_BG);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 12, 9, 12));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(new Color(0xE8E8E8));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> {
            setActiveButton(btn);
            showCard(card);
        });
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(SIDEBAR_BG);
            activeNavBtn.setForeground(TEXT_PRIMARY);
        }
        activeNavBtn = btn;
        btn.setBackground(SIDEBAR_ACTIVE);
        btn.setForeground(ACCENT);
    }

    private void showCard(String card) {
        if (card.equals(CARD_MANAGER))  managerPanel.refresh();
        if (card.equals(CARD_CUSTOMER)) customerPanel.refresh();
        cardLayout.show(contentPanel, card);
    }

    // ------------------------------------------------------------------ launch

    public static void launch(ManagerService managerService, CustomerService customerService) {
        FlatLightLaf.setup();

        // Windows 11-style tweaks
        UIManager.put("Button.arc",           8);
        UIManager.put("Component.arc",        8);
        UIManager.put("TextComponent.arc",    8);
        UIManager.put("ScrollBar.thumbArc",   999);
        UIManager.put("ScrollBar.width",      10);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(managerService, customerService);
            window.setVisible(true);
        });
    }
}

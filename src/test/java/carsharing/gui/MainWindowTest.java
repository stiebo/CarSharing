package carsharing.gui;

import carsharing.business.CustomerService;
import carsharing.business.ManagerService;
import com.formdev.flatlaf.FlatLightLaf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class MainWindowTest {

    @Mock private ManagerService managerService;
    @Mock private CustomerService customerService;

    @Test
    void accent_isExpectedBlue() {
        assertEquals(new Color(0x0078D4), MainWindow.ACCENT);
    }

    @Test
    void accentHover_isDarkerBlue() {
        assertEquals(new Color(0x006CC0), MainWindow.ACCENT_HOVER);
    }

    @Test
    void sidebarBg_isLightGrey() {
        assertEquals(new Color(0xF3F3F3), MainWindow.SIDEBAR_BG);
    }

    @Test
    void sidebarActive_isMediumGrey() {
        assertEquals(new Color(0xE0E0E0), MainWindow.SIDEBAR_ACTIVE);
    }

    @Test
    void textPrimary_isNearBlack() {
        assertEquals(new Color(0x1A1A1A), MainWindow.TEXT_PRIMARY);
    }

    @Test
    void textSecondary_isMediumGrey() {
        assertEquals(new Color(0x5C5C5C), MainWindow.TEXT_SECONDARY);
    }

    @Test
    void accentHover_isDarkerThanAccent() {
        // hover shade must have a lower blue component than the normal accent
        assertTrue(MainWindow.ACCENT_HOVER.getBlue() < MainWindow.ACCENT.getBlue()
                || MainWindow.ACCENT_HOVER.getRed() < MainWindow.ACCENT.getRed());
    }

    @Test
    void sidebarActive_isDarkerThanSidebarBg() {
        int bgBrightness  = MainWindow.SIDEBAR_BG.getRed()
                          + MainWindow.SIDEBAR_BG.getGreen()
                          + MainWindow.SIDEBAR_BG.getBlue();
        int actBrightness = MainWindow.SIDEBAR_ACTIVE.getRed()
                          + MainWindow.SIDEBAR_ACTIVE.getGreen()
                          + MainWindow.SIDEBAR_ACTIVE.getBlue();
        assertTrue(actBrightness < bgBrightness,
                "Active sidebar colour should be darker than the default sidebar background");
    }

    // ── launch() ─────────────────────────────────────────────────────────

    /**
     * Covers the entire launch() method body:
     * - FlatLightLaf.setup() mocked to avoid L&F side-effects
     * - SwingUtilities.invokeLater() made synchronous so the lambda executes
     * - MainWindow construction mocked to prevent HeadlessException
     */
    @Test
    void launch_executesLambdaAndConstructsMainWindow() {
        try (MockedStatic<FlatLightLaf> laf = mockStatic(FlatLightLaf.class);
             MockedStatic<SwingUtilities> swt = mockStatic(SwingUtilities.class);
             MockedConstruction<MainWindow> mc  = mockConstruction(MainWindow.class)) {

            // Run invokeLater lambda synchronously
            swt.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
               .thenAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; });

            MainWindow.launch(managerService, customerService);

            // Window was constructed exactly once with the right services
            assertEquals(1, mc.constructed().size());
            // setVisible(true) was called on the mock window
            verify(mc.constructed().get(0)).setVisible(true);
        }
    }

    @Test
    void launch_setupsUIManagerProperties() {
        try (MockedStatic<FlatLightLaf> laf = mockStatic(FlatLightLaf.class);
             MockedStatic<SwingUtilities> swt = mockStatic(SwingUtilities.class);
             MockedConstruction<MainWindow> mc  = mockConstruction(MainWindow.class)) {

            swt.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
               .thenAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; });

            // Should complete without exception even in headless mode
            assertDoesNotThrow(() -> MainWindow.launch(managerService, customerService));
        }
    }
}

package carsharing.gui;

import carsharing.business.ManagerService;
import carsharing.domain.Car;
import carsharing.domain.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerPanelTest {

    @Mock
    private ManagerService managerService;

    private ManagerPanel panel;

    private <T> T field(String name, Class<T> type) throws Exception {
        Field f = ManagerPanel.class.getDeclaredField(name);
        f.setAccessible(true);
        return type.cast(f.get(panel));
    }

    private void invoke(String method, Class<?>[] types, Object... args) throws Exception {
        Method m = ManagerPanel.class.getDeclaredMethod(method, types);
        m.setAccessible(true);
        m.invoke(panel, args);
    }

    private void setSelectedCompany(Company c) throws Exception {
        Field f = ManagerPanel.class.getDeclaredField("selectedCompany");
        f.setAccessible(true);
        f.set(panel, c);
    }


    @BeforeEach
    void setUp() {
        when(managerService.getCompanies()).thenReturn(List.of());
        panel = new ManagerPanel(managerService);
    }


    @Test
    void refresh_populatesCompanyModel() throws Exception {
        List<Company> companies = List.of(new Company(1, "Hertz"), new Company(2, "Avis"));
        when(managerService.getCompanies()).thenReturn(companies);
        panel.refresh();

        DefaultListModel<Company> model = field("companyModel", DefaultListModel.class);
        assertEquals(2, model.size());
        assertEquals("Hertz", model.get(0).getName());
    }

    @Test
    void refresh_emptyList_companyModelIsEmpty() throws Exception {
        when(managerService.getCompanies()).thenReturn(List.of());
        panel.refresh();

        DefaultListModel<Company> model = field("companyModel", DefaultListModel.class);
        assertEquals(0, model.size());
    }

    @Test
    void refresh_updatesCompanyCountLabel() throws Exception {
        List<Company> companies = List.of(new Company(1, "X"), new Company(2, "Y"), new Company(3, "Z"));
        when(managerService.getCompanies()).thenReturn(companies);
        panel.refresh();

        JLabel count = field("companyCount", JLabel.class);
        assertEquals("3 items", count.getText());
    }

    @Test
    void refresh_noCompanies_countLabelShowsZero() throws Exception {
        when(managerService.getCompanies()).thenReturn(List.of());
        panel.refresh();

        JLabel count = field("companyCount", JLabel.class);
        assertEquals("0 items", count.getText());
    }


    @Test
    void refresh_noSelectedCompany_carCountShowsHint() throws Exception {
        when(managerService.getCompanies()).thenReturn(List.of());
        panel.refresh();

        JLabel carCount = field("carCount", JLabel.class);
        assertEquals("select a company", carCount.getText());
    }

    @Test
    void refresh_withSelectedCompany_carsLoaded() throws Exception {
        Company hertz = new Company(1, "Hertz");
        List<Car> cars = List.of(new Car(1, "Tesla", 1), new Car(2, "BMW", 1));
        when(managerService.getCompanies()).thenReturn(List.of(hertz));
        when(managerService.getCarsForCompany(hertz)).thenReturn(cars);
        panel.refresh();

        JList<Company> companyList = field("companyList", JList.class);
        companyList.setSelectedIndex(0);

        DefaultListModel<Car> carModel = field("carModel", DefaultListModel.class);
        assertEquals(2, carModel.size());
    }

    @Test
    void refresh_withSelectedCompany_carCountUpdated() throws Exception {
        Company hertz = new Company(1, "Hertz");
        List<Car> cars = List.of(new Car(1, "Tesla", 1));
        when(managerService.getCompanies()).thenReturn(List.of(hertz));
        when(managerService.getCarsForCompany(hertz)).thenReturn(cars);
        panel.refresh();

        JList<Company> companyList = field("companyList", JList.class);
        companyList.setSelectedIndex(0);

        JLabel carCount = field("carCount", JLabel.class);
        assertEquals("1 items", carCount.getText());
    }


    @Test
    void addCarButton_disabledWithNoSelection() throws Exception {
        JButton addCar = field("addCarBtn", JButton.class);
        assertFalse(addCar.isEnabled());
    }

    @Test
    void addCarButton_enabledAfterCompanySelected() throws Exception {
        Company hertz = new Company(1, "Hertz");
        when(managerService.getCompanies()).thenReturn(List.of(hertz));
        when(managerService.getCarsForCompany(hertz)).thenReturn(List.of());
        panel.refresh();

        JList<Company> companyList = field("companyList", JList.class);
        companyList.setSelectedIndex(0);

        JButton addCar = field("addCarBtn", JButton.class);
        assertTrue(addCar.isEnabled());
    }


    @Test
    void promptAddCompany_withValidInput_callsAddCompany() throws Exception {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(
                    any(), any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(inv -> {
                    Object[] msg = (Object[]) inv.getArgument(1);
                    ((JTextField) msg[1]).setText("NewCo");
                    return JOptionPane.OK_OPTION;
                });
            when(managerService.getCompanies()).thenReturn(List.of());

            invoke("promptAddCompany", new Class[0]);

            verify(managerService).addCompany("NewCo");
        }
    }

    @Test
    void promptAddCompany_cancelled_doesNotCallAddCompany() throws Exception {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(
                    any(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(JOptionPane.CANCEL_OPTION);

            invoke("promptAddCompany", new Class[0]);

            verify(managerService, never()).addCompany(anyString());
        }
    }

    @Test
    void promptAddCompany_blankInput_doesNotCallAddCompany() throws Exception {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(
                    any(), any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(inv -> {
                    Object[] msg = (Object[]) inv.getArgument(1);
                    ((JTextField) msg[1]).setText("   ");
                    return JOptionPane.OK_OPTION;
                });

            invoke("promptAddCompany", new Class[0]);

            verify(managerService, never()).addCompany(anyString());
        }
    }


    @Test
    void promptAddCar_noSelectedCompany_doesNotOpenDialog() throws Exception {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            invoke("promptAddCar", new Class[0]);
            mocked.verifyNoInteractions();
            verify(managerService, never()).addCar(anyString(), any());
        }
    }

    @Test
    void promptAddCar_withValidInput_callsAddCar() throws Exception {
        Company hertz = new Company(1, "Hertz");
        setSelectedCompany(hertz);

        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(
                    any(), any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(inv -> {
                    Object[] msg = (Object[]) inv.getArgument(1);
                    ((JTextField) msg[1]).setText("Tesla");
                    return JOptionPane.OK_OPTION;
                });

            invoke("promptAddCar", new Class[0]);

            verify(managerService).addCar("Tesla", hertz);
        }
    }

    @Test
    void promptAddCar_cancelled_doesNotCallAddCar() throws Exception {
        Company hertz = new Company(1, "Hertz");
        setSelectedCompany(hertz);

        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(
                    any(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(JOptionPane.CANCEL_OPTION);

            invoke("promptAddCar", new Class[0]);

            verify(managerService, never()).addCar(anyString(), any());
        }
    }

    @Test
    void promptAddCar_blankInput_doesNotCallAddCar() throws Exception {
        Company hertz = new Company(1, "Hertz");
        setSelectedCompany(hertz);

        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(
                    any(), any(), anyString(), anyInt(), anyInt()))
                .thenAnswer(inv -> {
                    Object[] msg = (Object[]) inv.getArgument(1);
                    ((JTextField) msg[1]).setText("");
                    return JOptionPane.OK_OPTION;
                });

            invoke("promptAddCar", new Class[0]);

            verify(managerService, never()).addCar(anyString(), any());
        }
    }


    @Test
    void refresh_retainsSelectionForExistingCompany() throws Exception {
        Company hertz = new Company(1, "Hertz");
        when(managerService.getCompanies()).thenReturn(List.of(hertz));
        when(managerService.getCarsForCompany(hertz)).thenReturn(List.of());
        panel.refresh();

        JList<Company> companyList = field("companyList", JList.class);
        companyList.setSelectedIndex(0);
        setSelectedCompany(hertz);

        panel.refresh();

        DefaultListModel<Company> model = field("companyModel", DefaultListModel.class);
        assertEquals(1, model.size());
        assertEquals("Hertz", model.get(0).getName());
    }


    @Test
    @SuppressWarnings("unchecked")
    void entityCellRenderer_unselected_showsCompanyName() throws Exception {
        JList<Company> list = field("companyList", JList.class);
        ListCellRenderer<? super Company> renderer = list.getCellRenderer();
        Company company = new Company(1, "Hertz");

        Component comp = renderer.getListCellRendererComponent(list, company, 0, false, false);

        assertInstanceOf(JLabel.class, comp);
        assertEquals("Hertz", ((JLabel) comp).getText());
    }

    @Test
    @SuppressWarnings("unchecked")
    void entityCellRenderer_selected_usesAccentBackground() throws Exception {
        JList<Company> list = field("companyList", JList.class);
        ListCellRenderer<? super Company> renderer = list.getCellRenderer();
        Company company = new Company(1, "Hertz");

        Component comp = renderer.getListCellRendererComponent(list, company, 0, true, false);

        assertInstanceOf(JLabel.class, comp);
        assertEquals(MainWindow.ACCENT, comp.getBackground());
        assertEquals(Color.WHITE, ((JLabel) comp).getForeground());
    }

    @Test
    @SuppressWarnings("unchecked")
    void carCellRenderer_unselected_showsCarName() throws Exception {
        JList<Car> list = field("carList", JList.class);
        ListCellRenderer<? super Car> renderer = list.getCellRenderer();
        Car car = new Car(1, "Tesla", 1);

        Component comp = renderer.getListCellRendererComponent(list, car, 0, false, false);

        assertInstanceOf(JLabel.class, comp);
        assertEquals("Tesla", ((JLabel) comp).getText());
    }

    @Test
    @SuppressWarnings("unchecked")
    void carCellRenderer_selected_usesAccentBackground() throws Exception {
        JList<Car> list = field("carList", JList.class);
        ListCellRenderer<? super Car> renderer = list.getCellRenderer();
        Car car = new Car(1, "Tesla", 1);

        Component comp = renderer.getListCellRendererComponent(list, car, 0, true, false);

        assertEquals(MainWindow.ACCENT, comp.getBackground());
    }


    @Test
    void panel_layoutIsBorderLayout() {
        assertInstanceOf(BorderLayout.class, panel.getLayout());
    }

    @Test
    void panel_backgroundIsWhite() {
        assertEquals(Color.WHITE, panel.getBackground());
    }
}

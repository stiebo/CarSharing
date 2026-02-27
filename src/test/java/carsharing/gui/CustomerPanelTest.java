package carsharing.gui;

import carsharing.business.CustomerService;
import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;
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
class CustomerPanelTest {

    @Mock
    private CustomerService customerService;

    private CustomerPanel panel;

    private <T> T field(String name, Class<T> type) throws Exception {
        Field f = CustomerPanel.class.getDeclaredField(name);
        f.setAccessible(true);
        return type.cast(f.get(panel));
    }

    private void invoke(String method, Class<?>[] types, Object... args) throws Exception {
        Method m = CustomerPanel.class.getDeclaredMethod(method, types);
        m.setAccessible(true);
        m.invoke(panel, args);
    }

    @BeforeEach
    void setUp() {
        when(customerService.getCustomers()).thenReturn(List.of());
        panel = new CustomerPanel(customerService);
    }

    //  refresh 

    @Test
    void refresh_populatesCustomerModel() throws Exception {
        List<Customer> customers = List.of(new Customer(1, "Alice", null), new Customer(2, "Bob", 3));
        when(customerService.getCustomers()).thenReturn(customers);
        panel.refresh();
        DefaultListModel<Customer> model = field("customerModel", DefaultListModel.class);
        assertEquals(2, model.size());
        assertEquals("Alice", model.get(0).getName());
    }

    @Test
    void refresh_emptyList_modelIsEmpty() throws Exception {
        when(customerService.getCustomers()).thenReturn(List.of());
        panel.refresh();
        assertEquals(0, ((DefaultListModel<Customer>) field("customerModel", DefaultListModel.class)).size());
    }

    @Test
    void refresh_updatesCustomerCountLabel() throws Exception {
        when(customerService.getCustomers()).thenReturn(List.of(new Customer(1,"A",null),new Customer(2,"B",null),new Customer(3,"C",null)));
        panel.refresh();
        assertEquals("3 customers", ((JLabel) field("customerCount", JLabel.class)).getText());
    }

    @Test
    void refresh_zeroCustomers_countLabel() throws Exception {
        when(customerService.getCustomers()).thenReturn(List.of());
        panel.refresh();
        assertEquals("0 customers", ((JLabel) field("customerCount", JLabel.class)).getText());
    }

    @Test
    void refresh_noSelection_detailCardHidden() throws Exception {
        when(customerService.getCustomers()).thenReturn(List.of());
        panel.refresh();
        assertFalse(((JPanel) field("detailCard", JPanel.class)).isVisible());
    }

    @Test
    void refresh_noSelection_placeholderVisible() throws Exception {
        when(customerService.getCustomers()).thenReturn(List.of());
        panel.refresh();
        assertTrue(((JLabel) field("placeholderLbl", JLabel.class)).isVisible());
    }

    //  updateDetail 

    @Test
    void updateDetail_null_hidesDetailCard() throws Exception {
        invoke("updateDetail", new Class[]{Customer.class}, (Object) null);
        assertFalse(((JPanel) field("detailCard", JPanel.class)).isVisible());
    }

    @Test
    void updateDetail_null_showsPlaceholder() throws Exception {
        invoke("updateDetail", new Class[]{Customer.class}, (Object) null);
        assertTrue(((JLabel) field("placeholderLbl", JLabel.class)).isVisible());
    }

    @Test
    void updateDetail_noRental_showsNone() throws Exception {
        Customer alice = new Customer(1, "Alice", null);
        when(customerService.refreshCustomer(alice)).thenReturn(alice);
        when(customerService.getRentedCar(alice)).thenReturn(null);
        invoke("updateDetail", new Class[]{Customer.class}, alice);
        assertEquals("None", ((JLabel) field("rentedCarLabel", JLabel.class)).getText());
    }

    @Test
    void updateDetail_noRental_rentEnabled() throws Exception {
        Customer alice = new Customer(1, "Alice", null);
        when(customerService.refreshCustomer(alice)).thenReturn(alice);
        when(customerService.getRentedCar(alice)).thenReturn(null);
        invoke("updateDetail", new Class[]{Customer.class}, alice);
        assertTrue(((JButton) field("rentBtn", JButton.class)).isEnabled());
    }

    @Test
    void updateDetail_noRental_returnDisabled() throws Exception {
        Customer alice = new Customer(1, "Alice", null);
        when(customerService.refreshCustomer(alice)).thenReturn(alice);
        when(customerService.getRentedCar(alice)).thenReturn(null);
        invoke("updateDetail", new Class[]{Customer.class}, alice);
        assertFalse(((JButton) field("returnBtn", JButton.class)).isEnabled());
    }

    @Test
    void updateDetail_renting_showsCarAndCompany() throws Exception {
        Customer bob = new Customer(2, "Bob", 5);
        Car car = new Car(5, "Tesla", 1);
        Company co = new Company(1, "Hertz");
        when(customerService.refreshCustomer(bob)).thenReturn(bob);
        when(customerService.getRentedCar(bob)).thenReturn(car);
        when(customerService.getCompanyForCar(car)).thenReturn(co);
        invoke("updateDetail", new Class[]{Customer.class}, bob);
        assertEquals("Tesla", ((JLabel) field("rentedCarLabel", JLabel.class)).getText());
        assertEquals("Hertz", ((JLabel) field("rentedCompany", JLabel.class)).getText());
    }

    @Test
    void updateDetail_renting_rentDisabled() throws Exception {
        Customer bob = new Customer(2, "Bob", 5);
        Car car = new Car(5, "Tesla", 1);
        Company co = new Company(1, "Hertz");
        when(customerService.refreshCustomer(bob)).thenReturn(bob);
        when(customerService.getRentedCar(bob)).thenReturn(car);
        when(customerService.getCompanyForCar(car)).thenReturn(co);
        invoke("updateDetail", new Class[]{Customer.class}, bob);
        assertFalse(((JButton) field("rentBtn", JButton.class)).isEnabled());
    }

    @Test
    void updateDetail_renting_returnEnabled() throws Exception {
        Customer bob = new Customer(2, "Bob", 5);
        Car car = new Car(5, "Tesla", 1);
        Company co = new Company(1, "Hertz");
        when(customerService.refreshCustomer(bob)).thenReturn(bob);
        when(customerService.getRentedCar(bob)).thenReturn(car);
        when(customerService.getCompanyForCar(car)).thenReturn(co);
        invoke("updateDetail", new Class[]{Customer.class}, bob);
        assertTrue(((JButton) field("returnBtn", JButton.class)).isEnabled());
    }

    @Test
    void updateDetail_renting_detailCardVisible() throws Exception {
        Customer bob = new Customer(2, "Bob", 5);
        Car car = new Car(5, "Tesla", 1);
        Company co = new Company(1, "Hertz");
        when(customerService.refreshCustomer(bob)).thenReturn(bob);
        when(customerService.getRentedCar(bob)).thenReturn(car);
        when(customerService.getCompanyForCar(car)).thenReturn(co);
        invoke("updateDetail", new Class[]{Customer.class}, bob);
        assertTrue(((JPanel) field("detailCard", JPanel.class)).isVisible());
    }

    @Test
    void updateDetail_renting_nullCompany_showsDash() throws Exception {
        Customer bob = new Customer(2, "Bob", 5);
        Car car = new Car(5, "Tesla", 1);
        when(customerService.refreshCustomer(bob)).thenReturn(bob);
        when(customerService.getRentedCar(bob)).thenReturn(car);
        when(customerService.getCompanyForCar(car)).thenReturn(null);
        invoke("updateDetail", new Class[]{Customer.class}, bob);
        assertEquals("—", ((JLabel) field("rentedCompany", JLabel.class)).getText());
    }

    //  handleReturn 

    @Test
    void handleReturn_noSelection_noServiceCall() throws Exception {
        invoke("handleReturn", new Class[0]);
        verify(customerService, never()).returnCarForCustomer(any());
    }

    @Test
    void handleReturn_withSelection_callsReturn() throws Exception {
        Customer carol = new Customer(3, "Carol", 7);
        Customer fresh = new Customer(3, "Carol", 7);
        when(customerService.getCustomers()).thenReturn(List.of(carol));
        when(customerService.refreshCustomer(carol)).thenReturn(fresh);
        when(customerService.getRentedCar(fresh)).thenReturn(null);
        panel.refresh();
        ((JList<Customer>) field("customerList", JList.class)).setSelectedIndex(0);
        invoke("handleReturn", new Class[0]);
        verify(customerService).returnCarForCustomer(fresh);
    }

    //  handleRent 

    @Test
    void handleRent_noSelection_noServiceCall() throws Exception {
        invoke("handleRent", new Class[0]);
        verify(customerService, never()).getCompanies();
    }

    @Test
    void handleRent_noCompanies_showsInfo() throws Exception {
        Customer carol = new Customer(3, "Carol", null);
        when(customerService.getCustomers()).thenReturn(List.of(carol));
        when(customerService.refreshCustomer(carol)).thenReturn(carol);
        when(customerService.getRentedCar(carol)).thenReturn(null);
        panel.refresh();
        ((JList<Customer>) field("customerList", JList.class)).setSelectedIndex(0);
        when(customerService.getCompanies()).thenReturn(List.of());
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            invoke("handleRent", new Class[0]);
            mocked.verify(() -> JOptionPane.showMessageDialog(any(), eq("No companies available."), anyString(), anyInt()));
        }
    }

    @Test
    void handleRent_cancelAtCompanyStep_noRent() throws Exception {
        Customer carol = new Customer(3, "Carol", null);
        Company hertz = new Company(1, "Hertz");
        when(customerService.getCustomers()).thenReturn(List.of(carol));
        when(customerService.refreshCustomer(carol)).thenReturn(carol);
        when(customerService.getRentedCar(carol)).thenReturn(null);
        panel.refresh();
        ((JList<Customer>) field("customerList", JList.class)).setSelectedIndex(0);
        when(customerService.getCompanies()).thenReturn(List.of(hertz));
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), any(), contains("Step 1"), anyInt(), any(), any(), any())).thenReturn(null);
            invoke("handleRent", new Class[0]);
            verify(customerService, never()).rentCarForCustomer(any(), any());
        }
    }

    @Test
    void handleRent_noCarsAtCompany_showsInfo() throws Exception {
        Customer carol = new Customer(3, "Carol", null);
        Company hertz = new Company(1, "Hertz");
        when(customerService.getCustomers()).thenReturn(List.of(carol));
        when(customerService.refreshCustomer(carol)).thenReturn(carol);
        when(customerService.getRentedCar(carol)).thenReturn(null);
        panel.refresh();
        ((JList<Customer>) field("customerList", JList.class)).setSelectedIndex(0);
        when(customerService.getCompanies()).thenReturn(List.of(hertz));
        when(customerService.getAvailableCarsForCompany(hertz)).thenReturn(List.of());
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), any(), contains("Step 1"), anyInt(), any(), any(), any())).thenReturn(hertz);
            invoke("handleRent", new Class[0]);
            mocked.verify(() -> JOptionPane.showMessageDialog(any(), contains("No available cars"), anyString(), anyInt()));
            verify(customerService, never()).rentCarForCustomer(any(), any());
        }
    }

    @Test
    void handleRent_cancelAtCarStep_noRent() throws Exception {
        Customer carol = new Customer(3, "Carol", null);
        Company hertz = new Company(1, "Hertz");
        Car tesla = new Car(1, "Tesla", 1);
        when(customerService.getCustomers()).thenReturn(List.of(carol));
        when(customerService.refreshCustomer(carol)).thenReturn(carol);
        when(customerService.getRentedCar(carol)).thenReturn(null);
        panel.refresh();
        ((JList<Customer>) field("customerList", JList.class)).setSelectedIndex(0);
        when(customerService.getCompanies()).thenReturn(List.of(hertz));
        when(customerService.getAvailableCarsForCompany(hertz)).thenReturn(List.of(tesla));
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), any(), contains("Step 1"), anyInt(), any(), any(), any())).thenReturn(hertz);
            mocked.when(() -> JOptionPane.showInputDialog(any(), any(), contains("Step 2"), anyInt(), any(), any(), any())).thenReturn(null);
            invoke("handleRent", new Class[0]);
            verify(customerService, never()).rentCarForCustomer(any(), any());
        }
    }

    @Test
    void handleRent_fullSuccess_callsRent() throws Exception {
        Customer carol = new Customer(3, "Carol", null);
        Company hertz = new Company(1, "Hertz");
        Car tesla = new Car(1, "Tesla", 1);
        when(customerService.getCustomers()).thenReturn(List.of(carol));
        when(customerService.refreshCustomer(carol)).thenReturn(carol);
        when(customerService.getRentedCar(carol)).thenReturn(null);
        panel.refresh();
        ((JList<Customer>) field("customerList", JList.class)).setSelectedIndex(0);
        when(customerService.getCompanies()).thenReturn(List.of(hertz));
        when(customerService.getAvailableCarsForCompany(hertz)).thenReturn(List.of(tesla));
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showInputDialog(any(), any(), contains("Step 1"), anyInt(), any(), any(), any())).thenReturn(hertz);
            mocked.when(() -> JOptionPane.showInputDialog(any(), any(), contains("Step 2"), anyInt(), any(), any(), any())).thenReturn(tesla);
            invoke("handleRent", new Class[0]);
            verify(customerService).rentCarForCustomer(carol, tesla);
        }
    }

    //  promptAddCustomer 

    @Test
    void promptAddCustomer_withValidInput_callsAddCustomer() throws Exception {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                  .thenAnswer(inv -> { ((JTextField)((Object[])inv.getArgument(1))[1]).setText("NewCustomer"); return JOptionPane.OK_OPTION; });
            when(customerService.getCustomers()).thenReturn(List.of());
            invoke("promptAddCustomer", new Class[0]);
            verify(customerService).addCustomer("NewCustomer");
        }
    }

    @Test
    void promptAddCustomer_cancelled_noCall() throws Exception {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                  .thenReturn(JOptionPane.CANCEL_OPTION);
            invoke("promptAddCustomer", new Class[0]);
            verify(customerService, never()).addCustomer(anyString());
        }
    }

    @Test
    void promptAddCustomer_blankInput_noCall() throws Exception {
        try (MockedStatic<JOptionPane> mocked = mockStatic(JOptionPane.class)) {
            mocked.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                  .thenAnswer(inv -> { ((JTextField)((Object[])inv.getArgument(1))[1]).setText("  "); return JOptionPane.OK_OPTION; });
            invoke("promptAddCustomer", new Class[0]);
            verify(customerService, never()).addCustomer(anyString());
        }
    }

    //  CustomerCellRenderer 

    @Test
    @SuppressWarnings("unchecked")
    void cellRenderer_unselectedEvenRow_whiteBackground() throws Exception {
        JList<Customer> list = (JList<Customer>) field("customerList", JList.class);
        Customer alice = new Customer(1, "Alice", null);
        when(customerService.getRentedCar(alice)).thenReturn(null);
        Component comp = list.getCellRenderer().getListCellRendererComponent(list, alice, 0, false, false);
        assertEquals(Color.WHITE, comp.getBackground());
    }

    @Test
    @SuppressWarnings("unchecked")
    void cellRenderer_unselectedOddRow_offWhiteBackground() throws Exception {
        JList<Customer> list = (JList<Customer>) field("customerList", JList.class);
        Customer alice = new Customer(1, "Alice", null);
        when(customerService.getRentedCar(alice)).thenReturn(null);
        Component comp = list.getCellRenderer().getListCellRendererComponent(list, alice, 1, false, false);
        assertEquals(new Color(0xFAFAFA), comp.getBackground());
    }

    @Test
    @SuppressWarnings("unchecked")
    void cellRenderer_selected_accentBackground() throws Exception {
        JList<Customer> list = (JList<Customer>) field("customerList", JList.class);
        Customer alice = new Customer(1, "Alice", null);
        when(customerService.getRentedCar(alice)).thenReturn(null);
        Component comp = list.getCellRenderer().getListCellRendererComponent(list, alice, 0, true, false);
        assertEquals(MainWindow.ACCENT, comp.getBackground());
    }

    @Test
    @SuppressWarnings("unchecked")
    void cellRenderer_withRentedCar_rendersSuccessfully() throws Exception {
        JList<Customer> list = (JList<Customer>) field("customerList", JList.class);
        Customer bob = new Customer(2, "Bob", 5);
        Car car = new Car(5, "Tesla", 1);
        when(customerService.getRentedCar(bob)).thenReturn(car);
        Component comp = list.getCellRenderer().getListCellRendererComponent(list, bob, 0, false, false);
        assertNotNull(comp);
    }

    //  panel basics 

    @Test
    void panel_layoutIsBorderLayout() {
        assertInstanceOf(BorderLayout.class, panel.getLayout());
    }

    @Test
    void panel_backgroundIsWhite() {
        assertEquals(Color.WHITE, panel.getBackground());
    }
}

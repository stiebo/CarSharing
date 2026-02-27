package carsharing.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    void constructor_setsFields() {
        Car car = new Car(1, "Volvo", 3);
        assertEquals(1, car.getId());
        assertEquals("Volvo", car.getName());
        assertEquals(3, car.getCompanyId());
    }

    @Test
    void toString_returnsName() {
        Car car = new Car(5, "Tesla", 2);
        assertEquals("Tesla", car.toString());
    }
}

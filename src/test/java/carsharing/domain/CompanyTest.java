package carsharing.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void constructor_setsFields() {
        Company company = new Company(7, "Hertz");
        assertEquals(7, company.getId());
        assertEquals("Hertz", company.getName());
    }

    @Test
    void toString_returnsName() {
        Company company = new Company(2, "Avis");
        assertEquals("Avis", company.toString());
    }
}

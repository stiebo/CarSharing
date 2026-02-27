package carsharing.dao.company;

import carsharing.dao.impl.CompanyDaoImpl;
import carsharing.database.DbClient;
import carsharing.domain.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompanyDaoImplTest {

    private CompanyDaoImpl companyDao;

    @BeforeEach
    void setUp() {
        DbClient dbClient = new DbClient("mem:testCompanyDb_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        companyDao = new CompanyDaoImpl(dbClient);
    }

    @Test
    void findAll_emptyDatabase_returnsEmptyList() {
        List<Company> result = companyDao.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void add_thenFindAll_returnsAddedCompany() {
        companyDao.add("Hertz");
        List<Company> result = companyDao.findAll();
        assertEquals(1, result.size());
        assertEquals("Hertz", result.get(0).getName());
    }

    @Test
    void findById_existingId_returnsCompany() {
        companyDao.add("Avis");
        int id = companyDao.findAll().get(0).getId();
        Company found = companyDao.findById(id);
        assertNotNull(found);
        assertEquals("Avis", found.getName());
    }

    @Test
    void findById_nonExistentId_returnsNull() {
        Company found = companyDao.findById(999);
        assertNull(found);
    }

    @Test
    void add_multipleCompanies_sortedById() {
        companyDao.add("Bravo");
        companyDao.add("Alpha");
        List<Company> result = companyDao.findAll();
        assertEquals(2, result.size());
        assertEquals("Bravo", result.get(0).getName());
        assertEquals("Alpha", result.get(1).getName());
    }
}

package carsharing.dao.car;

import carsharing.dao.impl.CarDaoImpl;
import carsharing.database.DbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CarDaoImplTest {

    @Mock
    private DbClient dbClient;

    @InjectMocks
    private CarDaoImpl testCarDao;

    @Test
    void create() {
        String carName = "Volvo";
        int companyId = 1;

        testCarDao.create(carName, companyId);

        verify(dbClient).run(eq("INSERT INTO CAR (name, company_id) VALUES (?, ?)"),
                eq(carName), eq(companyId));

    }
}
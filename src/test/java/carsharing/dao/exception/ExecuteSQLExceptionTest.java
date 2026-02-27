package carsharing.dao.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecuteSQLExceptionTest {

    @Test
    void constructor_storesCause() {
        RuntimeException cause = new RuntimeException("original SQL error");
        ExecuteSQLException ex = new ExecuteSQLException(cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void isRuntimeException() {
        ExecuteSQLException ex = new ExecuteSQLException(new Exception("cause"));
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void getMessage_containsCauseMessage() {
        Exception cause = new Exception("table not found");
        ExecuteSQLException ex = new ExecuteSQLException(cause);
        assertTrue(ex.getMessage().contains("table not found"));
    }
}

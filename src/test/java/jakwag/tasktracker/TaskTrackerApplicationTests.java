package jakwag.tasktracker;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class TaskTrackerApplicationTests {

    @MockBean
    Flyway flyway;

    @Test
    void contextLoads() {
    }

}

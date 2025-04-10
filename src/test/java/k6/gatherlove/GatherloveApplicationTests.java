package k6.gatherlove;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GatherloveApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the application context starts successfully
    }

    @Test
    void mainMethodTest() {
        GatherloveApplication.main(new String[]{});
    }
}

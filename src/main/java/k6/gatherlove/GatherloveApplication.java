package k6.gatherlove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"k6.gatherlove", "Controller"})
public class GatherloveApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatherloveApplication.class, args);
    }

}



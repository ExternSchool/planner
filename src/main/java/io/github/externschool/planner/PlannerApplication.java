package io.github.externschool.planner;

import io.github.externschool.planner.bootstrapdata.ExcludeFromTests;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ExcludeFromTests
public class PlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlannerApplication.class, args);
    }
}

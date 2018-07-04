package io.github.externschool.planner;

import io.github.externschool.planner.bootstrapdata.ExcludeFromTests;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = ExcludeFromTests.class))
public class TestPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestPlannerApplication.class, args);
    }
}

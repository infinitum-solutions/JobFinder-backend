package ru.mityushin.jobfinder.server;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.mityushin.jobfinder.server.config.AspectConfig;
import ru.mityushin.jobfinder.server.config.LoggerConfig;
import ru.mityushin.jobfinder.server.config.ScheduleConfig;
import ru.mityushin.jobfinder.server.config.TomcatConfig;
import ru.mityushin.jobfinder.server.config.WebSecurityConfig;

import java.util.Collections;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {
        AspectConfig.class,
        LoggerConfig.class,
        ScheduleConfig.class,
        TomcatConfig.class,
        WebSecurityConfig.class
}, initializers = {
        ConfigFileApplicationContextInitializer.class,
        BaseIntegrationTest.Initializer.class
})
public abstract class BaseIntegrationTest {

    /**
     * Create database instance in docker container
     */
    protected static PostgreSQLContainer POSTGRESQL = (PostgreSQLContainer) new PostgreSQLContainer("postgres:10")
            .withDatabaseName("jobfinder")
            .withUsername("postgres")
            .withPassword("postgres")
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql", "rw"))
            .withCommand("postgres -c max_connections=2000 -c fsync=off");


    // Start database container for all test. Postgres server will be destroyed at JVM shutdown.
    static {
        POSTGRESQL.start();
    }

    /**
     * Initializer for spring.datasource properties initialization
     */
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext context) {
            HashMap<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", POSTGRESQL.getJdbcUrl());
            props.put("spring.datasource.username", POSTGRESQL.getUsername());
            props.put("spring.datasource.password", POSTGRESQL.getPassword());
            props.put("spring.datasource.driver-class-name", POSTGRESQL.getDriverClassName());

            TestPropertyValues.of(
                    props.entrySet().stream()
                            .map(o -> String.format("%s=%s", o.getKey(), o.getValue()))
                            .toArray(String[]::new))
                    .applyTo(context);
        }
    }
}

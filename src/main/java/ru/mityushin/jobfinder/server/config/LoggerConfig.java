package ru.mityushin.jobfinder.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mityushin.jobfinder.server.JobFinderServerApplication;

@Configuration
public class LoggerConfig {
    @Bean
    public Logger log() {
        return LoggerFactory.getLogger(JobFinderServerApplication.class);
    }
}

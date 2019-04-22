package ru.mityushin.jobfinder.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class JobFinderServerApplicationTests {
    @Configuration
    public static class ContextConfiguration {}

    @Test
    public void contextLoads() {
    }

}

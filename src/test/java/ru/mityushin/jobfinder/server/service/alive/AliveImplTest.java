package ru.mityushin.jobfinder.server.service.alive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({AliveImpl.class})
@SpringBootTest
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class AliveImplTest {
    @Autowired
    private Logger log;
    @Autowired
    private Alive aliveService;

    @Configuration
    static class ContextConfiguration {
        @Bean
        public Logger log() {
            return PowerMockito.mock(Logger.class);
        }

        @Bean
        public Alive aliveService() {
            return new AliveImpl();
        }
    }

    @Test
    public void sayAlive() throws Exception {
        Whitebox.setInternalState(AliveImpl.class, "LOG", log);
        aliveService.sayAlive();
        Mockito.verify(log, Mockito.times(1)).info("I'm alive!");
    }
}

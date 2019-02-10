package ru.mityushin.jobfinder.server.service.alive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AliveImpl implements Alive {
    private final static Logger LOG = LoggerFactory.getLogger(AliveImpl.class);

    @Scheduled(cron = "0 */10 * * * *")
    @Override
    public void sayAlive() {
        LOG.info("I'm alive!");
    }
}

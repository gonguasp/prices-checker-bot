package com.bot.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final String message) {
        log.info("Publishing custom event.");
        EmailEvent customSpringEvent = new EmailEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}

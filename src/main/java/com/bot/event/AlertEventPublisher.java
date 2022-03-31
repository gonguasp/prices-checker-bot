package com.bot.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AlertEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final String message) {
        log.info("Publishing custom event.");
        AlertEvent customSpringEvent = new AlertEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}

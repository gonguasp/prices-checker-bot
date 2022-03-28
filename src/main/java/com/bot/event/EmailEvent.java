package com.bot.event;

import org.springframework.context.ApplicationEvent;


public class EmailEvent extends ApplicationEvent {

    private String message;

    public EmailEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

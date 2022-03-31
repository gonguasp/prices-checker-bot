package com.bot.event;

import org.springframework.context.ApplicationEvent;


public class AlertEvent extends ApplicationEvent {

    public static boolean areNewSales = true;

    private String message;

    public AlertEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

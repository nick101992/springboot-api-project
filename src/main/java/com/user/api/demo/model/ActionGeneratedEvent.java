package com.user.api.demo.model;

import org.springframework.context.ApplicationEvent;

//Classe che rappresenta l'evento personalizzato "ReportGeneratedEvent"

/* L'Observer Pattern può essere implementato utilizzando i componenti di pubblicazione-sottoscrizione del framework.
Un modo per implementare l'Observer Pattern in Spring Boot è quello di utilizzare l'evento di pubblicazione-sottoscrizione.
In questo modo, gli oggetti "Subject" possono pubblicare eventi e gli oggetti "Observer" possono sottoscriversi a questi eventi.*/
public class ActionGeneratedEvent extends ApplicationEvent {
    private final String username;
    private final String description;
    private final boolean success;
    private final String actionType;

    public ActionGeneratedEvent(Object source, String username, String description, boolean success, String actionType) {
        super(source);
        this.username = username;
        this.description = description;
        this.success = success;
        this.actionType = actionType;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getActionType() {
        return actionType;
    }
}

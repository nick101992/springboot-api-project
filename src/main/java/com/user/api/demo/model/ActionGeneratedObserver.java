package com.user.api.demo.model;

import com.user.api.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;


// Classe che rappresenta l'observer "ReportGeneratedObserver"
public class ActionGeneratedObserver implements ApplicationListener<ActionGeneratedEvent> {

    @Autowired
    ReportService reportService;

    // Metodo che scatta quando viene pubblicato l'evento nell'Application Context
    @Override
    public void onApplicationEvent(ActionGeneratedEvent event) {
        // Observer che registra l'evento nella tabella report
        String username = event.getUsername();
        String fileName = event.getDescription();
        boolean success = event.isSuccess();
        String actionType = event.getActionType();

        Action action = new Action();
        action.setUsername(username);
        action.setDescription(fileName);
        action.setSuccess(success);
        action.setActionType(actionType);

        reportService.save(action);
    }
}

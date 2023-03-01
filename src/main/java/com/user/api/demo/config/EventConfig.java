package com.user.api.demo.config;

import com.user.api.demo.model.ActionGeneratedObserver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Classe per generare il bean da iniettare
@Configuration
public class EventConfig {
    @Bean
    public ActionGeneratedObserver reportGeneratedObserver() {
        return new ActionGeneratedObserver();
    }
}

package com.example.configmap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyBean {
    @Autowired
    private MyConfig config;

    @Autowired
    private MySecret secret;

    @Scheduled(fixedDelay = 5000)
    public void hello() {
        log.info("The configmap is: " + config.getMessage());
        log.info("The secret    is: {}/{}", secret.getUsername(), secret.getPassword());
    }
}

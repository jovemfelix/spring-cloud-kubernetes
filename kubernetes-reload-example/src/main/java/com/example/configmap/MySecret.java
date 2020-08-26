package com.example.configmap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "secret")
@Getter
@Setter
public class MySecret {

    private String username="username not set";
    private String password="password not set";

}

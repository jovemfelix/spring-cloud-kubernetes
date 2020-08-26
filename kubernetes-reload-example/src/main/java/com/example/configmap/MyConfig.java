package com.example.configmap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bean")
@Getter
@Setter
public class MyConfig {

    private String message = "a message that can be changed live";

}

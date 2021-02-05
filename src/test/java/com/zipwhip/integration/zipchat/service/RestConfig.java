package com.zipwhip.integration.zipchat.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.zipwhip.integration.zipchat.service.RestConfig.RestConfigProps;

@Configuration
@EnableConfigurationProperties(RestConfigProps.class)
public class RestConfig {

    @ConfigurationProperties(prefix = "rest")
    @Data
    public static class RestConfigProps {
        String hostname;
    }

}

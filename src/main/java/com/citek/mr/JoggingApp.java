package com.citek.mr;

import com.citek.mr.config.ApplicationProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.net.UnknownHostException;

@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class JoggingApp {

    private static final Logger log = LoggerFactory.getLogger(JoggingApp.class);

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(JoggingApp.class);
        Environment env = app.run(args).getEnvironment();

    }
}

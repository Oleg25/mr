package com.citek.mr.config;



import ch.qos.logback.classic.LoggerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {
    private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH";
    private static final String ASYNC_LOGSTASH_APPENDER_NAME = "ASYNC_LOGSTASH";

    private final Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);

    private LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    private final String appName;

    private final String serverPort;

    public LoggingConfiguration(@Value("${spring.application.name}") String appName,
                                @Value("${server.port}") String serverPort)
                                {
        this.appName = appName;
        this.serverPort = serverPort;

    }


}

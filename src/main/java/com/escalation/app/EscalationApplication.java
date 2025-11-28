package com.escalation.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EscalationApplication {

    private static final Logger logger = LoggerFactory.getLogger(EscalationApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Escalation Application...");
        SpringApplication.run(EscalationApplication.class, args);
        logger.info("Escalation Application started successfully");
    }
}

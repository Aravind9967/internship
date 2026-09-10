package com.screenings.cervical_screenings.Config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AuditConfig {

 private static final Logger log = LoggerFactory.getLogger(AuditConfig.class);

 @Value("${audit.log.dir:./logs}")
 private String auditLogDir;

 @PostConstruct
 public void init() {
     Path dir = Paths.get(auditLogDir);

     try {
         if (!Files.exists(dir)) {
             Files.createDirectories(dir);
             log.info("Created audit log directory: {}", dir.toAbsolutePath());
         } else {
             log.info("Using existing audit log directory: {}", dir.toAbsolutePath());
         }

         log.info("Audit logging is enabled. All sensitive actions will be recorded.");

     } catch (IOException e) {
         log.error("Failed to initialize audit log directory", e);
         throw new IllegalStateException("Cannot initialize audit log directory", e);
     }
 }

}
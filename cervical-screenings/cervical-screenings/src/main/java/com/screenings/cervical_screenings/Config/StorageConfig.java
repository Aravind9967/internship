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
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

@Configuration
public class StorageConfig {

 private static final Logger log = LoggerFactory.getLogger(StorageConfig.class);

 @Value("${storage.images.base-path:./secure-images}")
 private String imagesBasePath;

 @PostConstruct
 public void init() {
     Path base = Paths.get(imagesBasePath);

     try {
         if (!Files.exists(base)) {
             Files.createDirectories(base);
             log.info("Created image storage directory: {}", base.toAbsolutePath());
         } else {
             log.info("Using existing image storage directory: {}", base.toAbsolutePath());
         }

         try {
             Files.setPosixFilePermissions(
                     base,
                     Set.of(
                             PosixFilePermission.OWNER_READ,
                             PosixFilePermission.OWNER_WRITE,
                             PosixFilePermission.OWNER_EXECUTE
                     )
             );
         } catch (UnsupportedOperationException e) {
         
             log.debug("POSIX permissions not supported on this file system");
         }

     } catch (IOException e) {
        
         log.error("Failed to initialize secure image storage directory", e);
         throw new IllegalStateException("Cannot initialize secure image storage", e);
     }
 }
}
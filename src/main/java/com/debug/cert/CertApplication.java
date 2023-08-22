package com.debug.cert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * in command line pass the following to override defaults:
 * application properties (the file needs to be in same location of executable jar)
 * --spring.config.location=/home/bilaleluneis/application.properties
 */
@SpringBootApplication
public class CertApplication {

    private static final Logger log = LoggerFactory.getLogger(CertApplication.class);

    @Value("${endpoint.url}")
    private String endPoint;

    @Value("${ssl.debug.flags}")
    private static String sslDebugFlags;

    public static void main(String[] args) {

        if(!sslDebugFlags.equalsIgnoreCase("non")){
            System.setProperty("javax.net.debug", sslDebugFlags);
        }

        if(Arrays.stream(args).anyMatch(Predicate.isEqual("-client"))) {
            SpringApplication.run(CertApplication.class, args).close();
        }else{
            SpringApplication.run(CertApplication.class, args);
        }

    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        log.debug("EXECUTING CommandLine Runner ...");
        return args -> {

            if (Arrays.stream(args).anyMatch(Predicate.isEqual("-client"))) {
                log.debug("executing client");
                String result = restTemplate.getForObject(endPoint, String.class);
                log.info("call result is: " + result);
            }
        };
    }

}

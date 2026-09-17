package com.hadaka_electro.internal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.hadaka_electro.common.entities",
        "com.hadaka_electro.internal"})
@ComponentScan(basePackages = {"com.hadaka_electro.internal", "com.hadaka_electro.common.exception"})
public class ElectroInternalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectroInternalApiApplication.class, args);
    }

}

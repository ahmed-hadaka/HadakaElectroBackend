package com.hadaka_electro.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.hadaka_electro.common.entities",
        "com.hadaka_electro.customer"
})
@ComponentScan(basePackages = {
        "com.hadaka_electro.customer",
        "com.hadaka_electro.common.exception"
})

public class ElectroCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectroCustomerApiApplication.class, args);
    }

}

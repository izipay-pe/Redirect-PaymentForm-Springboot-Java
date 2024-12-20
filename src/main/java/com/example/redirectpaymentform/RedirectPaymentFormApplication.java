package com.example.redirectpaymentform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class RedirectPaymentFormApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RedirectPaymentFormApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(RedirectPaymentFormApplication.class, args);
    }
}

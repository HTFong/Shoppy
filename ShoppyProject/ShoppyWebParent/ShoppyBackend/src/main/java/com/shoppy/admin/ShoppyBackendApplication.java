package com.shoppy.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.shoppy.common.entity","com.shoppy.admin.user"})
public class ShoppyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppyBackendApplication.class, args);
    }

}

package com.vault.velocitylimiter;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VelocityLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(VelocityLimiterApplication.class, args);
        startHsqldbManager();
    }

    private static void startHsqldbManager() {
        System.setProperty("java.awt.headless", "false");
        DatabaseManagerSwing.main(new String[]{
                "--url", "jdbc:hsqldb:mem:vaultdb",
                "--user", "sa",
                "--password", ""
        });
    }

}

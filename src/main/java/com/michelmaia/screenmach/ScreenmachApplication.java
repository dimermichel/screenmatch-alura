package com.michelmaia.screenmach;

import com.michelmaia.screenmach.principal.Principal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmachApplication implements CommandLineRunner {

    @Value("${omdb.api.key}")
    private String apiKey;

    public static void main(String[] args) {
        SpringApplication.run(ScreenmachApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal();
        principal.showMenu(apiKey);
    }
}

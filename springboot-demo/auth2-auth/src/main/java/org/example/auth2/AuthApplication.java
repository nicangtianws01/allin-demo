package org.example.auth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class AuthApplication implements CommandLineRunner {

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("\n---------------------------\n" +
                "AuthApplication started \n" +
                "\\\\\\ listen port: {} \\\\\\\n" +
                "---------------------------"
                , port);
    }
}

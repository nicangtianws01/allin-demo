package org.example.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Value("${server.port}")
    private String port;

    @Override
    public void run(String... args) {
        log.info("\n---------------------------\n" +
                        "AuthApplication started \n" +
                        "\\\\\\ listen port: {} \\\\\\\n" +
                        "---------------------------"
                , port);
    }
}

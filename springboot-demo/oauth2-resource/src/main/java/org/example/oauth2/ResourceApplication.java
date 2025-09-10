package org.example.oauth2;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ResourceApplication implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }

    @Value("${server.port}")
    private String port;

    @Override
    public void run(ApplicationArguments args) {
        log.info("\n---------------------------\n" +
                        "AuthApplication started \n" +
                        "\\\\\\ listen port: {} \\\\\\\n" +
                        "---------------------------"
                , port);
    }
}

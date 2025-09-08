package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.security.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class DemoController {
    @Resource
    private JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestBody LoginUser user) {
        String role = "ROLE_ADMIN";
        log.info("user: {}", user.getUsername());
        return jwtService.getToken(user.getUsername(), role);
    }
}

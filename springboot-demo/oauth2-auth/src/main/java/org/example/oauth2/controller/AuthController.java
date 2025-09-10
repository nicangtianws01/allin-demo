package org.example.oauth2.controller;

import org.example.oauth2.dto.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8091", // 或 "*"（如无 credentials 需求）
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowCredentials = "true",
        maxAge = 3600)
@Controller
public class AuthController {

    @RequestMapping("/auth")
    public String auth(@RequestParam String clientId, @RequestParam String redirectUri, Model model) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId is required");
        }
        if (redirectUri == null || redirectUri.isEmpty()) {
            throw new IllegalArgumentException("redirectUri is required");
        }
        if (!clientId.equals("123")) {
            throw new IllegalArgumentException("invalid clientId or clientSecret");
        }
        model.addAttribute("redirectUri", redirectUri);
        return "index";
    }

    @ResponseBody
    @RequestMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return new Result<String>().error("user and password are required");
        }
        if (!username.equals("admin") || !password.equals("test123")) {
            return new Result<String>().error("invalid user or password");
        }
        return new Result<String>().setData("123");
    }

    @ResponseBody
    @RequestMapping("/token")
    public Result<String> token(@RequestBody Map<String, String> body) {
        String clientId = body.get("clientId");
        String clientSecret = body.get("clientSecret");
        String code = body.get("code");
        if (clientId == null || clientSecret == null) {
            return new Result<String>().error("clientId and clientSecret are required");
        }
        if (!clientId.equals("123") || !clientSecret.equals("test123")) {
            return new Result<String>().error("invalid clientId or clientSecret");
        }
        if (code == null || !code.equals("123")) {
            return new Result<String>().error("error auth code");
        }
        return new Result<String>().setData("abc");
    }
}

package org.example.oauth2.controller;

import org.example.oauth2.dto.AuthRequest;
import org.example.oauth2.dto.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:8091", // 或 "*"（如无 credentials 需求）
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowCredentials = "true",
        maxAge = 3600)
@Controller
public class AuthController {

    @Resource
    private CacheManager cacheManager;

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

        // 清除之前的授权码
        Cache authCodeCache = cacheManager.getCache("authCode");
        assert authCodeCache != null;
        authCodeCache.evictIfPresent(clientId);
//        String authCode = authCodeCache.get(clientId, String.class);
//        if (authCode != null && !authCode.isEmpty()) {
//            authCodeCache.evict(clientId);
//            Cache tokenCache = cacheManager.getCache("token");
//            assert tokenCache != null;
//            tokenCache.evictIfPresent(clientId + ":" + authCode);
//        }

        model.addAttribute("redirectUri", redirectUri);
        model.addAttribute("clientId", clientId);
        return "index";
    }

    /**
     * 登录并生成授权码
     *
     * @param body
     * @return
     */
    @ResponseBody
    @RequestMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String clientId = body.get("clientId");
        if (username == null || password == null) {
            return Result.error("user and password are required");
        }
        if (!username.equals("admin") || !password.equals("test123")) {
            return Result.error("invalid user or password");
        }
        // 生成并缓存授权码
        String authCode = Math.random() * 1000 / 2 + "";
        Cache authCodeCache = cacheManager.getCache("authCode");
        assert authCodeCache != null;
        authCodeCache.put(clientId, authCode);
        return Result.success("success", authCode);
    }

    //    @Cacheable(value = "token", key = "#authRequest.clientId + ':' + #authRequest.code")
    @ResponseBody
    @RequestMapping("/token")
    public Result<String> token(@RequestBody AuthRequest authRequest) {
        String clientId = authRequest.getClientId();
        String clientSecret = authRequest.getClientSecret();
        String code = authRequest.getCode();
        if (clientId == null || clientSecret == null) {
            return Result.error("clientId and clientSecret are required");
        }
        if (!clientId.equals("123") || !clientSecret.equals("test123")) {
            return Result.error("invalid clientId or clientSecret");
        }
        if (code == null) {
            return Result.error("auth code is required");
        }

        // 校验授权码
        Cache authCodeCache = cacheManager.getCache("authCode");
        assert authCodeCache != null;
        String authCode = authCodeCache.get(clientId, String.class);
        if (authCode == null || !authCode.equalsIgnoreCase(code)) {
            return Result.error("invalid auth code");
        }

        // 生成token
        String token = UUID.randomUUID().toString();
//        Cache tokenCache = cacheManager.getCache("token");
//        assert tokenCache != null;
//        tokenCache.put(clientId + ":" + code, token);

        return Result.success(token);
    }
}

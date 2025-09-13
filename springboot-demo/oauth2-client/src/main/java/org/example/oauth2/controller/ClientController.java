package org.example.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.oauth2.dto.AuthRequest;
import org.example.oauth2.dto.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.naming.NoPermissionException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ClientController {

    @Resource
    private CacheManager cacheManager;

    /**
     * 获取访问token并缓存
     * @param code
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping("/auth/{code}")
    public Result<String> auth(@PathVariable String code) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        AuthRequest authRequest = new AuthRequest();
        authRequest.setCode(code);
        authRequest.setClientId("123");
        authRequest.setClientSecret("test123");
        HttpEntity<AuthRequest> entity = new HttpEntity<>(authRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8090/token", entity, String.class);
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            String body = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            Result<String> result = mapper.readValue(body, new TypeReference<Result<String>>() {
            });
            if (result.getCode() == 200) {
                Cache token = cacheManager.getCache("token");
                assert token != null;
                // 缓存授权token
                token.put("auth:admin", result.getData());
                // 假设客户端token可解析出admin用户名
                return Result.success("success", "client:token");
            }
            return Result.error(result.getMessage());
        }
        return Result.error();
    }

    /**
     * 请求数据
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping("/resource/data")
    public Result<String> getData(HttpServletRequest request) throws JsonProcessingException {
        String token = (String) request.getAttribute("token");
        ObjectMapper mapper = new ObjectMapper();
        // 请求数据
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        Map<String, String> params = new HashMap<>();
        params.put("clientId", "123");
        params.put("clientSecret", "test123");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8092/data", entity, String.class);
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            String body = response.getBody();
            Result<String> result = mapper.readValue(body, new TypeReference<Result<String>>() {
            });
            if (result.getCode() == 200) {
                return Result.success("success", result.getData());
            }
            return Result.error(result.getMessage());
        }
        return Result.error(response.getBody());
    }
}

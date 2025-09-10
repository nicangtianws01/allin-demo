package org.example.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.oauth2.dto.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
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
import java.util.HashMap;
import java.util.Map;

@RestController
public class ClientController {

    @Resource
    private CacheManager cacheManager;

    @Cacheable(value = "token", key = "'admin'")
    @RequestMapping("/auth/{code}")
    public Result<String> auth(@PathVariable String code) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("clientId", "123");
        params.put("clientSecret", "test123");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8090/token", entity, String.class);
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            String body = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            Result<String> result = mapper.readValue(body, new TypeReference<Result<String>>() {
            });
            if (result.getCode() == 200) {
                return new Result<String>().success("success", "ttt:" + result.getData());
            }
            return new Result<String>().error(result.getMsg());
        }
        return new Result<String>().error();
    }

    @RequestMapping("/data")
    public Result<String> getData() throws JsonProcessingException, NoPermissionException {
        // 从缓存中获取token
        Cache cache = cacheManager.getCache("token");
        if (cache == null) {
            throw new NoPermissionException("Invalid token");
        }
        Object tokenCache = cache.get("admin", Object.class);
        if (tokenCache == null) {
            throw new NoPermissionException("Invalid token");
        }
        ObjectMapper mapper = new ObjectMapper();
        Result<String> tokenInfo = mapper.readValue(mapper.writeValueAsString(tokenCache), new TypeReference<Result<String>>() {
        });
        if (tokenInfo.getCode() != 200) {
            throw new NoPermissionException("Invalid token");
        }
        String token = tokenInfo.getData().replace("ttt:", "");
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
                return new Result<String>().success("success", result.getData());
            }
            return new Result<String>().error(result.getMsg());
        }
        return new Result<String>().error(response.getBody());
    }
}

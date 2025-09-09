package org.example.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.oauth2.dto.Result;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ClientController {
    @RequestMapping("/auth/{code}")
    public Result<String> auth(@PathVariable String code) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("clientId", "123");
        params.put("clientSecret", "123");
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
        }
        return new Result<String>().error();
    }
}

package org.example.oauth2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.oauth2.dto.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ResourceController {
    @RequestMapping("/data")
    public Result<String> getData() throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("test", "data");
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(map);
        return Result.success("success", value);
    }
}

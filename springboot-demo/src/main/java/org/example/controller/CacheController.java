package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.DemoEntity;
import org.example.service.DemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/cache")
public class CacheController {

    @Resource
    private DemoService demoService;

    @RequestMapping("/getByName")
    public DemoEntity get(@RequestParam String name) {
        return demoService.getByName(name);
    }

    @RequestMapping("/updateByName")
    public DemoEntity update(@RequestParam String name, @RequestParam String value) {
        return demoService.updateByName(name, value);
    }
    @RequestMapping("/updateByNameExpire")
    public int updateExpire(@RequestParam String name, @RequestParam String value) {
        return demoService.updateByNameExpire(name, value);
    }
}

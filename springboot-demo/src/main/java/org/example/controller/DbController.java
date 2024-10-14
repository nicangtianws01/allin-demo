package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.DemoEntity;
import org.example.entity.DemoRelation;
import org.example.repository.DemoRelationRepository;
import org.example.repository.DemoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/db")
public class DbController {
    @Resource
    private DemoRepository demoRepository;

    @Resource
    private DemoRelationRepository demoRelationRepository;

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/lock")
    public void lock(@RequestParam Long id) {
        DemoEntity demoEntity1 = demoRepository.lockById01(id);
        log.info(demoEntity1.toString());
        try {
            DemoEntity demoEntity = demoRepository.lockById01(id);
            log.info(demoEntity.toString());
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/update")
    public void update(@RequestParam Long id, @RequestParam String intro) {
        DemoRelation demoRelation = new DemoRelation()
                .setId(id)
                .setIntro(intro);
        demoRelationRepository.updateById(demoRelation);
    }
}

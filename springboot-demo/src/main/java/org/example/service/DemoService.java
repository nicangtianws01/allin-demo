package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.DemoEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

public interface DemoService extends IService<DemoEntity> {
    void readFile();

    void testException(int testNum);

    DemoEntity getByName(String name);

    DemoEntity updateByName(String name, String value);

    int updateByNameExpire(String name, String value);
}

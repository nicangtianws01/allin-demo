package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.DemoEntity;
import org.example.entity.EncodeDeviceDto;
import org.example.repository.DemoRepository;
import org.example.service.DemoService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DemoServiceImpl extends ServiceImpl<DemoRepository, DemoEntity> implements DemoService {

    @Resource
    private DemoRepository demoRepository;

    @Override
    public void readFile() {
        List<EncodeDeviceDto> cameras = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String path = "D:/tmp/iot/device/camera/";
        File dir = new File(path);
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            try {
                EncodeDeviceDto encodeDeviceDto = objectMapper.readValue(file, EncodeDeviceDto.class);
                cameras.add(encodeDeviceDto);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        log.info("camera number: {}", cameras.size());

        List<EncodeDeviceDto> encodes = new ArrayList<>();
        String encodePath = "D:/tmp/iot/device/encodeDeviceList/";
        File encodeDir = new File(encodePath);
        File[] encodeFiles = encodeDir.listFiles();
        assert encodeFiles != null;
        for (File file : encodeFiles) {
            try {
                EncodeDeviceDto encodeDeviceDto = objectMapper.readValue(file, EncodeDeviceDto.class);
                encodes.add(encodeDeviceDto);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        log.info("encodeDevice number: {}", encodes.size());

        List<EncodeDeviceDto> filtered = cameras.stream().filter(camera -> {
            long count = encodes.stream()
                    .filter(encodeDeviceDto -> camera.getName().contains(encodeDeviceDto.getName()))
                    .count();
            return count > 0;
        }).sorted(Comparator.comparing(EncodeDeviceDto::getName)).collect(Collectors.toList());

        filtered.forEach(camera -> {
            log.info("{} - {}", camera.getName(), camera.getIndexCode());
        });

        List<EncodeDeviceDto> filteredNot = cameras.stream().filter(camera -> {
            long count = encodes.stream()
                    .filter(encodeDeviceDto -> camera.getName().contains(encodeDeviceDto.getName()))
                    .count();
            return count == 0;
        }).sorted(Comparator.comparing(EncodeDeviceDto::getName)).collect(Collectors.toList());

        log.info("-----------------------------------------");
        log.info("-----------------------------------------");
        log.info("-----------------------------------------");
        log.info("-----------------------------------------");
        log.info("-----------------------------------------");

        filteredNot.forEach(camera -> {
            log.info("{} - {}", camera.getName(), camera.getIndexCode());
        });

        log.info("all camera number: {}", cameras.size());
        log.info("filtered camera number: {}", filtered.size());
        log.info("filteredNot camera number: {}", filteredNot.size());

//        encodes.stream().filter(encodeDeviceDto -> {
//            cameras.stream().filter(new Predicate<EncodeDeviceDto>() {
//                @Override
//                public boolean test(EncodeDeviceDto camera) {
//                    return encodeDeviceDto.getName().contains(camera.getName());
//                }
//            })
//        })
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void testException(int testNum) {
        DemoEntity entity = new DemoEntity();
        entity.setId(1L);
        entity.setName("inner rollback");
        demoRepository.updateById(entity);
        if (testNum > 10) {
            throw new RuntimeException("inner rollback");
        }
    }

    @Cacheable(cacheNames = "entities", key = "#name")
    @Override
    public DemoEntity getByName(String name) {
        log.info("query by name {}", name);
        QueryWrapper<DemoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        return demoRepository.selectOne(wrapper, false);
    }

    @CachePut(cacheNames = "entities", key = "#name")
    @Override
    public DemoEntity updateByName(String name, String value) {
        log.info("update by name {}", name);
        UpdateWrapper<DemoEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("name", name);
        wrapper.set("value", value);
        demoRepository.update(wrapper);
        QueryWrapper<DemoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        return demoRepository.selectOne(queryWrapper, false);
    }

    @CacheEvict(cacheNames = "entities", key = "#name")
    @Override
    public int updateByNameExpire(String name, String value) {
        log.info("update by name {}", name);
        UpdateWrapper<DemoEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("name", name);
        wrapper.set("value", value);
        return demoRepository.update(wrapper);
    }
}

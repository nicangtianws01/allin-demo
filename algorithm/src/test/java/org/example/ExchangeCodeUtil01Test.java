package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.util.ExchangeCodeUtil;
import org.example.util.ExchangeCodeUtil01;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExchangeCodeUtil01Test {
    @Test
    public void testGenerate() {
        String s = ExchangeCodeUtil01.generateCode(1894814, Instant.now().toEpochMilli());
        log.info(s);
    }
    @Test
    public void testGenerate01() {
        String s = ExchangeCodeUtil01.generateCode(0, 1718629831823L);
        log.info(s);
    }

    @Test
    public void testParse() {
        long l = ExchangeCodeUtil01.parseCode("VLT8KSQR6");
        log.info(String.valueOf(l));
    }

    @Test
    public void testGenerateMore() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < 1000_0000; i++) {
            long fresh = Instant.now().toEpochMilli();
            String s = ExchangeCodeUtil01.generateCode(i, fresh);
            if (s.length() < 10) {
                log.info("fresh: {}, id: {}, no: {}, size: {}", fresh, i, s, s.length());
            }
            if (map.containsKey(s)) {
                log.info("发生重复：{}", s);
                continue;
            }
            map.put(s, i);
        }
    }

    @Test
    public void testFresh() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
//            String s = String.valueOf(Instant.now().toEpochMilli());
//            String substring = s.substring(s.length() - 5);
//            log.info(substring);
//            long l = Long.parseLong(substring) & 0xf;
            long l = Instant.now().toEpochMilli() & 0xf;
            log.info(String.valueOf(l));
            Thread.sleep(101);
        }
    }

    @Test
    public void testGenerate1() {
        String s = ExchangeCodeUtil.generateCode(772, 1717637673534L);
        log.info(s);
    }

    @Test
    public void testParse1() {
        long l = ExchangeCodeUtil.parseCode("A92DN27PLLS");
        log.info(String.valueOf(l));
    }
}

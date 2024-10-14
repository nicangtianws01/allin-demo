package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class BacnetCollect01Test {
    @Test
    public void test() throws JsonProcessingException {
        List<BacnetDataCollect01.ResultObj> collect =
                new BacnetDataCollect01().collect();
        log.info("{}", new ObjectMapper().writeValueAsString(collect));

    }
}

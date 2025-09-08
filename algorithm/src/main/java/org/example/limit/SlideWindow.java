package org.example.limit;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class SlideWindow {

    public static final int MAX_SLIDE_WINDOW_SIZE = 10;
    private static final Map<String, AccessRecord> map = new ConcurrentHashMap<>();

    public void in(int ip) {
        long time = new Date().getTime();
        String ipStr = String.valueOf(ip);
        log.info("get{}", ipStr);
        AccessRecord record;
        if (map.containsKey(ipStr)) {
           record = map.computeIfPresent(ipStr, (s, accessRecord) -> {
               log.info("{}count{}", ipStr, accessRecord.getRecords().size());
               List<Long> filterdRecords = accessRecord.getRecords().stream()
                       .filter(accessTime -> accessTime + 1000 > time)
                       .collect(Collectors.toList());
               if (filterdRecords.size() > MAX_SLIDE_WINDOW_SIZE) {
//                   log.error("limit{}", ipStr);
               } else {
                   filterdRecords.add(time);
               }
               accessRecord.setCount(filterdRecords.size());
               accessRecord.setRecords(filterdRecords);
               return accessRecord;
           });
        } else {
            List<Long> records = new ArrayList<>();
            records.add(time);
            record = new AccessRecord()
                    .setIp(ipStr)
                    .setRecords(records)
                    .setCount(1)
                    .setName(ipStr);
            map.put(ipStr, record);
        }
        assert record != null;
        if (record.getCount() > MAX_SLIDE_WINDOW_SIZE) {
            log.error("limit{}", ipStr);
            return;
        }

        log.info("resolve{}time{}", ipStr, time);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error("resolveerror{}", e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("resolvecomplete{}time{}", ipStr, time);
    }

    @Accessors(chain = true)
    @Data
    public static class AccessRecord {
        private String ip;
        private String name;
        private int count;
        private List<Long> records;
    }
}

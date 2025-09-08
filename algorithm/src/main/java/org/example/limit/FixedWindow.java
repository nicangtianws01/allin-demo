package org.example.limit;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindow {
    private static final Map<String, List<Long>> map = new ConcurrentHashMap<>();

    public void in(String ip) {
        long time = new Date().getTime();
        if(!map.containsKey(ip)) {

        }
    }
}

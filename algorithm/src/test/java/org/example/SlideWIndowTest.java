package org.example;

import org.example.limit.SlideWindow;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SlideWIndowTest {
    @Test
    public void test() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 400, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
//        for (int i = 0; i < 500; i++) {
//            double floor = Math.floor(Math.random() * 10);
//            int ip = (int) floor;
//            executor.execute(() -> {
//                SlideWindow slideWindow = new SlideWindow();
//                slideWindow.in(ip);
//            });
//        }
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                SlideWindow slideWindow = new SlideWindow();
                slideWindow.in(1);
            });
        }
        Thread.sleep(10 * 1000);
    }
}

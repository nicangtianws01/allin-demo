package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.polygon.Point;
import org.example.polygon.Polygon;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@Slf4j
public class PolygonTest {
    @Test
    public void testFour(){
        Point p1 = new Point(-59.5234375, 301);
        Point p2 = new Point(-151.5234375,305.5);
        Point p3 = new Point(-153.5234375,392);
        Point p4 = new Point(-44.0234375,391);
        Polygon polygon = Polygon.Builder()
                .addVertice(p1)
                .addVertice(p2)
                .addVertice(p3)
                .addVertice(p4)
                .build();
        // 匹配
        Point point = new Point(-104.0234375, 344);
        boolean contains = polygon.contains(point);
        log.info("在区域内：{}", contains);

        Point point1 = new Point(-141.0234375,380);
        boolean contains1 = polygon.contains(point1);
        log.info("在区域内：{}", contains1);

        Point point2 = new Point(-68.0234375,312);
        boolean contains2 = polygon.contains(point2);
        log.info("在区域内：{}", contains2);

        // 不匹配
        Point point3 = new Point(-142.0234375,295);
        boolean contains3 = polygon.contains(point3);
        log.info("不在区域内：{}", contains3);

        Point point4 = new Point(-63.5234375,398.5);
        boolean contains4 = polygon.contains(point4);
        log.info("不在区域内：{}", contains4);

        Point point5 = new Point(-128.0234375,133.5);
        boolean contains5 = polygon.contains(point5);
        log.info("不在区域内：{}", contains5);
    }

    @Test
    public void testThree(){
        Point p1 = new Point(-147.5234375,136.5);
        Point p2 = new Point(-96.5234375,391.5);
        Point p3 = new Point(-213.5234375,394.5);
        Polygon polygon = Polygon.Builder()
                .addVertice(p1)
                .addVertice(p2)
                .addVertice(p3)
                .build();
        // 匹配
        Point point = new Point(-135.0234375,304.5);
        boolean contains = polygon.contains(point);
        log.info("在区域内：{}", contains);

        Point point1 = new Point(-156.5234375,387.5);
        boolean contains1 = polygon.contains(point1);
        log.info("在区域内：{}", contains1);

        Point point2 = new Point(-157.0234375,200.5);
        boolean contains2 = polygon.contains(point2);
        log.info("在区域内：{}", contains2);

        // 不匹配
        Point point3 = new Point(-47.5234375,210.5);
        boolean contains3 = polygon.contains(point3);
        log.info("不在区域内：{}", contains3);

        Point point4 = new Point(-151.5234375,453.5);
        boolean contains4 = polygon.contains(point4);
        log.info("不在区域内：{}", contains4);

        Point point5 = new Point(-225.5234375,317);
        boolean contains5 = polygon.contains(point5);
        log.info("不在区域内：{}", contains5);

    }

    @Test
    public void testFive(){
        Point p1 = new Point(-59.5234375, 301);
        Point p2 = new Point(-151.5234375,305.5);
        Point p3 = new Point(-153.5234375,392);
        Point p4 = new Point(-44.0234375,391);
        Point p5 = new Point(-100.5234375,344.5);
        Polygon polygon = Polygon.Builder()
                .addVertice(p1)
                .addVertice(p2)
                .addVertice(p3)
                .addVertice(p4)
                .addVertice(p5)
                .build();
        // 匹配
        Point point = new Point(-90.0234375,312);
        boolean contains = polygon.contains(point);
        log.info("在区域内：{}", contains);

        Point point1 = new Point(-86.5234375,376.5);
        boolean contains1 = polygon.contains(point1);
        log.info("在区域内：{}", contains1);

        Point point2 = new Point(-125.0234375,329.5);
        boolean contains2 = polygon.contains(point2);
        log.info("在区域内：{}", contains2);

        // 不匹配
        Point point3 = new Point(-69.0234375,344.5);
        boolean contains3 = polygon.contains(point3);
        log.info("不在区域内：{}", contains3);

        Point point4 = new Point(-133.0234375,170.5);
        boolean contains4 = polygon.contains(point4);
        log.info("不在区域内：{}", contains4);

        Point point5 = new Point(-101.5234375,401.5);
        boolean contains5 = polygon.contains(point5);
        log.info("不在区域内：{}", contains5);
    }

    @Test
    public void test(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.toLocalDate().minusMonths(11).withDayOfMonth(1).atTime(0, 0, 0);
        log.info(String.valueOf(startTime.getMonth().getValue()));
    }
}

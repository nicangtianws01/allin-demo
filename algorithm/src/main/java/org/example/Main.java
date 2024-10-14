package org.example;

import org.example.jdk8.LamdaClazz;
import org.example.jdk8.TestService;
import org.example.jdk8.TestServiceImpl;
import org.example.kmp.KmpSearch;
import org.example.polygon.Point;
import org.example.polygon.Polygon;
import org.example.util.ExchangeCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
//         testKmp();
//         testPolygon();
//        testJdk8();

        testExchangeCode();

    }

    private static void testExchangeCode() {
        String s = ExchangeCodeUtil.generateCode(1, 133);
        log.info(s);
    }

    public static void testJdk8() {
        TestService testService = new TestServiceImpl();
        testService.test();
        TestService.testStatic();

        LamdaClazz lamdaClazz = new LamdaClazz();
        lamdaClazz.lambdaInterfaceDemo(()-> System.out.println("自定义函数式接口"));
    }


    public static void testKmp() {
        System.out.println(Arrays.toString(KmpSearch.generateNext("Java")));
        System.out.println(KmpSearch.search("Java 是由 Sun Microsystems 公司于 1995 年 5 月推出的高级程序设计语言。\n" +
                        "Java 可运行于多个平台，如 Windows, Mac OS 及其他多种 UNIX 版本的系统。\n" +
                        "本教程通过简单的实例将让大家更好的了解 Java 编程语言。\n" +
                        "移动操作系统 Android 大部分的代码采用 Java 编程语言编程。\n",
                "Java"));
    }

    public static void testPolygon() {
        // -59.5234375,301
        // -151.5234375,305.5
        // -153.5234375,392
        // -44.0234375,391
        // -104.0234375,344
        Point p1 = new Point(-59.5234375, 301);
        Point p2 = new Point(-151.5234375, 305.5);
        Point p3 = new Point(-153.5234375, 392);
        Point p4 = new Point(-44.0234375, 391);
        Polygon polygon = Polygon.Builder()
                .addVertice(p1)
                .addVertice(p2)
                .addVertice(p3)
                .addVertice(p4)
//                .addVertice(p5)
                .build();
        Point point = new Point(-104.0234375, 344);
        System.out.println(polygon.contains(point));
    }

}
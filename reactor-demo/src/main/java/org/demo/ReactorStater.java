package org.demo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

public class ReactorStater {
    public static void main(String[] args) {
        ReactTest reactTest = new ReactTest();
        reactTest.test().subscribe(System.out::println);
    }
}

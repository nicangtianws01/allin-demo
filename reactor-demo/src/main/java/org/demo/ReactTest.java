package org.demo;

import reactor.core.publisher.Mono;

public class ReactTest {
    public Mono<String> test(){
        return Mono.just("test");
    }
}

package org.example.jdk8;

public interface TestService {
    default void testDefault() {
        System.out.println("test default method");
    }

    static void testStatic() {
        System.out.println("test static method");
    }

    void test();
}

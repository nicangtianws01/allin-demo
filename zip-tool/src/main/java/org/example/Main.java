package org.example;

import org.example.file.FileUtils;

public class Main {
    public static void main(String[] args) {
        if(args.length < 1){
            throw new RuntimeException("请输入目录");
        }
        String dir = args[0];

    }
}

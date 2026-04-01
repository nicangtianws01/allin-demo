package org.demo;

import org.demo.type.ClassFile;
import org.demo.util.ClassFileAnalysiser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class ClassFileAnalysisMain {

    public static ByteBuffer readFile(String classFilePath) throws Exception {
        File file = new File(classFilePath);
        if (!file.exists()) {
            throw new Exception("file not exists!");
        }
        byte[] byteCodeBuf = new byte[4096];
        int lenght;
        try (InputStream in = Files.newInputStream(file.toPath())) {
            lenght = in.read(byteCodeBuf);
        }
        if (lenght < 1) {
            throw new Exception("not read byte code.");
        }
        // 将字节数组包装为ByteBuffer
        return ByteBuffer.wrap(byteCodeBuf, 0, lenght).asReadOnlyBuffer();
    }

    public static void main(String[] args) throws Exception {
        // 读取class文件
        ByteBuffer codeBuf = readFile("xxx.class");
        // 解析class文件
        ClassFile classFile = ClassFileAnalysiser.analysis(codeBuf);
        // 打印魔数解析器解析出来的Magic
        System.out.println(classFile.getMagic().toHexString());
    }

}

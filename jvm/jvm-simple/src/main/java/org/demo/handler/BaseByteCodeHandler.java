package org.demo.handler;

import org.demo.type.ClassFile;

import java.nio.ByteBuffer;

public interface BaseByteCodeHandler {
    /**
     * 解释器的排序值
     * @return
     */
    int order();

    /**
     * 读取
     * @param codeBuf
     * @param classFile
     */
    void read(ByteBuffer codeBuf, ClassFile classFile) throws Exception;
}

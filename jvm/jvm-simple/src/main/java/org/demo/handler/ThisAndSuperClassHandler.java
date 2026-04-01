package org.demo.handler;

import org.demo.type.ClassFile;
import org.demo.type.U2;

import java.nio.ByteBuffer;

public class ThisAndSuperClassHandler implements BaseByteCodeHandler {

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void read(ByteBuffer codeBuf, ClassFile classFile) throws Exception {
        classFile.setThis_class(new U2(codeBuf.get(), codeBuf.get()));
        classFile.setSuper_class(new U2(codeBuf.get(), codeBuf.get()));
    }

}

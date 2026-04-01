package org.demo.handler;

import org.demo.type.*;

import java.nio.ByteBuffer;

public class ConstantPoolHandler implements BaseByteCodeHandler {

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void read(ByteBuffer codeBuf, ClassFile classFile) throws Exception {
        // 1）
        U2 cpLen = new U2(codeBuf.get(), codeBuf.get());
        classFile.setConstant_pool_count(cpLen);
        // 2）
        int cpInfoLeng = cpLen.toInt() - 1;
        classFile.setConstant_pool(new CpInfo[cpInfoLeng]);
        // 3）
        for (int i = 0; i < cpInfoLeng; i++) {
            U1 tag = new U1(codeBuf.get());
            CpInfo cpInfo = CpInfo.newCpInfo(tag);
            cpInfo.read(codeBuf);
            System.out.println("#" + (i + 1) + ":" + cpInfo);
            classFile.getConstant_pool()[i] = cpInfo;
            // 4）
            if (cpInfo instanceof CONSTANT_Long_info) {
                i++; // jump n+2
            }
        }
    }

}

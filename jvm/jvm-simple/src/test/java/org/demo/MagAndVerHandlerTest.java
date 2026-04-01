package org.demo;

import org.demo.type.*;
import org.demo.util.ClassAccessFlagUtils;
import org.demo.util.ClassFileAnalysiser;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

public class MagAndVerHandlerTest {

    @Test
    public void test() throws Exception {
        System.out.println("--------------magic and version-------------");
        // 将class文件读取到ByteBuffer
        ByteBuffer codeBuf = ClassFileAnalysisMain.readFile("D:\\tmp\\jvm\\FileType.class");
        // 解析class文件
        ClassFile classFile = ClassFileAnalysiser.analysis(codeBuf);
        System.out.println(classFile.getMagic().toHexString());  // 打印魔数
        System.out.println(classFile.getMinor_version().toInt());  // 打印副版本号
        System.out.println(classFile.getMagor_version().toInt());  // 打印主版本号
    }

    @Test
    public void testConstantPoolHandler() throws Exception {
        System.out.println("--------------constant pool-------------");
        // 读取class文件，生成ByteBuffer
        ByteBuffer codeBuf = ClassFileAnalysisMain.readFile("D:\\tmp\\jvm\\FileType.class");
        // 解析class文件
        ClassFile classFile = ClassFileAnalysiser.analysis(codeBuf);
        // 获取常量池常量的总数
        int cp_info_count = classFile.getConstant_pool_count().toInt();
        System.out.println("常量池中常量项总数：" + cp_info_count);
        // 遍历常量池中的常量
        CpInfo[] cpInfo = classFile.getConstant_pool();
        for (CpInfo cp : cpInfo) {
            System.out.println(cp.toString());
        }
    }

    @Test
    public void testAccessFlagsHandlerHandler() throws Exception {
        System.out.println("--------------access flags-------------");
        ByteBuffer codeBuf = ClassFileAnalysisMain.readFile("D:\\tmp\\jvm\\FileType.class");
        ClassFile classFile = ClassFileAnalysiser.analysis(codeBuf);
        // 获取访问标志
        U2 accessFlags = classFile.getAccess_flags();
        // 输出为字符串
        System.out.println(ClassAccessFlagUtils.toClassAccessFlagsString(accessFlags));
    }

    @Test
    public void testThisAndSuperHandlerHandler() throws Exception {
        System.out.println("-------------this and super-------------");
        ByteBuffer codeBuf = ClassFileAnalysisMain.readFile("D:\\tmp\\jvm\\FileType.class");
        ClassFile classFile = ClassFileAnalysiser.analysis(codeBuf);
        // this_class
        U2 this_class = classFile.getThis_class();
        // 根据this_class 到常量池获取CONSTANT_Class_info常量
        // 由于常量池的索引是从1开始的，所以需要将索引减1取得数组下标
        CONSTANT_Class_info this_class_cpInfo = (CONSTANT_Class_info) classFile.getConstant_pool()[this_class.toInt() - 1];
        CONSTANT_Utf8_info this_class_name= (CONSTANT_Utf8_info)
                classFile.getConstant_pool()
                        [this_class_cpInfo.getName_index().toInt()-1];
        System.out.println(this_class_name);
        // super_class
        U2 super_class = classFile.getSuper_class();
        CONSTANT_Class_info super_class_cpInfo = (CONSTANT_Class_info)
                classFile.getConstant_pool() [super_class.toInt() - 1];

        CONSTANT_Utf8_info supor_class_name = (CONSTANT_Utf8_info)
                classFile.getConstant_pool()
                        [super_class_cpInfo.getName_index().toInt()-1];
        System.out.println(supor_class_name);
    }

}

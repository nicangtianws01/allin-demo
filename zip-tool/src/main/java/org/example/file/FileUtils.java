package org.example.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class FileUtils {

    private FileUtils(){}

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 获取压缩包真实类型
     *
     * @param file 要获取类型的文件。
     * @return 文件类型枚举。
     */
    public static FileType getFileType(File file) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            byte[] head = new byte[4];
            if (-1 == inputStream.read(head)) {
                return FileType.UNKNOWN;
            }
            int headHex = 0;
            for (byte b : head) {
                headHex <<= 8;
                headHex |= b;
            }
            switch (headHex) {
                case 0x504B0304:
                    return FileType.ZIP;
                case 0x776f7264:
                    return FileType.TAR;
                case -0x51:
                    return FileType._7Z;
                case 0x425a6839:
                    return FileType.BZ2;
                case -0x74f7f8:
                    return FileType.GZ;
                case 0x52617221:
                    return FileType.RAR;
                default:
                    return FileType.UNKNOWN;
            }
        } catch (IOException e) {
            log.error("文件识别出错！", e);
        }
        return FileType.UNKNOWN;
    }
}

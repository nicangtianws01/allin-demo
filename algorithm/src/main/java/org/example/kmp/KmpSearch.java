package org.example.kmp;

import java.util.ArrayList;
import java.util.List;

public class KmpSearch {

    public static List<Integer> search(String source, String pattern) {

        List<Integer> result = new ArrayList<>();

        int[] next = generateNext(pattern);

        char[] chars = source.toCharArray();
        char[] patterns = pattern.toCharArray();
        int i = 0;
        int j = 0;
        while (i < chars.length) {
            char c = chars[i];
            char p = patterns[j];
            if (c == p) {
                if (j == patterns.length - 1) {
                    // 整个匹配串匹配完成，返回起始地址
                    result.add(i + 1 - patterns.length);
                    // 继续匹配并重置pattern状态
                    i++;
                    j = 0;
                } else {
                    // 继续匹配
                    i++;
                    j++;
                }
                continue;
            }

            if (j == 0) {
                // 匹配串回到了开头，不能倒退，原串前进
                i++;
            } else {
                // 匹配串倒退
                j = next[j];
            }
        }
        return result;
    }

    /**
     * 计算位置前面的字符公共前后缀，使用时不需要再-1
     * 为什么计算之前的字符串公共前后缀，因为匹配时如果匹配不上，则回退pattern是根据当前的前面子串来进行计算前后缀
     * @param pattern
     * @return
     */
    public static int[] next(String pattern) {
        char[] chars = pattern.toCharArray();
        int len = chars.length;
        int[] next = new int[len];
        // 初始化
        if (len > 0) next[0] = 0;
        if (len > 1) next[1] = 0;

        for (int pIndex = 2; pIndex < len; pIndex++) {
            // 前一个公共前后缀长度
            int last = next[pIndex - 1];
            int ch = chars[pIndex - 1];
            if (ch == chars[last]) {
                // 匹配新增的字符，则直接在前一次公共前后缀长度上+1
                next[pIndex] = last + 1;
                continue;
            }

            // 未匹配上则回退到上一个地址重新匹配
            while (chars[last] != ch && last != 0) {
                last = next[last];
            }

            // 已经到达头部，且未匹配上，则公共前后缀长度为0
            if (last == 0 && chars[0] != ch) {
                next[pIndex] = 0;
            }
        }
        return next;
    }

    /**
     * 简化
     * @param pattern
     * @return
     */
    public static int[] generateNext(String pattern) {
        char[] chars = pattern.toCharArray();
        int len = chars.length;
        int[] next = new int[len];
        // 初始化
        if (len > 0) next[0] = 0;
        if (len > 1) next[1] = 0;

        for (int pIndex = 2; pIndex < len; pIndex++) {
            // 前一个公共前后缀长度
            int last = next[pIndex - 1];
            int ch = chars[pIndex - 1];

            // 未匹配上则回退
            while (chars[last] != ch && last != 0) {
                last = next[last];
            }
            // 默认为0
            next[pIndex] = 0;
            // 匹配上了+1
            if(ch == chars[last]){
                next[pIndex] = last + 1;
            }
        }
        return next;
    }
}

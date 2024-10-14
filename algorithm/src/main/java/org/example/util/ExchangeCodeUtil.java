package org.example.util;

public final class ExchangeCodeUtil {
    private ExchangeCodeUtil() {
    }

    /**
     * 优惠券兑换码模板
     */
    private final static String COUPON_CODE_PATTERN = "^[23456789ABCDEFGHJKLMNPQRSTUVWXYZ]{8,10}$";

    /**
     * 异或密钥表，用于最后的数据混淆
     */
    private final static long[] XOR_TABLE = {
            45139281907L, 61261925523L, 58169127203L, 97031786219L,
            64169927199L, 46169126943L, 62731286209L, 52082227349L,
            59169127063L, 76169126987L, 52082200939L, 61261925739L,
            82731286563L, 97031786427L, 56169127077L, 74111865001L,
            52082216763L, 61261925663L, 56169127113L, 45139282119L,
            82731286479L, 64169927233L, 41390251661L, 59169127121L,
            64169927321L, 55139282179L, 64111864881L, 46169127031L,
            58169127221L, 61261925523L, 76169126943L, 64169927363L,
    };
    /**
     * fresh值的偏移位数
     */
    private final static int FRESH_BIT_OFFSET = 32;
    /**
     * 校验码的偏移位数
     */
    private final static int CHECK_CODE_BIT_OFFSET = 36;
    /**
     * fresh值的掩码，4位
     */
    private final static int FRESH_MASK = 0xF;
    /**
     * 验证码的掩码，14位
     */
    private final static int CHECK_CODE_MASK = 0b11111111111111;
    /**
     * 载荷的掩码，36位
     */
    private final static long PAYLOAD_MASK = 0xFFFFFFFFFL;
    /**
     * 序列号掩码，32位
     */
    private final static long SERIAL_NUM_MASK = 0xFFFFFFFFL;
    /**
     * 序列号加权运算的秘钥表
     */
    private final static int[][] PRIME_TABLE = {
            {10, 59, 241, 61, 607, 67, 977, 1217, 1289, 1291},
            {79, 83, 106, 439, 313, 619, 911, 1049, 1237, 1245},
            {173, 211, 499, 673, 823, 941, 1039, 1161, 1229, 1259},
            {31, 293, 311, 349, 461, 577, 757, 883, 1009, 1157},
            {353, 23, 367, 499, 599, 657, 719, 929, 1101, 1211},
            {103, 179, 353, 467, 577, 691, 891, 947, 1153, 1253},
            {213, 432, 257, 313, 571, 619, 743, 829, 983, 1103},
            {31, 151, 241, 349, 607, 677, 769, 823, 967, 1058},
            {61, 83, 109, 137, 151, 521, 701, 827, 1118, 1211},
            {23, 61, 199, 223, 479, 647, 746, 811, 947, 1019},
            {31, 109, 311, 467, 616, 743, 821, 881, 1031, 1171},
            {41, 173, 367, 408, 569, 683, 761, 883, 1009, 1181},
            {123, 283, 467, 577, 661, 773, 881, 967, 1097, 1289},
            {59, 137, 258, 347, 439, 547, 641, 839, 977, 1009},
            {60, 199, 313, 421, 613, 739, 827, 941, 1087, 1207},
            {19, 127, 241, 353, 499, 607, 811, 919, 1031, 1207}
    };
//    private final static int[][] PRIME_TABLE = {
//            {10, 59, 241, 61, 607, 67, 977, 1217, 1289, 1601},
//            {79, 83, 106, 439, 313, 619, 911, 1049, 1237, 1801},
//            {173, 211, 499, 673, 823, 941, 1039, 1361, 1429, 1259},
//            {31, 293, 311, 349, 461, 577, 757, 883, 1009, 1657},
//            {353, 23, 367, 499, 599, 657, 719, 929, 1301, 1511},
//            {103, 179, 353, 467, 577, 691, 891, 947, 1153, 1453},
//            {213, 432, 257, 313, 571, 619, 743, 829, 983, 1103},
//            {31, 151, 241, 349, 607, 677, 769, 823, 967, 1058},
//            {61, 83, 109, 137, 151, 521, 701, 827, 1118, 1711},
//            {23, 61, 199, 223, 479, 647, 746, 811, 947, 1019},
//            {31, 109, 311, 467, 616, 743, 821, 881, 1031, 1171},
//            {41, 173, 367, 408, 569, 683, 761, 883, 1009, 1181},
//            {123, 283, 467, 577, 661, 773, 881, 967, 1097, 1289},
//            {59, 137, 258, 347, 439, 547, 641, 839, 977, 1009},
//            {60, 199, 313, 421, 613, 739, 827, 941, 1087, 1307},
//            {19, 127, 241, 353, 499, 607, 811, 919, 1031, 1307}
//    };

    /**
     * 生成兑换码
     *
     * @param serialNum 递增序列号
     * @param fresh     新限值
     * @return 兑换码
     */
    public static String generateCode(long serialNum, long fresh) {
        // 1.计算新鲜值
        fresh = fresh & FRESH_MASK;
        // 2.拼接payload，fresh（4位） + serialNum（32位）
        long payload = fresh << FRESH_BIT_OFFSET | serialNum;
        // 3.计算验证码
        long checkCode = calcCheckCode(payload, (int) fresh);
        // 4.payload做大质数异或运算，混淆数据
        payload ^= XOR_TABLE[(int) (checkCode & 0b11111)];
        // 5.拼接兑换码明文: 校验码（14位） + payload（36位）
        long code = checkCode << CHECK_CODE_BIT_OFFSET | payload;
        // 6.转码
        return Base32Util.encode(code);
    }

    private static long calcCheckCode(long payload, int fresh) {
        // 1.获取码表
        int[] table = PRIME_TABLE[fresh];
        // 2.生成校验码，payload每4位乘加权数，求和，取最后13位结果
        long sum = 0;
        int index = 0;
        while (payload > 0) {
            sum += (payload & 0xf) * table[index++];
            payload >>>= 4;
        }
        return sum & CHECK_CODE_MASK;
    }

    public static long parseCode(String code) {
        if (code == null || !code.matches(COUPON_CODE_PATTERN)) {
            // 兑换码格式错误
            throw new RuntimeException("无效兑换码");
        }
        // 1.Base32解码
        long num = Base32Util.decode(code);
        // 2.获取低36位，payload
        long payload = num & PAYLOAD_MASK;
        // 3.获取高14位，校验码
        int checkCode = (int) (num >>> CHECK_CODE_BIT_OFFSET);
        // 4.载荷异或大质数，解析出原来的payload
        payload ^= XOR_TABLE[(checkCode & 0b11111)];
        // 5.获取高4位，fresh
        int fresh = (int) (payload >>> FRESH_BIT_OFFSET & FRESH_MASK);
        // 6.验证格式：
        if (calcCheckCode(payload, fresh) != checkCode) {
            throw new RuntimeException("无效兑换码");
        }
        return payload & SERIAL_NUM_MASK;
    }

}

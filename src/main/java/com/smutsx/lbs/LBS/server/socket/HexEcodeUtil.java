package com.smutsx.lbs.LBS.server.socket;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.HashMap;
import java.util.Map;

public class HexEcodeUtil {

    //16进制数字字符集
    public static final String HEXMAXSTRING = "0123456789ABCDEF";
    public static final String HEXMINSTRING = "0123456789abcdef";
    /**
     * byte[]转16进制Str
     *
     * @param byteArray
     */
    public static String ByteArrayToHexStr(byte[] byteArray){
        if (byteArray == null)
            return null;
        char[] hexArray = HEXMAXSTRING.toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int i = 0; i < byteArray.length; i++){
            int temp = byteArray[i] & 0xFF;
            hexChars[i * 2] = hexArray[temp >>> 4];
            hexChars[i * 2 + 1] = hexArray[temp & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Str转16进制Str
     *
     * @param str
     * @return
     */
    public static String StrToHexStr(String str) {
        //根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        //将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++){
            stringBuilder.append("0x");
            stringBuilder.append(HEXMAXSTRING.charAt((bytes[i] & 0xf0) >> 4));
            stringBuilder.append(HEXMAXSTRING.charAt((bytes[i] & 0x0f) >> 0));
            //去掉末尾的逗号
            if (i != bytes.length - 1)
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制Str转byte[]
     *
     * @param hexStr 不带空格、不带0x、不带逗号的16进制Str,如:06EEF7F1
     * @return
     */
    public static byte[] HexStrToByteArray(String hexStr){
        byte[] byteArray = new byte[hexStr.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = hexStr.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    /**
     * 温度湿度数据转换
     *
     */
    public static Map<String , String> HexToRead(String info){
        HashMap<String, String> hashMap = new HashMap<>();

        return hashMap;
    }

    /**
     * 将16进制字符串转换为byte数组
     * @param hexItr 16进制字符串
     * @return
     */
    public static byte[] hexItr2Arr(String hexItr) {
        try {
            return Hex.decodeHex(hexItr);
        } catch (DecoderException e) {
            System.out.println("16进制字符串转byte异常!");
            e.printStackTrace();
        }
        return null;
    }



    public static void main(String[] args) {
        //byte[] bytes = hexItr2Arr("454E383739465134563249393936394F");
        byte[] bytes = hexItr2Arr("010300000002C40B010304012202585B5F");
        try {
            String s = new String(bytes, "UTF-8");
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
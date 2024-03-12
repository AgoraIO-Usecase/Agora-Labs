package io.agora.api.example.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import io.agora.api.example.App;
import java.nio.charset.StandardCharsets;

public class AESUtils {

    static{
        System.loadLibrary("agoralabs");
    }


    public static String decrypt(String encrypted) {
        byte[] decoder=Base64.decode(encrypted,Base64.DEFAULT);
        String hex=toHex(decoder);
        return decrypt(App.getInstance(),hex);



    }


    private static final char[] DIGITS
        = {'0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String toHex(byte[] data) {
        final StringBuffer sb = new StringBuffer(data.length * 2);
        for (byte datum : data) {
            sb.append(DIGITS[(datum >>> 4) & 0x0F]);
            sb.append(DIGITS[datum & 0x0F]);
        }
        return sb.toString();
    }

    public static byte[] hexToByteArray(String hexString) {
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) +
                Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }


    public static String encrypt(String src){
        String encrypted=encrypt(App.getInstance(),src);
        String base64=Base64.encodeToString(hexToByteArray(encrypted),Base64.NO_WRAP);
        return base64;
    }



    public static native String encrypt(Context context,String src);
    public  static native String decrypt(Context context,String src);

    public static native String getS1(Context context);
    public static native String getS2(Context context);

    private AESUtils() {
    }
}

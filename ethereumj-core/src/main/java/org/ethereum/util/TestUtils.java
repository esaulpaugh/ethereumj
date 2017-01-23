package org.ethereum.util;

import java.security.SecureRandom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * Created by esaulpaugh on 1/20/17.
 */
public class TestUtils {

    private static final SecureRandom sr = new SecureRandom();

    static {
        sr.setSeed(sr.generateSeed(32));
    }

    public static byte[] secureRandomBytes(int n) {
        byte[] b = new byte[n];
        sr.nextBytes(b);
        return b;
    }

    public static void nextBytes(byte[] bytes) {
        sr.nextBytes(bytes);
    }

    public static String toChars(byte... bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    public static void printBytes(byte... bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(b)
                    .append(", ");
        }
        if(bytes.length > 0) {
            sb.replace(sb.length() - 2, sb.length(), "");
        }
        System.out.println(sb.toString());
    }

    public static void printChars(byte... bytes) {
        System.out.println(toChars(bytes));
    }

    public static String base64(byte[] bytes) {
        return Base64.encode(bytes, Integer.MAX_VALUE);
    }

    public static byte[] decodeBase64(String encoded) throws Base64DecodingException {
        return Base64.decode(encoded);
    }

}

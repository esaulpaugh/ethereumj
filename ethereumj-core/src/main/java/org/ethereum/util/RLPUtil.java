package org.ethereum.util;

/**
 * Created by esaulpaugh on 1/22/17.
 */
public class RLPUtil {

//    public static byte[] encodeList(byte[]... elements) {
//        if (elements == null) {
//            return new byte[] { (byte) ElementType.LIST_SHORT.getOffset() };
//        }
//
//        int dataLen = 0;
//        for (byte[] e : elements) {
//            dataLen += e.length;
//        }
//
//        byte[] data;
//        int copyPos;
//
//        if(dataLen > 55) {
//            // length of length = BX
//            // prefix = [BX, [length]]
//            final byte lengthLen = ByteUtil.byteLengthNoLeadZeroes(dataLen);
//
//            data = new byte[1 + lengthLen + dataLen];
//            data[0] = (byte) (ElementType.LIST_LONG.getOffset() + lengthLen);
//            ByteUtil.insertInt(data, dataLen, lengthLen);
//
//            copyPos = lengthLen + 1;
//        } else {
//            data = new byte[1 + dataLen];
//            data[0] = (byte) (ElementType.LIST_SHORT.getOffset() + dataLen);
//            copyPos = 1;
//        }
//
//        for (byte[] element : elements) {
//            System.arraycopy(element, 0, data, copyPos, element.length);
//            copyPos += element.length;
//        }
//        return data;
//    }
}

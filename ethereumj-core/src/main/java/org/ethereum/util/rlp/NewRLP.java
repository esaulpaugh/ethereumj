package org.ethereum.util.rlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.Charset;

import static org.ethereum.util.rlp.ElementType.*;

public class NewRLP {

    private static final Logger logger = LoggerFactory.getLogger("newrlp");

    /* ******************************************************
     *                      DECODING                        *
     * ******************************************************/

    static long decodeLong(final byte[] buffer, final int i, final int numBytes) {

        int shiftAmount = 0;

        long val = 0;
        switch (numBytes) {// cases all fall through
        case 8: val |= (buffer[i+7] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 7: val |= (buffer[i+6] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 6: val |= (buffer[i+5] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 5: val |= (buffer[i+4] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 4: val |= (buffer[i+3] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 3: val |= (buffer[i+2] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 2: val |= (buffer[i+1] & 0xFFL) << shiftAmount; shiftAmount += Byte.SIZE;
        case 1:
            byte lead = buffer[i];
            val |= (lead & 0xFFL) << shiftAmount;
            // validate
            if(lead == 0 && val > 0) {
                throw new NumberFormatException("Deserialised positive integers with leading zeroes are invalid.");
            }
        default: return val;
        }
    }

//    /* ******************************************************
//     *                      ENCODING                        *
//     * ******************************************************/

    /**
     *
     * @param val
     * @param o
     * @param i
     * @return  the number of bytes inserted
     */
    static int putLong(long val, byte[] o, int i) {

        byte a = 0, b = 0, c = 0, d = 0, e = 0, f = 0, g = 0, h;

        int n = 1;
        h = (byte) (val & 0xFF);
        val = val >>> Byte.SIZE;
        if(val != 0) {
            n = 2;
            g = (byte) (val & 0xFF);
            val = val >>> Byte.SIZE;
            if(val != 0) {
                n = 3;
                f = (byte) (val & 0xFF);
                val = val >>> Byte.SIZE;
                if(val != 0) {
                    n = 4;
                    e = (byte) (val & 0xFF);
                    val = val >>> Byte.SIZE;
                    if(val != 0) {
                        n = 5;
                        d = (byte) (val & 0xFF);
                        val = val >>> Byte.SIZE;
                        if(val != 0) {
                            n = 6;
                            c = (byte) (val & 0xFF);
                            val = val >>> Byte.SIZE;
                            if(val != 0) {
                                n = 7;
                                b = (byte) (val & 0xFF);
                                val = val >>> Byte.SIZE;
                                if(val != 0) {
                                    n = 8;
                                    a = (byte) (val & 0xFF);
                                }
                            }
                        }
                    }
                }
            }
        }

        switch (n) {
        case 1: o[i]=h; return 1;
        case 2: o[i]=g; o[i+1]=h; return 2;
        case 3: o[i]=f; o[i+1]=g; o[i+2]=h; return 3;
        case 4: o[i]=e; o[i+1]=f; o[i+2]=g; o[i+3]=h; return 4;
        case 5: o[i]=d; o[i+1]=e; o[i+2]=f; o[i+3]=g; o[i+4]=h; return 5;
        case 6: o[i]=c; o[i+1]=d; o[i+2]=e; o[i+3]=f; o[i+4]=g; o[i+5]=h; return 6;
        case 7: o[i]=b; o[i+1]=c; o[i+2]=d; o[i+3]=e; o[i+4]=f; o[i+5]=g; o[i+6]=h; return 7;
        default:o[i]=a; o[i+1]=b; o[i+2]=c; o[i+3]=d; o[i+4]=e; o[i+5]=f; o[i+6]=g; o[i+7]=h; return 8;
        }
    }

    /**
     * Can accept char, byte, short, int, or long.
     *
     * Note: Method encodes 0x00 as 0x00 // TODO define empty vs zero cases
     *
     * @param val
     * @param dest
     * @param destIndex
     * @return
     */
    public static int encode(final long val, final byte[] dest, final int destIndex) {
        if(ElementType.isSingleByteItem(val)) { // val >= 0 && val < ITEM_SHORT.offset
            dest[destIndex] = (byte) val;
            return destIndex + 1;
        }

//        byte[] source = encodeLong(val);
//        return encodeItem(source, 0, source.length, dest, destIndex);

        final int dataIndex = destIndex + 1;
        int numLengthBytes = putLong(val, dest, dataIndex);
        dest[destIndex] = (byte) (ITEM_SHORT.offset + numLengthBytes);
        return dataIndex + numLengthBytes;
    }

    public static int encode(BigInteger srcBigInteger, byte[] dest, int destIndex) {
//        if (srcBigInteger.equals(BigInteger.ZERO))
//            return encodeLong(0, o, i);
//        else
//            return encodeItem(asUnsignedByteArray(srcBigInteger), o, i);
        return encodeAsItem(srcBigInteger.toByteArray(), dest, destIndex);
    }

    public static int encode(String string, Charset charset, byte[] o, int i) {
        return encodeAsItem(string.getBytes(charset), o, i);
    }

    public static int encodeAsItem(byte[] source, byte[] dest, int destIndex) {
        return encodeAsItem(source, 0, source.length, dest, destIndex);
    }

    /**
     *
     * @param source
     * @param srcDataIndex
     * @param srcDataLen
     * @param dest
     * @param destIndex
     * @return  the index immediately after the encoded item
     */
    public static int encodeAsItem(byte[] source, int srcDataIndex, int srcDataLen, byte[] dest, int destIndex) {
        if(srcDataLen < LONG_DATA_THRESHOLD) {

            byte single;
            if (srcDataLen == 1 && ElementType.type((single = source[srcDataIndex])) == ITEM_SINGLE_BYTE) {
                dest[destIndex] = single;
                return destIndex + 1;
            }

            dest[destIndex] = (byte) (ITEM_SHORT.offset + srcDataLen);
            destIndex++;
        } else {
//            byte[] dataLenBytes = encodeLong(srcDataLen);
//            System.arraycopy(dataLenBytes, 0, dest, destIndex + 1, dataLenBytes.length);
            final int lengthIndex = destIndex + 1;
            int numLengthBytes = putLong(srcDataLen, dest, lengthIndex);
            dest[destIndex] = (byte) (ITEM_LONG.offset + numLengthBytes);
            destIndex = lengthIndex + numLengthBytes;
        }
        System.arraycopy(source, srcDataIndex, dest, destIndex, srcDataLen);
        return destIndex + srcDataLen;
    }
}

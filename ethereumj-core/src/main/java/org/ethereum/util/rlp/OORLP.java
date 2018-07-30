package org.ethereum.util.rlp;

import java.math.BigInteger;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.ethereum.util.rlp.ElementType.*;

/**
 * NOTE: 0x00 will always be encoded as 0x00
 */
public class OORLP {

    /* ENCODING */

    public static NewRLPItem encodeAsItem(BigInteger srcBigInteger) {
//        if (srcBigInteger.equals(BigInteger.ZERO))
//            return encodeItem(ZERO_BYTE);
//        else
//            return encodeItem(asUnsignedByteArray(srcBigInteger));
        return encodeAsItem(srcBigInteger.toByteArray());

    }

    public static NewRLPItem encodeAsItem(long val) {
        return NewRLPItem.fromEncoding(encodeLong(val));
    }

//    public static NewRLPItem encodeAsItem(char c) {
//        return encodeAsItem((byte) c);
//    }
//
//    public static NewRLPItem encodeAsItem(byte b) {
//        return encodeAsItem( new byte[] { b });
//    }

    public static NewRLPItem encodeAsItem(String string, Charset charset) {
        return encodeAsItem(string.getBytes(charset));
    }

    public static NewRLPItem encodeAsItem(byte[] data) {
        return encodeAsItem(data, 0, data.length);
    }

    /**
     * Note: Method encodes 0x00 as 0x00
     *
     * @param source
     * @return
     */
    public static NewRLPItem encodeAsItem(final byte[] source, final int srcDataIndex, final int srcDataLen) {

        byte[] dest;

        if (srcDataLen < LONG_DATA_THRESHOLD) {
            byte single;
            if (srcDataLen == 1 && ElementType.type((single = source[srcDataIndex])) == ITEM_SINGLE_BYTE) {
                return NewRLPItem.fromSingleByte(single);
            }

            dest = new byte[1 + srcDataLen];
            dest[0] = (byte) (ITEM_SHORT.offset + srcDataLen);
            System.arraycopy(source, srcDataIndex, dest, 1, srcDataLen);
        } else {

            dest = encodeElementLong(ITEM_LONG, source, srcDataIndex, srcDataLen);

//            final byte[] dataLenBytes = NewRLP.longToBytes(srcDataLen);
//            final int numLengthBytes = dataLenBytes.length;
//            final int totalPrefixLen = 1 + numLengthBytes;
//            buffer = new byte[totalPrefixLen + srcDataLen];
//            buffer[0] = (byte) (ElementType.ITEM_LONG.offset + numLengthBytes);
//            System.arraycopy(dataLenBytes, 0, buffer, 1, numLengthBytes);
//            System.arraycopy(source, srcDataIndex, buffer, totalPrefixLen, srcDataLen);
        }

        return NewRLPItem.fromEncoding(dest);
    }

    public static NewRLPList encodeAsList(Iterable<NewRLPElement> elements) {
        return NewRLPList.fromElements(elements);
    }

    public static NewRLPList encodeAsList(NewRLPElement... elements) {
        return NewRLPList.fromElements(elements);
    }

    public static NewRLPList encodeAsList(byte[] buffer) {
        return NewRLPList.fromData(buffer, 0, buffer.length);
    }

    public static NewRLPList encodeAsList(byte[] buffer, int dataIndex, int dataLen) {
        return NewRLPList.fromData(buffer, dataIndex, dataLen);
    }

    /**
     * Java arrays are limited in size to (roughly) {@code Integer#MAX_VALUE}, so {@code dataLen} is an {@code int} here.
     * @param type
     * @param source
     * @param srcDataIndex
     * @param srcDataLen
     * @return
     */
    static byte[] encodeElementLong(ElementType type, byte[] source, final int srcDataIndex, final  /*long*/int srcDataLen) {

//        if(!(type == ITEM_LONG || type == LIST_LONG)) {
//            throw new IllegalArgumentException("type must be " + ITEM_LONG + " or " + LIST_LONG);
//        }

        int t = srcDataLen;

        byte a = 0, b = 0, c = 0, d;

        int n = 1;
        d = (byte) (t & 0xFF);
        t = t >>> Byte.SIZE;
        if(t != 0) {
            n = 2;
            c = (byte) (t & 0xFF);
            t = t >>> Byte.SIZE;
            if(t != 0) {
                n = 3;
                b = (byte) (t & 0xFF);
                t = t >>> Byte.SIZE;
                if(t != 0) {
                    n = 4;
                    a = (byte) (t & 0xFF);
                }
            }
        }

        final int destDataIndex = 1 + n;
        byte[] encoding = new byte[destDataIndex + srcDataLen];
        encoding[0] = (byte) (type.offset + n);

        insertLengthBytes(encoding, n, a, b, c, d);
        System.arraycopy(source, srcDataIndex, encoding, destDataIndex, srcDataLen);

        return encoding;
    }

    static void insertLengthBytes(byte[] longElementEncoding, int n, byte a, byte b, byte c, byte d) {
        int i = 1;
        switch (n) {
        case 4: longElementEncoding[i++] = a;
        case 3: longElementEncoding[i++] = b;
        case 2: longElementEncoding[i++] = c;
        case 1: longElementEncoding[i] = d;
        }
    }

    static byte[] encodeLong(long val) {

        if(ElementType.isSingleByteItem(val)) {
            return new byte[] { (byte) val };
        }

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

        final byte lead = (byte) (ITEM_SHORT.offset + n);
        switch (n) {
        case 1: return new byte[] { lead, h };
        case 2: return new byte[] { lead, g, h };
        case 3: return new byte[] { lead, f, g, h };
        case 4: return new byte[] { lead, e, f, g, h };
        case 5: return new byte[] { lead, d, e, f, g, h };
        case 6: return new byte[] { lead, c, d, e, f, g, h };
        case 7: return new byte[] { lead, b, c, d, e, f, g, h };
        default: return new byte[]{ lead, a, b, c, d, e, f, g, h};
        }
    }

    /* DECODING */

    public static char decodeChar(byte[] buffer, int i) {
        return (char) decodeLong(buffer, i);
    }

    public static byte decodeByte(byte[] buffer, int i) {
        return (byte) decodeLong(buffer, i);
    }

    public static int decodeInt(byte[] buffer, int i) {
        return (int) decodeLong(buffer, i);
    }

    /**
     * "Deserialised positive integers with leading zeroes must be treated as invalid."
     *
     */
    public static long decodeLong(byte[] buffer, int i) {
        final byte first = buffer[i];
        switch (ElementType.type(first)) {
        case ITEM_SINGLE_BYTE: return first;
        case ITEM_SHORT: {
            return NewRLP.decodeLong(buffer, i + 1, first - ITEM_SHORT.offset);
        }
        default: throw new RuntimeException("wrong decode attempt");
        }
    }

    public static String decodeString(byte[] buffer) {
        return new String(decode(buffer, 0).buffer, UTF_8);
    }

    public static String decodeString(byte[] buffer, int index, Charset charset) {
        return new String(decode(buffer, index).buffer, charset);
    }

    public static BigInteger decodeBigInteger(byte[] buffer, int index) {
        return new BigInteger(decode(buffer, index).data());
    }

    public static NewRLPItem decodeItem(byte[] buffer) {
        return decodeItem(buffer, 0);
    }

    public static NewRLPList decodeList(byte[] buffer) {
        return decodeList(buffer, 0);
    }

    public static NewRLPElement decode(byte[] buffer) {
        return decode(buffer, 0);
    }

    public static NewRLPItem decodeItem(byte[] buffer, int index) {
        return (NewRLPItem) decode(buffer, index);
    }

    public static NewRLPList decodeList(byte[] buffer, int index) {
        return (NewRLPList) decode(buffer, index);
    }

    public static NewRLPElement decode(byte[] buffer, int index) {
        ElementType type = ElementType.type(buffer[index]);
        switch (type) {
        case ITEM_SINGLE_BYTE:
        case ITEM_SHORT:
        case ITEM_LONG:
            return NewRLPItem.fromEncoding(buffer, index);
        case LIST_SHORT:
        case LIST_LONG:
            return NewRLPList.fromEncoding(buffer, index);
        default:
            throw new RuntimeException("???");
        }
    }
}

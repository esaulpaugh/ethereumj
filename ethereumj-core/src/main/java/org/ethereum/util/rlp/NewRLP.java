package org.ethereum.util.rlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

import static org.ethereum.util.rlp.ElementType.ITEM_LONG;
import static org.ethereum.util.rlp.ElementType.ITEM_SHORT;
import static org.ethereum.util.rlp.ElementType.LIST_LONG;

// TODO split into encoder and decoder
public class NewRLP {

    private static final Logger logger = LoggerFactory.getLogger("newrlp");

    private static final int LONG_DATA_THRESHOLD = 56;

    /* ******************************************************
     *                      DECODING                        *
     * ******************************************************/

    public static long decodeLong(byte[] data) {
        return decodeLong(data, 0);
    }

    /**
     * "Deserialised positive integers with leading zeroes must be treated as invalid."
     *
     */
    public static long decodeLong(byte[] buffer, int i) {
        final byte first = buffer[i];
        switch (ElementType.type(first)) {
        case SINGLE_BYTE: return first;
        case ITEM_SHORT: {
            return decodeLong(buffer, i + 1, first - ITEM_SHORT.offset);
        }
        default: throw new RuntimeException("wrong decode attempt");
        }
    }

    public static long decodeLong(final byte[] buffer, final int i, final int numBytes) {

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
     * "Deserialised positive integers with leading zeroes must be treated as invalid."
     *
     */
//    public static long bytesToLong(byte[] data, int i, int len) {
//
//    }

//    public static int getFirstListElement(byte[] payload, int pos) {
//        return decode(payload, pos).getDataIndex();
//    }

//    public static byte[] longToBytes2(long val) {
//        byte[]
//    }

    // TODO
//    public static int encodeBigInteger(BigInteger srcBigInteger, byte[] o, int i) {
//        if (srcBigInteger.equals(BigInteger.ZERO))
//            return encodeLong(0, o, i);
//        else
//            return encodeBytes(asUnsignedByteArray(srcBigInteger), o, i);
//    }

//    static byte[] encodeLong(long val) {
//        if(val == 0) {// special case
//            return ZERO_BYTE_ARRAY;
//        }
//        byte[] x = longToBytes(val);
//        int n = putLong(val, o, i+1);
//        o[i] = (byte) (ElementType.ITEM_SHORT.offset + n);
//        return i + 1 + n;
//    }

//    public static byte[] longToBytes(long val) {
//
//        byte a = 0, b = 0, c = 0, d = 0, e = 0, f = 0, g = 0, h;
//
//        int n = 1;
//        h = (byte) (val & 0xFF);
//        val = val >>> Byte.SIZE;
//        if(val != 0) {
//            n = 2;
//            g = (byte) (val & 0xFF);
//            val = val >>> Byte.SIZE;
//            if(val != 0) {
//                n = 3;
//                f = (byte) (val & 0xFF);
//                val = val >>> Byte.SIZE;
//                if(val != 0) {
//                    n = 4;
//                    e = (byte) (val & 0xFF);
//                    val = val >>> Byte.SIZE;
//                    if(val != 0) {
//                        n = 5;
//                        d = (byte) (val & 0xFF);
//                        val = val >>> Byte.SIZE;
//                        if(val != 0) {
//                            n = 6;
//                            c = (byte) (val & 0xFF);
//                            val = val >>> Byte.SIZE;
//                            if(val != 0) {
//                                n = 7;
//                                b = (byte) (val & 0xFF);
//                                val = val >>> Byte.SIZE;
//                                if(val != 0) {
//                                    n = 8;
//                                    a = (byte) (val & 0xFF);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        switch (n) {
//        case 1: return new byte[] { h };
//        case 2: return new byte[] { g, h };
//        case 3: return new byte[] { f, g, h };
//        case 4: return new byte[] { e, f, g, h };
//        case 5: return new byte[] { d, e, f, g, h };
//        case 6: return new byte[] { c, d, e, f, g, h };
//        case 7: return new byte[] { b, c, d, e, f, g, h };
//        default: return new byte[]{ a, b, c, d, e, f, g, h};
//        }
//    }

    public static byte[] encodeShortItem(long val) {

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

        final byte lead = (byte) (ElementType.ITEM_SHORT.offset + n);
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

    /**
     *
     * @param val
     * @param o
     * @param i
     * @return  the number of bytes inserted
     */
    public static int putLong(long val, byte[] o, int i) {

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

    // TODO

    /**
     * Java arrays are limited in size to (roughly) {@code Integer#MAX_VALUE}, so {@code dataLen} is an {@code int} here.
     * @param type
     * @param source
     * @param srcDataIndex
     * @param srcDataLen
     * @return
     */
    public static byte[] encodeElementLong(ElementType type, byte[] source, final int srcDataIndex, final  /*long*/int srcDataLen) {

        if(!(type == ITEM_LONG || type == LIST_LONG)) {
            throw new IllegalArgumentException("type must be " + ITEM_LONG + " or " + LIST_LONG);
        }

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

        // [ 0, 1, 2, 3, 4 ]
//        case 1: return new byte[] { lead, d };
//        case 2: return new byte[] { lead, c, d };
//        case 3: return new byte[] { lead, b, c, d };
//        case 4: return new byte[] { lead, a, b, c, d };

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

    // TODO
    /**
     * Note: Method encodes 0x00 as 0x00 // TODO define empty vs zero cases
     * @param val
     * @param o
     * @param i
     * @return
     */
    public static int encodeLong(long val, byte[] o, int i) {
        if(val == 0) {// special case
//            o[i] = ElementType.ITEM_SHORT.offset;
            o[i] = 0x00;
            return i + 1;
        }
        int n = putLong(val, o, i+1);
        o[i] = (byte) (ElementType.ITEM_SHORT.offset + n);
        return i + 1 + n;
    }

    public static int encodeString(String string, Charset charset, byte[] o, int i) {
        return encodeBytes(string.getBytes(charset), o, i);
    }

    public static int encodeBytes(byte[] source, byte[] o, int i) {
        int dataIndex;
        final int dataLen = source.length;
        if(dataLen < LONG_DATA_THRESHOLD) {
            dataIndex = i + 1;
            o[i] = (byte) (ElementType.ITEM_SHORT.offset + dataLen);
        } else {
            int numLengthBytes = putLong(dataLen, o, i + 1);
            dataIndex = i + 1 + numLengthBytes;
            o[i] = (byte) (ITEM_LONG.offset + numLengthBytes);
        }
        System.arraycopy(source, 0, o, dataIndex, dataLen);
        return dataIndex + dataLen;
    }
}

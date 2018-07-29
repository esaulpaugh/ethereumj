package org.ethereum.util.rlp;

import org.ethereum.util.ElementType;

import java.math.BigInteger;
import java.nio.charset.Charset;

import static org.ethereum.util.ElementType.ITEM_SHORT;
import static org.spongycastle.util.BigIntegers.asUnsignedByteArray;

public class NewRLP {

//    private static final Logger logger = LoggerFactory.getLogger("rlp");

    private static final byte[] ZERO_BYTE_ARRAY = new byte[] { (byte) 0x80 };

    private static final int LONG_DATA_THRESHOLD = 56;

    /**
     * Allow for content up to size of 2^64 bytes *
     */
    private static final double MAX_ITEM_LENGTH = Math.pow(256, 8);

    /**
     * Reason for threshold according to Vitalik Buterin:
     * - 56 bytes maximizes the benefit of both options
     * - if we went with 60 then we would have only had 4 slots for long strings
     * so RLP would not have been able to store objects above 4gb
     * - if we went with 48 then RLP would be fine for 2^128 space, but that's way too much
     * - so 56 and 2^64 space seems like the right place to put the cutoff
     * - also, that's where Bitcoin's varint does the cutof
     */
    private static final int SIZE_THRESHOLD = 56;

    /** RLP encoding rules are defined as follows: */

    /*
     * For a single byte whose value is in the [0x00, 0x7f] range, that byte is
     * its own RLP encoding.
     */

    /**
     * [0x80]
     * If a string is 0-55 bytes long, the RLP encoding consists of a single
     * byte with value 0x80 plus the length of the string followed by the
     * string. The range of the first byte is thus [0x80, 0xb7].
     */
    private static final int OFFSET_SHORT_ITEM = 0x80;

    /**
     * [0xb7]
     * If a string is more than 55 bytes long, the RLP encoding consists of a
     * single byte with value 0xb7 plus the length of the length of the string
     * in binary form, followed by the length of the string, followed by the
     * string. For example, a length-1024 string would be encoded as
     * \xb9\x04\x00 followed by the string. The range of the first byte is thus
     * [0xb8, 0xbf].
     */
    private static final int OFFSET_LONG_ITEM = 0xb7; // 0x80 + 55

    /**
     * [0xc0]
     * If the total payload of a list (i.e. the combined length of all its
     * items) is 0-55 bytes long, the RLP encoding consists of a single byte
     * with value 0xc0 plus the length of the list followed by the concatenation
     * of the RLP encodings of the items. The range of the first byte is thus
     * [0xc0, 0xf7].
     */
    private static final int OFFSET_SHORT_LIST = 0xc0;

    /**
     * [0xf7]
     * If the total payload of a list is more than 55 bytes long, the RLP
     * encoding consists of a single byte with value 0xf7 plus the length of the
     * length of the list in binary form, followed by the length of the list,
     * followed by the concatenation of the RLP encodings of the items. The
     * range of the first byte is thus [0xf8, 0xff].
     */
    private static final int OFFSET_LONG_LIST = 0xf7;


    /* ******************************************************
     *                      DECODING                        *
     * ******************************************************/

    public static long decodeLong(byte[] data) {
        return decodeLong(data, 0);
    }

    public static long decodeLong(byte[] data, int i) {
        final byte first = data[i];
        switch (ElementType.type(first)) {
        case SINGLE_BYTE: return first;
        case ITEM_SHORT: {
            int length = first - ITEM_SHORT.offset;
            return bytesToLong(data, i + 1, length);
        }
        default: throw new RuntimeException("wrong decode attempt");
        }
    }

    public static long bytesToLong(byte[] data, int i, int len) {
        long val = 0;
        switch (len) {// cases all fall through
        case 8: val |= (data[i++] & 0xFFL) << (Byte.SIZE * 7);
        case 7: val |= (data[i++] & 0xFFL) << (Byte.SIZE * 6);
        case 6: val |= (data[i++] & 0xFFL) << (Byte.SIZE * 5);
        case 5: val |= (data[i++] & 0xFFL) << (Byte.SIZE * 4);
        case 4: val |= (data[i++] & 0xFFL) << (Byte.SIZE * 3);
        case 3: val |= (data[i++] & 0xFFL) << (Byte.SIZE * 2);
        case 2: val |= (data[i++] & 0xFFL) << Byte.SIZE;
        case 1: val |= data[i] & 0xFFL;
        default: return val;
        }
    }

//    public static int getFirstListElement(byte[] payload, int pos) {
//        return decode(payload, pos).getDataIndex();
//    }

    // -------------------------------------------------------------------------------------------------------------------
//
//    /**
//     * Reads any RLP encoded byte-array and returns all objects as byte-array or list of byte-arrays
//     *
//     * @param data RLP encoded byte-array
//     * @param pos  position in the array to start reading
//     * @return DecodeResult encapsulates the decoded items as a single Object and the final read position
//     */
//    public static DecodeResult decode(byte[] data, int pos) {
//        if (data == null || data.length < 1) {
//            return null;
//        }
//        int prefix = data[pos] & 0xFF;
//        if (prefix == OFFSET_SHORT_ITEM) {  // 0x80
//            return new DecodeResult(pos + 1, ""); // means no length or 0
//        } else if (prefix < OFFSET_SHORT_ITEM) {  // [0x00, 0x7f]
//            return new DecodeResult(pos + 1, new byte[]{data[pos]}); // byte is its own RLP encoding
//        } else if (prefix <= OFFSET_LONG_ITEM) {  // [0x81, 0xb7]
//            int len = prefix - OFFSET_SHORT_ITEM; // length of the encoded bytes
//            return new DecodeResult(pos + 1 + len, copyOfRange(data, pos + 1, pos + 1 + len));
//        } else if (prefix < OFFSET_SHORT_LIST) {  // [0xb8, 0xbf]
//            int lenlen = prefix - OFFSET_LONG_ITEM; // length of length the encoded bytes
//            int lenbytes = byteArrayToInt(copyOfRange(data, pos + 1, pos + 1 + lenlen)); // length of encoded bytes
//            return new DecodeResult(pos + 1 + lenlen + lenbytes, copyOfRange(data, pos + 1 + lenlen, pos + 1 + lenlen
//                    + lenbytes));
//        } else if (prefix <= OFFSET_LONG_LIST) {  // [0xc0, 0xf7]
//            int len = prefix - OFFSET_SHORT_LIST; // length of the encoded list
//            int prevPos = pos;
//            pos++;
//            return decodeList(data, pos, prevPos, len);
//        } else if (prefix <= 0xFF) {  // [0xf8, 0xff]
//            int lenlen = prefix - OFFSET_LONG_LIST; // length of length the encoded list
//            int lenlist = byteArrayToInt(copyOfRange(data, pos + 1, pos + 1 + lenlen)); // length of encoded bytes
//            pos = pos + lenlen + 1; // start at position of first element in list
//            int prevPos = lenlist;
//            return decodeList(data, pos, prevPos, lenlist);
//        } else {
//            throw new RuntimeException("Only byte values between 0x00 and 0xFF are supported, but got: " + prefix);
//        }
//    }
//
//    private static DecodeResult decodeList(byte[] data, int pos, int prevPos, int len) {
//        List<Object> slice = new ArrayList<>();
//        for (int i = 0; i < len; ) {
//            // Get the next item in the data list and append it
//            DecodeResult result = decode(data, pos);
//            slice.add(result.getDecoded());
//            // Increment pos by the amount bytes in the previous read
//            prevPos = result.getPos();
//            i += (prevPos - pos);
//            pos = prevPos;
//        }
//        return new DecodeResult(pos, slice.toArray());
//    }

//    public static byte[] intToBytes(int val) {
//
//        if (val == 0) {
//            return EMPTY_BYTE_ARRAY; // TODO 0x00 or 0x80 ?
//        }
//
//        byte a = 0, b = 0, c = 0, d;
//
//        int n = 1;
//        d = (byte) (val & 0xFF);
//        val = val >>> Byte.SIZE;
//        if(val != 0) {
//            n = 2;
//            c = (byte) (val & 0xFF);
//            val = val >>> Byte.SIZE;
//            if(val != 0) {
//                n = 3;
//                b = (byte) (val & 0xFF);
//                val = val >>> Byte.SIZE;
//                if(val != 0) {
//                    n = 4;
//                    a = (byte) (val & 0xFF);
//                }
//            }
//        }
//
//        switch (n) {
//        case 1: return new byte[] { d };
//        case 2: return new byte[] { c, d };
//        case 3: return new byte[] { b, c, d };
//        default: return new byte[] { a, b, c, d };
//        }
//    }

//    static final byte[] x = new byte[Long.BYTES];
//    public static byte[] longToBytes3(long v) {
//
//        x[0] = (byte)(v >>> 56);
//        x[1] = (byte)(v >>> 48);
//        x[2] = (byte)(v >>> 40);
//        x[3] = (byte)(v >>> 32);
//        x[4] = (byte)(v >>> 24);
//        x[5] = (byte)(v >>> 16);
//        x[6] = (byte)(v >>>  8);
//        x[7] = (byte) v;
//
//        int i = 0;
//        while(i < Long.BYTES && x[i] == 0) {
//            i++;
//        }
//
//        return Arrays.copyOfRange(x, i, Long.BYTES);
//    }
//
//    public static byte[] longToBytes2(long val) {
//
//        if(val == 0) {
//            return EMPTY_BYTE_ARRAY;
//        }
//
//        byte[] temp = new byte[Long.BYTES];
//
//        int n = 8;
//        while(val != 0) {
//            temp[--n] = (byte) (val & 0xFF);
//            val = val >>> Byte.SIZE;
//        }
//        byte[] bytes = new byte[Long.BYTES - n];
//        System.arraycopy(temp, n, bytes, 0, bytes.length);
//
//        return bytes;
//    }

    public static byte[] longToBytes(long val) {

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
        case 1: return new byte[] { h };
        case 2: return new byte[] { g, h };
        case 3: return new byte[] { f, g, h };
        case 4: return new byte[] { e, f, g, h };
        case 5: return new byte[] { d, e, f, g, h };
        case 6: return new byte[] { c, d, e, f, g, h };
        case 7: return new byte[] { b, c, d, e, f, g, h };
        default: return new byte[]{ a, b, c, d, e, f, g, h};
        }
    }

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

//    /* ******************************************************
//     *                      ENCODING                        *
//     * ******************************************************/

    public static int encodeBigInteger(BigInteger srcBigInteger, byte[] o, int i) {
        if (srcBigInteger.equals(BigInteger.ZERO))
            return encodeLong(0, o, i);
        else
            return encodeBytes(asUnsignedByteArray(srcBigInteger), o, i);
    }

//    static byte[] encodeLong(long val) {
//        if(val == 0) {// special case
//            return ZERO_BYTE_ARRAY;
//        }
//        byte[] x = longToBytes(val);
//        int n = putLong(val, o, i+1);
//        o[i] = (byte) (ElementType.ITEM_SHORT.offset + n);
//        return i + 1 + n;
//    }

    /**
     * Note: Method encodes 0x00 as 0x80 // TODO define empty vs zero cases
     * @param val
     * @param o
     * @param i
     * @return
     */
    public static int encodeLong(long val, byte[] o, int i) {
        if(val == 0) {// special case
            o[i] = ElementType.ITEM_SHORT.offset;
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
            int lol = putLong(dataLen, o, i + 1);
            dataIndex = i + 1 + lol;
            o[i] = (byte) (ElementType.ITEM_LONG.offset + lol);
        }
        System.arraycopy(source, 0, o, dataIndex, dataLen);
        return dataIndex + dataLen;
    }
}

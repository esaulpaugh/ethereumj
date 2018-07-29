package org.ethereum.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.ethereum.util.ElementType.ITEM_SHORT;
import static org.ethereum.util.NewRLPItem.EMPTY_ITEM;
import static org.spongycastle.util.BigIntegers.asUnsignedByteArray;

public class NewRLP {

//    private static final Logger logger = LoggerFactory.getLogger("rlp");

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

    public static short decodeShort(byte[] data) {
        return (short) decodeLong(data, 0);
    }

    public static int decodeInt(byte[] data) {
        return (int) decodeLong(data, 0);
    }

    public static long decodeLong(byte[] data) {
        return decodeLong(data, 0);
    }

    public static short decodeShort(byte[] data, int index) {
        return (short) decodeLong(data, index);
    }

    public static int decodeInt(byte[] data, int index) {
        return (int) decodeLong(data, index);
    }

    public static long decodeLong(byte[] data, int index) {
        final byte first = data[index];
        switch (ElementType.type(first)) {
        case SINGLE_BYTE: return first;
        case ITEM_SHORT: {
            int length = first - ITEM_SHORT.offset;
            long val = 0;
            int i = index;
            switch (length) {
            case 8: val += (long) (data[++i] & 0xFF) << (Byte.SIZE * 7);
            case 7: val += (long) (data[++i] & 0xFF) << (Byte.SIZE * 6);
            case 6: val += (long) (data[++i] & 0xFF) << (Byte.SIZE * 5);
            case 5: val += (long) (data[++i] & 0xFF) << (Byte.SIZE * 4);
            case 4: val += (data[++i] & 0xFF) << (Byte.SIZE * 3);
            case 3: val += (data[++i] & 0xFF) << (Byte.SIZE * 2);
            case 2: val += (data[++i] & 0xFF) << Byte.SIZE;
            case 1: val += (data[++i] & 0xFF);
            default: return val;
            }
        }
        default: throw new RuntimeException("wrong decode attempt");
        }
    }

    public static long bytesToLong(byte[] data) {

//        if(data.length == 0) {
//            return 0;
//        }

//        byte length = (byte) (data[index] - ITEM_SHORT.offset);

        int index = 0;

        long val = 0;
        int i = index;
        switch (data.length) {
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

    private static String decodeStringItem(byte[] data) {
        return new String(decode(data, 0).rlpData, UTF_8);
    }

    private static String decodeStringItem(byte[] data, int index, Charset charset) {
        return new String(decode(data, index).rlpData, charset);
    }

    public static BigInteger decodeBigInteger(byte[] data, int index) {
        return new BigInteger(1, NewRLP.decode(data, index).getData());
    }

    public static int getFirstListElement(byte[] payload, int pos) {
        return NewRLP.decode(payload, pos).getDataIndex();
    }

    public static int nextElementIndex(NewRLPElement e) {
        return e.getRLPIndex() + e.getRlpLength();
    }

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
//
//    /* ******************************************************
//     *                      ENCODING                        *
//     * ******************************************************/
//
    public static byte[] encodeByte(byte singleByte) {
        if ((singleByte & 0xFF) == 0) {
            return new byte[]{(byte) OFFSET_SHORT_ITEM};
        } else if ((singleByte & 0xFF) <= 0x7F) {
            return new byte[]{singleByte};
        } else {
            return new byte[]{(byte) (OFFSET_SHORT_ITEM + 1), singleByte};
        }
    }

    public static NewRLPItem encodeShort(short singleShort) {
        return encodeInt(singleShort);
    }

    public static NewRLPItem encodeInt(int singleInt) {
        return encodeItem(intToBytes(singleInt));
    }
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static byte[] intToBytes(int val) {

        if (val == 0) {
            return EMPTY_BYTE_ARRAY; // TODO 0x00 or 0x80 ?
        }

        byte a = 0, b = 0, c = 0, d;

        int n = 1;
        d = (byte) (val & 0xFF);
        val = val >>> Byte.SIZE;
        if(val != 0) {
            n = 2;
            c = (byte) (val & 0xFF);
            val = val >>> Byte.SIZE;
            if(val != 0) {
                n = 3;
                b = (byte) (val & 0xFF);
                val = val >>> Byte.SIZE;
                if(val != 0) {
                    n = 4;
                    a = (byte) (val & 0xFF);
                }
            }
        }

        switch (n) {
        case 1: return new byte[] { d };
        case 2: return new byte[] { c, d };
        case 3: return new byte[] { b, c, d };
        default: return new byte[] { a, b, c, d };
        }
    }

    static final byte[] x = new byte[Long.BYTES];
    public static byte[] longToBytes3(long v) {

        x[0] = (byte)(v >>> 56);
        x[1] = (byte)(v >>> 48);
        x[2] = (byte)(v >>> 40);
        x[3] = (byte)(v >>> 32);
        x[4] = (byte)(v >>> 24);
        x[5] = (byte)(v >>> 16);
        x[6] = (byte)(v >>>  8);
        x[7] = (byte) v;

        int i = 0;
        while(i < Long.BYTES && x[i] == 0) {
            i++;
        }

        return Arrays.copyOfRange(x, i, Long.BYTES);
    }

    public static byte[] longToBytes2(long val) {

        if(val == 0) {
            return EMPTY_BYTE_ARRAY;
        }

        byte[] temp = new byte[Long.BYTES];

        int n = 8;
        while(val != 0) {
            temp[--n] = (byte) (val & 0xFF);
            val = val >>> Byte.SIZE;
        }
        byte[] bytes = new byte[Long.BYTES - n];
        System.arraycopy(temp, n, bytes, 0, bytes.length);

        return bytes;
    }

    public static byte[] longToBytes(long val) {

        if(val == 0) {
            return EMPTY_BYTE_ARRAY;
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

    public static NewRLPItem encodeString(String srcString) {
        return encodeItem(srcString.getBytes());
    }

    public static NewRLPItem encodeBigInteger(BigInteger srcBigInteger) {
        if (srcBigInteger.equals(BigInteger.ZERO))
            return encodeItem(new byte[] { 0 });
        else
            return encodeItem(asUnsignedByteArray(srcBigInteger));
    }

    // ====================================

    public static NewRLPElement decode(byte[] rlpData) {
        return decode(rlpData, 0);
    }

    public static NewRLPItem decodeItem(byte[] rlpData) {
        return (NewRLPItem) decode(rlpData, 0);
    }

    public static NewRLPItem decodeItem(byte[] rlpData, int rlpIndex) {
        return (NewRLPItem) decode(rlpData, rlpIndex);
    }

    public static NewRLPList decodeList(byte[] rlpData) {
        return (NewRLPList) decode(rlpData, 0);
    }

    public static NewRLPList decodeList(byte[] rlpData, int rlpIndex) {
        return (NewRLPList) decode(rlpData, rlpIndex);
    }

    public static NewRLPElement decode(byte[] rlpData, int rlpIndex) {

        if(rlpData == null) {
            return null;
        }

        if(rlpData.length == 0) {
            return EMPTY_ITEM;
        }

        ElementType type = ElementType.type(rlpData[rlpIndex]);
        switch (type) {
        case SINGLE_BYTE:
        case ITEM_SHORT:
        case ITEM_LONG:
            return new NewRLPItem(rlpData, rlpIndex);
        case LIST_SHORT:
        case LIST_LONG:
            return new NewRLPList(rlpData, rlpIndex, null).build();
        default:
            throw new RuntimeException("???");
        }
    }

    public static NewRLPItem encodeItem(String string, Charset charset) {
        return encodeItem(string.getBytes(charset));
    }

    public static NewRLPItem encodeItem(byte[] data) {

        ElementType type;
        byte[] rlpData;

        int dataLen = data.length;

        if (dataLen < LONG_DATA_THRESHOLD) {
            if (dataLen == 1) {
                byte single = data[0];

                if ((single & 0xFF) < 0x80) {
                    // single == 0 ? (byte) ElementType.ITEM_SHORT.getOffset() :
                    // NOTE: there are two ways zero can be encoded - 0x00 and OFFSET_SHORT_ITEM
                    return new NewRLPItem(new byte[] { single }, 0);
                }
            }

            type = ElementType.ITEM_SHORT;

            rlpData = new byte[1 + dataLen];
            rlpData[0] = (byte) (type.offset + dataLen);
            System.arraycopy(data, 0, rlpData, 1, dataLen);
        } else {

            type = ElementType.ITEM_LONG;

            final byte[] dataLenBytes = NewRLP.intToBytes(dataLen);
            final int numLengthBytes = dataLenBytes.length;

            rlpData = new byte[1 + numLengthBytes + dataLen];
            rlpData[0] = (byte) (type.offset + numLengthBytes);
            System.arraycopy(dataLenBytes, 0, rlpData, 1, numLengthBytes);
            System.arraycopy(data, 0, rlpData, 1 + numLengthBytes, dataLen);
        }

        return new NewRLPItem(rlpData, 0);
    }

    /**
     *
     * @param elements pre-encoded top-level elements of the list
     * @return
     */
    public static NewRLPElement encodeList(NewRLPElement... elements) {

        ElementType type;
        byte[] rlpData;
        if (elements == null) {
            type = ElementType.LIST_SHORT;
            rlpData = new byte[] { type.offset };
        } else {

            int dataLen = 0;
            for (NewRLPElement element : elements) {
                dataLen += element.rlpData.length;
            }

            int destPos;

            if (dataLen < LONG_DATA_THRESHOLD) {

                type = ElementType.LIST_SHORT;

                rlpData = new byte[1 + dataLen];
                rlpData[0] = (byte) (type.offset + dataLen);
                destPos = 1;
            } else {

                type = ElementType.LIST_LONG;

                final byte[] dataLenBytes = NewRLP.intToBytes(dataLen);
                final int numLengthBytes = dataLenBytes.length;

                rlpData = new byte[1 + numLengthBytes + dataLen];
                rlpData[0] = (byte) (type.offset + numLengthBytes);
                System.arraycopy(dataLenBytes, 0, rlpData, 1, numLengthBytes);

                destPos = 1 + numLengthBytes;
            }

            for (final NewRLPElement element : elements) {
                System.arraycopy(element.rlpData, 0, rlpData, destPos, element.rlpData.length);
                destPos += element.rlpData.length;
            }
        }

        return new NewRLPList(rlpData, 0, elements);
    }
}

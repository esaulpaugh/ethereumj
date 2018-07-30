package org.ethereum.util.rlp;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.spongycastle.util.BigIntegers.asUnsignedByteArray;

/**
 *
 */
public class OORLP {

    private static final byte[] ZERO_BYTE = new byte[] { (byte) 0x00 };
    private static final byte[] EMPTY_ITEM = new byte[] { (byte) 0x80 };

    private static final int LONG_DATA_THRESHOLD = 56;

    /* ENCODING */

    public NewRLPItem encodeBigIntegerItem(BigInteger srcBigInteger) {
        if (srcBigInteger.equals(BigInteger.ZERO))
            return encodeItem(ZERO_BYTE);
        else
            return encodeItem(asUnsignedByteArray(srcBigInteger));
    }

//    public NewRLPItem encodeLong(long val) {
//        byte[] nineBytes = new byte[9];
//        NewRLP.encodeLong(val, nineBytes, 0);
//        return new NewRLPItem(nineBytes, 0);
//    }

    // TODO SEPARATE DECODER FROM ENCODER

    public NewRLPItem encodeItem(long val) {
        return new NewRLPItem(NewRLP.encodeShortItem(val));
    }

    public NewRLPItem encodeItem(char c) {
        return encodeItem(new byte[] { (byte) c });
    }

    public NewRLPItem encodeItem(String string, Charset charset) {
        return encodeItem(string.getBytes(charset));
    }

    public NewRLPItem encodeItem(byte[] data) {
        return encodeItem(data, 0, data.length);
    }

//    public NewRLPItem encodeItem(byte[] source, int dataIndex, int dataLen, final byte[] dest, final int destIndex) {
//
//    }

    /**
     * Note: Method encodes 0x00 as 0x00
     *
     * @param source
     * @return
     */
    public NewRLPItem encodeItem(final byte[] source, final int srcDataIndex, final int srcDataLen) {

        System.out.println("encodeItem()");

        byte[] dest;

        if (srcDataLen < LONG_DATA_THRESHOLD) {
            if (srcDataLen == 1) {
                byte single = source[srcDataIndex];

                if ((single & 0xFF) < 0x80) {
                    // single == 0 ? (byte) ElementType.ITEM_SHORT.getOffset() :
                    // NOTE: there are two ways zero can be encoded - 0x00 and OFFSET_SHORT_ITEM
                    return new NewRLPItem(single);
                }
            }

//            NewRLP.encodeShortItem()

            dest = new byte[1 + srcDataLen];
            dest[0] = (byte) (ElementType.ITEM_SHORT.offset + srcDataLen);
            System.arraycopy(source, srcDataIndex, dest, 1, srcDataLen);
        } else {

            dest = NewRLP.encodeElementLong(ElementType.ITEM_LONG, source, srcDataIndex, srcDataLen);

//            final byte[] dataLenBytes = NewRLP.longToBytes(srcDataLen);
//            final int numLengthBytes = dataLenBytes.length;
//            final int totalPrefixLen = 1 + numLengthBytes;
//            buffer = new byte[totalPrefixLen + srcDataLen];
//            buffer[0] = (byte) (ElementType.ITEM_LONG.offset + numLengthBytes);
//            System.arraycopy(dataLenBytes, 0, buffer, 1, numLengthBytes);
//            System.arraycopy(source, srcDataIndex, buffer, totalPrefixLen, srcDataLen);
        }

        return new NewRLPItem(dest);
    }

    public NewRLPElement encodeList(NewRLPElement... elements) {
        return encodeList(Arrays.asList(elements));
    }

    /**
     *
     * @param elements pre-encoded top-level elements of the list
     * @return
     */
    public NewRLPElement encodeList(Collection<NewRLPElement> elements) {

        System.out.println("encodeList()");

        if (elements == null) {
            throw new IllegalArgumentException("elements cannot be null");
        }

        ElementType type;
        byte[] dest;

        int dataLen = 0;
        for (NewRLPElement element : elements) {
            dataLen += element.encodingLength();
        }

        int destPos;

        if (dataLen < LONG_DATA_THRESHOLD) {

            type = ElementType.LIST_SHORT;

            dest = new byte[1 + dataLen];
            dest[0] = (byte) (type.offset + dataLen);
            destPos = 1;

            for (final NewRLPElement element : elements) {
                final int elementLen = element.encodingLength();
                System.arraycopy(element.buffer, element.index, dest, destPos, elementLen);
                destPos += elementLen;
            }

        } else {
//            type = ElementType.LIST_LONG;
//            final byte[] dataLenBytes = NewRLP.longToBytes(dataLen);
//            final int numLengthBytes = dataLenBytes.length;
//            final int totalPrefixLen = 1 + numLengthBytes;
//            dest = new byte[totalPrefixLen + dataLen];
//            dest[0] = (byte) (type.offset + numLengthBytes);
//            System.arraycopy(dataLenBytes, 0, dest, 1, numLengthBytes);
//
//            destPos = totalPrefixLen;

            dest = encodeListLong(dataLen, elements);
        }

        return new NewRLPList(
                dest,
                0,
                elements instanceof ArrayList ? (ArrayList<NewRLPElement>) elements : new ArrayList<>(elements)
        );
    }

    public static byte[] encodeListLong(final int srcDataLen, final Iterable<NewRLPElement> srcElements) { // prefixOffset typically 0x80, 0xb7, 0xc0, or 0xf7

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

        int destDataIndex = 1 + n;
        byte[] longListEncoding = new byte[destDataIndex + srcDataLen];
        longListEncoding[0] = (byte) (ElementType.LIST_LONG.offset + n);

        // [ 0, 1, 2, 3, 4 ]
//        case 1: return new byte[] { prefix, d };
//        case 2: return new byte[] { prefix, c, d };
//        case 3: return new byte[] { prefix, b, c, d };
//        case 4: return new byte[] { prefix, a, b, c, d };

        NewRLP.insertLengthBytes(longListEncoding, n, a, b, c, d);

        for (final NewRLPElement element : srcElements) {
            final int elementLen = element.encodingLength();
            System.arraycopy(element.buffer, element.index, longListEncoding, destDataIndex, elementLen);
            destDataIndex += elementLen;
        }

        return longListEncoding;
    }

    /* DECODING */

//    private static String decodeStringItem(byte[] data) {
//        return new String(decode(data, 0).buffer, UTF_8);
//    }
//
//    private static String decodeStringItem(byte[] data, int index, Charset charset) {
//        return new String(decode(data, index).buffer, charset);
//    }
//
//    public static BigInteger decodeBigInteger(byte[] data, int index) {
//        return new BigInteger(1, decode(data, index).getData());
//    }

//    public static int nextElementIndex(NewRLPElement e) {
//        return e.getindex() + e.encodingLength();
//    }

    public NewRLPElement decode(byte[] buffer) {
        return decode(buffer, 0);
    }

    public NewRLPItem decodeItem(byte[] buffer) {
        return (NewRLPItem) decode(buffer, 0);
    }

    public NewRLPItem decodeItem(byte[] buffer, int index) {
        return (NewRLPItem) decode(buffer, index);
    }

    public NewRLPList decodeList(byte[] buffer) {
        return (NewRLPList) decode(buffer, 0);
    }

    public NewRLPList decodeList(byte[] buffer, int index) {
        return (NewRLPList) decode(buffer, index);
    }

    public NewRLPElement decode(byte[] buffer, int index) {

        System.out.println("decode()");

//        if(buffer == null || buffer.length == 0) {
//            throw new IllegalArgumentException("buffer must be nonnull and nonempty");
//        }

        ElementType type = ElementType.type(buffer[index]);
        switch (type) {
        case SINGLE_BYTE:
        case ITEM_SHORT:
        case ITEM_LONG:
            return new NewRLPItem(buffer, index);
        case LIST_SHORT:
        case LIST_LONG:
            return new NewRLPList(buffer, index, null).build();
        default:
            throw new RuntimeException("???");
        }
    }
}

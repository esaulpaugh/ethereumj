package org.ethereum.util.rlp;

import org.ethereum.util.ElementType;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.spongycastle.util.BigIntegers.asUnsignedByteArray;

public class OORLP {

    private static final int LONG_DATA_THRESHOLD = 56;

    /* ENCODING */

    public static NewRLPItem encodeBigIntegerItem(BigInteger srcBigInteger) {
        if (srcBigInteger.equals(BigInteger.ZERO))
            return encodeItem(new byte[] { 0 });
        else
            return encodeItem(asUnsignedByteArray(srcBigInteger));
    }

    public static NewRLPItem encodeLong(long val) {
        return encodeItem(NewRLP.longToBytes(val));
    }

    public static NewRLPItem encodeChar(char c) {
        return encodeItem(new byte[] { (byte) c });
    }

    public static NewRLPItem encodeItem(String string, Charset charset) {
        return encodeItem(string.getBytes(charset));
    }

    public static NewRLPItem encodeItem(byte[] data) {
        return encodeItem(data, 0, data.length);
    }

    /**
     * Note: Method encodes 0x00 as 0x00
     *
     * @param source
     * @return
     */
    public static NewRLPItem encodeItem(byte[] source, int dataIndex, int dataLen) {

        System.out.println("encodeItem()");

        ElementType type;
        byte[] buffer;

        if (dataLen < LONG_DATA_THRESHOLD) {
            if (dataLen == 1) {
                byte single = source[dataIndex];

                if ((single & 0xFF) < 0x80) {
                    // single == 0 ? (byte) ElementType.ITEM_SHORT.getOffset() :
                    // NOTE: there are two ways zero can be encoded - 0x00 and OFFSET_SHORT_ITEM
                    return new NewRLPItem(new byte[] { single }, dataIndex);
                }
            }

            type = ElementType.ITEM_SHORT;

            buffer = new byte[1 + dataLen];
            buffer[0] = (byte) (type.offset + dataLen);
            System.arraycopy(source, 0, buffer, 1, dataLen);
        } else {

            type = ElementType.ITEM_LONG;

            final byte[] dataLenBytes = NewRLP.longToBytes(dataLen);
            final int numLengthBytes = dataLenBytes.length;

            buffer = new byte[1 + numLengthBytes + dataLen];
            buffer[0] = (byte) (type.offset + numLengthBytes);
            System.arraycopy(dataLenBytes, 0, buffer, 1, numLengthBytes);
            System.arraycopy(source, 0, buffer, 1 + numLengthBytes, dataLen);
        }

        return new NewRLPItem(buffer, 0);
    }

    public static NewRLPElement encodeList(NewRLPElement... elements) {
        return encodeList(Arrays.asList(elements));
    }

    /**
     *
     * @param elements pre-encoded top-level elements of the list
     * @return
     */
    public static NewRLPElement encodeList(Collection<NewRLPElement> elements) {

        System.out.println("encodeList()");

        if (elements == null) {
            throw new IllegalArgumentException("elements cannot be null");
        }

        ElementType type;
        byte[] buffer;

        int dataLen = 0;
        for (NewRLPElement element : elements) {
            dataLen += element.getEncoding().length;
        }

        int destPos;

        if (dataLen < LONG_DATA_THRESHOLD) {

            type = ElementType.LIST_SHORT;

            buffer = new byte[1 + dataLen];
            buffer[0] = (byte) (type.offset + dataLen);
            destPos = 1;
        } else {

            type = ElementType.LIST_LONG;

            final byte[] dataLenBytes = NewRLP.longToBytes(dataLen);
            final int numLengthBytes = dataLenBytes.length;

            buffer = new byte[1 + numLengthBytes + dataLen];
            buffer[0] = (byte) (type.offset + numLengthBytes);
            System.arraycopy(dataLenBytes, 0, buffer, 1, numLengthBytes);

            destPos = 1 + numLengthBytes;
        }

        for (final NewRLPElement element : elements) {
            System.arraycopy(element.buffer, 0, buffer, destPos, element.buffer.length);
            destPos += element.buffer.length;
        }

        return new NewRLPList(
                buffer,
                0,
                elements instanceof ArrayList ? (ArrayList<NewRLPElement>) elements : new ArrayList<>(elements)
        );
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

    public static NewRLPElement decode(byte[] buffer) {
        return decode(buffer, 0);
    }

    public static NewRLPItem decodeItem(byte[] buffer) {
        return (NewRLPItem) decode(buffer, 0);
    }

    public static NewRLPItem decodeItem(byte[] buffer, int index) {
        return (NewRLPItem) decode(buffer, index);
    }

    public static NewRLPList decodeList(byte[] buffer) {
        return (NewRLPList) decode(buffer, 0);
    }

    public static NewRLPList decodeList(byte[] buffer, int index) {
        return (NewRLPList) decode(buffer, index);
    }

    public static NewRLPElement decode(byte[] buffer, int index) {

        System.out.println("decode()");

        if(buffer == null || buffer.length == 0) {
            throw new IllegalArgumentException("buffer must be nonnull and nonempty");
        }

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

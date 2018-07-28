package org.ethereum.util;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Evo on 1/19/2017.
 */
public abstract class NewRLPElement {

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final int LONG_DATA_THRESHOLD = 56;

    protected final byte[] rlpData;
    protected final int rlpIndex;
//    protected final ElementType type;

//    private final InputStream rlpStream;
//    private final long rlpStreamIndex;

    private final LazyInitializer<Metadata> lazyMetadata;

    NewRLPElement(byte[] rlpData) {
        this(rlpData, 0);
    }

//     ElementType.type(rlpData[0])

//    NewRLPElement(byte[] rlpData, int rlpIndex) {
//        this(rlpData, rlpIndex, ElementType.type(rlpData[rlpIndex]));
//    }

    NewRLPElement(byte[] rlpData, int rlpIndex) {
        this.rlpData = rlpData;
        this.rlpIndex = rlpIndex;
        this.lazyMetadata = new LazyInitializer<Metadata>() {
            @Override
            protected Metadata initialize() throws ConcurrentException {
                return deriveMetadata();
            }
        };
//        this.type = type;
//        this.metadata = decodeMetadata();
    }

    public byte[] getRLPData() {
        return rlpData;
    }

    public int getRLPIndex() {
        return rlpIndex;
    }

    private Metadata getMetadata() {
        try {
            return lazyMetadata.get();
        } catch (ConcurrentException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getData() {
        Metadata metadata = getMetadata();
        long idx = metadata.dataIndex;
        long len = metadata.dataLength;
        if(idx + len > Integer.MAX_VALUE - 100) {
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx + len));
        }
        return Arrays.copyOfRange(rlpData, (int) idx, (int) (idx + len));
    }

    public ElementType getType() {
        return getMetadata().type;
    }

    public int getRlpLength() {
        Metadata metadata = getMetadata();
        return (metadata.dataIndex + metadata.dataLength) - rlpIndex;
    }

    public long getDataIndex() {
        return getMetadata().dataIndex;
    }

    public long getDataLength() {
        return getMetadata().dataLength;
    }

    public byte getByte(int rlpIndex) {
        return rlpData[rlpIndex];
    }

//    private static Metadata decodeMetadata(InputStream rlpStream) throws IOException {
//        byte[] firstNine = new byte[9];
//        int read = rlpStream.read(firstNine);
//        if(read < firstNine.length && read != -1) {
//            throw new IOException("error reading stream");
//        }
//        byte byteZero = firstNine[0];
//        return decodeMetadata(firstNine, 0);
//    }

    public static NewRLPItem decodeItem(byte[] rlpData) {
        return (NewRLPItem) decode(rlpData, 0);
    }

    public static NewRLPList decodeList(byte[] rlpData) {
        return (NewRLPList) decode(rlpData, 0);
    }

    public static NewRLPList decodeList(byte[] rlpData, int rlpIndex) {
        return (NewRLPList) decode(rlpData, rlpIndex);
    }

    public static NewRLPElement decode(byte[] rlpData) {
        return decode(rlpData, 0);
    }

    public static NewRLPElement decode(byte[] rlpData, int rlpIndex) {

        ElementType type = ElementType.type(rlpData[rlpIndex]);
        switch (type) {
        case SINGLE_BYTE:
        case ITEM_SHORT:
        case ITEM_LONG:
            return new NewRLPItem(rlpData, rlpIndex);
        case LIST_SHORT:
        case LIST_LONG:
            return new NewRLPList(rlpData, rlpIndex).build();
        default:
            throw new RuntimeException("???");
        }
    }

    public static NewRLPItem encodeItem(String string, Charset charset) {
        return encodeItem(string.getBytes(charset));
    }

//    public static NewRLPItem encodeItem(byte[] data) {
//        return (NewRLPItem) _encode(false, data);
//
////        final ElementType type;
////        if(data.length > 55) {
////            type = ElementType.ITEM_LONG;
////            byte[] lengthBytes = ByteUtil.intToBytesNoLeadZeroes(data.length);
////
////            byte[] rlpData = new byte[1 + lengthBytes.length + data.length];
////            rlpData[0] = (byte) (type.getOffset() + lengthBytes.length);
////            System.arraycopy(lengthBytes, 0, rlpData, 1, lengthBytes.length);
////            System.arraycopy(data, 0, rlpData, 1 + lengthBytes.length, data.length);
////
////            return new NewRLPItem(rlpData, 0, type);
////        }
////
////        type = ElementType.ITEM_SHORT;
////
////        byte[] rlpData = new byte[1 + data.length];
////        rlpData[0] = (byte) (type.getOffset() + data.length);
////        System.arraycopy(data, 0, rlpData, 1, data.length);
////
////        return new NewRLPItem(rlpData, 0, type);
//    }

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
            rlpData[0] = (byte) (type.getOffset() + dataLen);
            System.arraycopy(data, 0, rlpData, 1, dataLen);

        } else {

            type = ElementType.ITEM_LONG;

//                final int lengthLen = ByteUtil.byteLengthNoLeadZeroes(dataLen);

//                final byte[] lengthBytes = ByteUtil.intsToBytes()

//            byte[] lengthBytes = ByteUtil.intToBytesNoLeadZeroes(dataLen);
//            final int lengthLen = lengthBytes.length;

            final byte[] dataLenBytes = lengthToBytes(dataLen);
            final int numLengthBytes = dataLenBytes.length;

            rlpData = new byte[1 + numLengthBytes + dataLen];
            rlpData[0] = (byte) (type.getOffset() + numLengthBytes);
            System.arraycopy(dataLenBytes, 0, rlpData, 1, numLengthBytes);
            System.arraycopy(data, 0, rlpData, 1 + numLengthBytes, dataLen);

//            System.arraycopy(lengthBytes, 0, rlpData, 1, lengthLen);
//                ByteUtil.insertInt(rlpData, dataLen, lengthLen);
//                System.arraycopy(data, 0, rlpData, 1 + lengthLen, dataLen);
        }

        return new NewRLPItem(rlpData, 0);
    }

    // TODO test size one array encoded as single item and as short list

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
            rlpData = new byte[] { (byte) type.getOffset() };
        } else {

            int dataLen = 0;
            for (NewRLPElement element : elements) {
                dataLen += element.rlpData.length;
            }

            int destPos;

            if (dataLen < LONG_DATA_THRESHOLD) {

                type = ElementType.LIST_SHORT;

                rlpData = new byte[1 + dataLen];
                rlpData[0] = (byte) (type.getOffset() + dataLen);
                destPos = 1;

//                System.arraycopy(data, 0, rlpData, 1, dataLen);
            } else {

                type = ElementType.LIST_LONG;

//                final int lengthLen = ByteUtil.byteLengthNoLeadZeroes(dataLen);

//                final byte[] lengthBytes = ByteUtil.intsToBytes()

//            byte[] lengthBytes = ByteUtil.intToBytesNoLeadZeroes(dataLen);
//            final int lengthLen = lengthBytes.length;

                final byte[] dataLenBytes = lengthToBytes(dataLen);
                final int numLengthBytes = dataLenBytes.length;

                rlpData = new byte[1 + numLengthBytes + dataLen];
                rlpData[0] = (byte) (type.getOffset() + numLengthBytes);
                System.arraycopy(dataLenBytes, 0, rlpData, 1, numLengthBytes);


//            System.arraycopy(lengthBytes, 0, rlpData, 1, lengthLen);
//                ByteUtil.insertInt(rlpData, dataLen, lengthLen);

                destPos = 1 + numLengthBytes;

//                System.arraycopy(data, 0, rlpData, 1 + lengthLen, dataLen);
            }

            for (final NewRLPElement element : elements) {
                System.arraycopy(element.rlpData, 0, rlpData, destPos, element.rlpData.length);
                destPos += element.rlpData.length;
            }
        }

        return new NewRLPList(rlpData, 0);
    }

    private static byte[] lengthToBytes(int length) {

        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }

        byte a = 0, b = 0, c = 0, d;

        int n = 1;
        d = (byte) (length & 0xFF);
        length = length >>> Byte.SIZE;
        if(length != 0) {
            n = 2;
            c = (byte) (length & 0xFF);
            length = length >>> Byte.SIZE;
            if(length != 0) {
                n = 3;
                b = (byte) (length & 0xFF);
                length = length >>> Byte.SIZE;
                if(length != 0) {
                    n = 4;
                    a = (byte) (length & 0xFF);
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

    protected void recursivePrint(StringBuilder sb) {

        if (this instanceof NewRLPList) {

            NewRLPList rlpList = (NewRLPList) this;
            sb.append("[");
            final int size = rlpList.size();
            for (int i = 0; i < size; i++) {
                rlpList.get(i).recursivePrint(sb);
            }
            if (size > 0 && sb.charAt(sb.length() - 1) == '|') {
                sb.replace(sb.length() - 1, sb.length(), "");
            }
            sb.append("]");
        } else {
            sb.append(toString()).append("|");
        }
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof NewRLPElement)) {
            return false;
        }

        NewRLPElement other = (NewRLPElement) obj;

        final int end = rlpIndex + getRlpLength();
        for (int i = rlpIndex, _i = other.rlpIndex; i < end; i++, _i++) {
            if(rlpData[i] != other.rlpData[_i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see Arrays#hashCode(byte[])
     * @return
     */
    @Override
    public int hashCode() {

        int result = 1;
        final int len = getRlpLength();
        for (int i = rlpIndex; i < len; i++)
            result = 31 * result + rlpData[i];

        return result;
    }

    @Override
    public String toString() {
        return TestUtils.toChars(getData()); // TODO decouple from TestUtils
    }

    private Metadata deriveMetadata() {

        final ElementType type = ElementType.type(rlpData[rlpIndex]);

        if(type == ElementType.SINGLE_BYTE) {
            return new Metadata(type, rlpIndex, 1);
        }

        final int byteZero = getByte(rlpIndex) & 0xFF;

        if(type.isShort()) {
            return new Metadata(type, rlpIndex + 1, byteZero - type.getOffset());
        }

        int lengthOfLength = byteZero - type.getOffset();
        int length = 0;
        int shiftAmount = 0;
        switch (lengthOfLength) {
        case 8:
            length += (long) rlpData[rlpIndex + 8];
            shiftAmount += Byte.SIZE;
        case 7:
            length += (long) rlpData[rlpIndex + 7] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 6:
            length += (long) rlpData[rlpIndex + 6] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 5:
            length += (long) rlpData[rlpIndex + 5] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 4:
            length += (long) rlpData[rlpIndex + 4] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 3:
            length += (long) rlpData[rlpIndex + 3] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 2:
            length += (long) rlpData[rlpIndex + 2] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 1:
            length += (long) rlpData[rlpIndex + 1] << shiftAmount;
        }
        return new Metadata(type, rlpIndex + 1 + lengthOfLength, length);
    }

    private static final class Metadata {
        private final ElementType type;
        private final int dataIndex;
        private final int dataLength;

        private Metadata(ElementType type, int dataIndex, int dataLength) {
            this.type = type;
            this.dataIndex = dataIndex;
            this.dataLength = dataLength;
        }
    }
}

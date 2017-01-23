package org.ethereum.util;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Evo on 1/19/2017.
 */
public abstract class NewRLPElement {

    protected final byte[] rlpData;
    protected final int rlpIndex;
    protected final ElementType type;

//    private final InputStream rlpStream;
//    private final long rlpStreamIndex;

    private final Metadata metadata;

    NewRLPElement(byte[] rlpData) {
        this(rlpData, 0, ElementType.type(rlpData[0]));
    }

    NewRLPElement(byte[] rlpData, int rlpIndex) {
        this(rlpData, rlpIndex, ElementType.type(rlpData[rlpIndex]));
    }

    NewRLPElement(byte[] rlpData, int rlpIndex, ElementType type) {
        this.rlpData = rlpData;
        this.rlpIndex = rlpIndex;
        this.type = type;
        this.metadata = decodeMetadata();
    }

    public byte[] getRLPData() {
        return rlpData;
    }

    public int getRLPIndex() {
        return rlpIndex;
    }

    public ElementType getType() {
        return type;
    }

    public int getRlpLength() {
        return (metadata.dataIndex + metadata.dataLength) - rlpIndex;
    }

    public long getDataIndex() {
        return metadata.dataIndex;
    }

    public long getDataLength() {
        return metadata.dataLength;
    }

    public byte getByte(int rlpIndex) {
        return rlpData[rlpIndex];
    }

    public byte[] getData() {
        long idx = metadata.dataIndex;
        long len = metadata.dataLength;
        if(idx + len > Integer.MAX_VALUE - 100) {
            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx + len));
        }
        return Arrays.copyOfRange(rlpData, (int) idx, (int) (idx + len));
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

    private Metadata decodeMetadata() {

        if(type == ElementType.SINGLE_BYTE) {
            return new Metadata(rlpIndex, 1);
        }

        final int byteZero = getByte(rlpIndex) & 0xFF;

        if(type.isShort()) {
            return new Metadata(rlpIndex + 1, byteZero - type.getOffset());
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
        return new Metadata(rlpIndex + 1 + lengthOfLength, length);
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
            return new NewRLPItem(rlpData, rlpIndex, type);
        case LIST_SHORT:
        case LIST_LONG:
            return new NewRLPList(rlpData, rlpIndex, type).build();
        default:
            throw new RuntimeException("???");
        }
    }

    public static NewRLPElement encodeString(String string, Charset charset) {
        return encodeString(string.getBytes(charset));
    }

    public static NewRLPElement encodeString(byte[] data) {

        final ElementType type;
        if(data.length > 55) {
            type = ElementType.ITEM_LONG;
            byte[] lengthBytes = ByteUtil.intToBytesNoLeadZeroes(data.length);

            byte[] rlpData = new byte[1 + lengthBytes.length + data.length];
            rlpData[0] = (byte) (type.getOffset() + lengthBytes.length);
            System.arraycopy(lengthBytes, 0, rlpData, 1, lengthBytes.length);
            System.arraycopy(data, 0, rlpData, 1 + lengthBytes.length, data.length);

            return new NewRLPItem(rlpData, 0, type);
        }

        type = ElementType.ITEM_SHORT;

        byte[] rlpData = new byte[1 + data.length];
        rlpData[0] = (byte) (type.getOffset() + data.length);
        System.arraycopy(data, 0, rlpData, 1, data.length);

        return new NewRLPItem(rlpData, 0, type);
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

        if(!(obj instanceof NewRLPItem)) {
            return false;
        }

        NewRLPItem other = (NewRLPItem) obj;

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

    private static final class Metadata {
        private final int dataIndex;
        private final int dataLength;

        private Metadata(int dataIndex, int dataLength) {
            this.dataIndex = dataIndex;
            this.dataLength = dataLength;
        }
    }

    @Override
    public String toString() {
        return TestUtils.toChars(getData()); // TODO decouple from TestUtils
    }
}

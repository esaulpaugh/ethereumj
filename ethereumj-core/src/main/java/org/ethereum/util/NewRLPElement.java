package org.ethereum.util;

import java.util.Arrays;

/**
 * Created by Evo on 1/19/2017.
 */
public abstract class NewRLPElement {

//    private static final Metadata SINGLE_BYTE_METADATA = new Metadata(0, 1);

    private final byte[] rlpData;
    private final int rlpIndex;
    private final ElementType type;

//    private final InputStream rlpStream;
//    private final long rlpStreamIndex;


//    private final byte[] data;
//    private final int index;

    private final Metadata metadata;


//    private final ElementType type;
//    private final long length;

    NewRLPElement(byte[] rlpData, int rlpIndex) {
        this(rlpData, rlpIndex, ElementType.type(rlpData[rlpIndex]));
    }

    NewRLPElement(byte[] rlpData, int rlpIndex, ElementType type) {
        this.rlpData = rlpData;
        this.rlpIndex = rlpIndex;
        this.type = type;
        this.metadata = decodeMetadata();
    }

//    NewRLPElement(InputStream rlpStream) throws IOException { // TODO
//        this.rlpData = null;
//        this.rlpStream = rlpStream;
//
//        this.metadata = decodeMetadata(rlpStream);
//
////        this.index = 0;
////        stream.mark(9);
////        final byte byteZero = (byte) stream.read();
////        stream.reset();
////        this.type = ElementType.type(byteZero);
////        this.length = decodeLength(type, byteZero);
//    }

//    /**
//     * Returns a copy.
//     * @return
//     */
    public byte[] getRLPData() {
        return rlpData;
//        return rlpData != null ? rlpData.clone() : null;
    }

    @Override
    public String toString() {
        return TestUtils.bytesToString(getData());
    }

    public void recursivePrint(StringBuilder sb) {

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

    public long getRLPIndex() {
        return rlpIndex;
    }

    public ElementType getType() {
        return type;
    }

    public long rlpLength() {
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

//    public InputStream getStream() {
//        return rlpStream;
//    }

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
                length += (long) rlpData[rlpIndex + 8] << shiftAmount;
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

    private static final class Metadata {
//        private final long rlpIndex;
//        private final ElementType type;
        private final long dataIndex;
        private final long dataLength;

        private Metadata(long dataIndex, long dataLength) {
//            this.rlpIndex = rlpIndex;
//            this.type = type;
            this.dataIndex = dataIndex;
            this.dataLength = dataLength;
        }
    }
}

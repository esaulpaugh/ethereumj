package org.ethereum.util;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

import java.util.Arrays;

/**
 * Created by Evo on 1/19/2017.
 */
public abstract class NewRLPElement {

    protected final byte[] rlpData;
    protected final int rlpIndex;

    private transient final LazyInitializer<Metadata> lazyMetadata;

    NewRLPElement(byte[] rlpData) {
        this(rlpData, 0);
    }

    NewRLPElement(byte[] rlpData, int rlpIndex) {
        this.rlpData = rlpData;
        this.rlpIndex = rlpIndex;
        this.lazyMetadata = new LazyInitializer<Metadata>() {
            @Override
            protected Metadata initialize() {
                System.out.println("*** initialize() metadata ***");
                return deriveMetadata();
            }
        };
    }

    protected abstract void recursivePrint(StringBuilder sb);

    public byte[] getRLPData() {
        return rlpData;
    }

    public int getRLPIndex() {
        return rlpIndex;
    }

    protected Metadata getMetadata() {
        try {
            return lazyMetadata.get();
        } catch (ConcurrentException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getData() {
        Metadata metadata = getMetadata();
//        if(idx + len > Integer.MAX_VALUE - 100) {
//            throw new ArrayIndexOutOfBoundsException(String.valueOf(idx + len));
//        }
        return Arrays.copyOfRange(rlpData, metadata.dataIndex, metadata.dataIndex + metadata.dataLength);
    }

    public ElementType getType() {
        return getMetadata().type;
    }

    public int getRlpLength() {
        Metadata metadata = getMetadata();
        return (metadata.dataIndex + metadata.dataLength) - rlpIndex;
    }

    public int getDataIndex() {
        return getMetadata().dataIndex;
    }

    public int getDataLength() {
        return getMetadata().dataLength;
    }

    public byte getByte(int rlpIndex) {
        return rlpData[rlpIndex];
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof NewRLPElement)) {
            return false;
        }

        NewRLPElement other = (NewRLPElement) obj;

        final int end = rlpIndex + getRlpLength();
        for (int i = rlpIndex, j = other.rlpIndex; i < end; i++, j++) {
            if(rlpData[i] != other.rlpData[j]) {
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
        for (int i = rlpIndex; i < len; i++) {
            result = 31 * result + rlpData[i];
        }
        return result;
    }

    @Override
    public String toString() {
        return TestUtils.toChars(getData()); // TODO decouple from TestUtils
    }

    private Metadata deriveMetadata() {

        final byte leadByte = rlpData[rlpIndex];
        final ElementType type = ElementType.type(leadByte);

        if(type == ElementType.SINGLE_BYTE) {
            return new Metadata(type, rlpIndex, 1);
        }

        if(type.isShort()) {
            return new Metadata(type, rlpIndex + 1, leadByte - type.offset);
        }

        int lengthOfLength = leadByte - type.offset;
        int length = 0;
        int i = rlpIndex;
        switch (lengthOfLength) {
//        case 8: length += (long) (rlpData[++i] & 0xFF) << (Byte.SIZE * 7);
//        case 7: length += (long) (rlpData[++i] & 0xFF) << (Byte.SIZE * 6);
//        case 6: length += (long) (rlpData[++i] & 0xFF) << (Byte.SIZE * 5);
//        case 5: length += (long) (rlpData[++i] & 0xFF) << (Byte.SIZE * 4);
        case 4: length += (rlpData[++i] & 0xFF) << (Byte.SIZE * 3);
        case 3: length += (rlpData[++i] & 0xFF) << (Byte.SIZE * 2);
        case 2: length += (rlpData[++i] & 0xFF) << Byte.SIZE;
        case 1: length += (rlpData[++i] & 0xFF);
        default: return new Metadata(type, rlpIndex + 1 + lengthOfLength, length);
        }
    }

    protected static final class Metadata {
        protected final ElementType type;
        protected final int dataIndex;
        protected final int dataLength;

        private Metadata(ElementType type, int dataIndex, int dataLength) {
            this.type = type;
            this.dataIndex = dataIndex;
            this.dataLength = dataLength;
        }
    }
}

package org.ethereum.util.rlp;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

import java.util.Arrays;

/**
 * Created by Evo on 1/19/2017.
 */
abstract class NewRLPElement {

    protected final byte[] buffer;
    protected final int index;

    private transient final LazyInitializer<Metadata> lazyMetadata;

    NewRLPElement(byte[] buffer, int index) {
        this.buffer = buffer;
        this.index = index;
        this.lazyMetadata = new LazyInitializer<Metadata>() {
            @Override
            protected Metadata initialize() {
                return deriveMetadata();
            }
        };
    }

    protected abstract void recursivePrint(boolean hex, StringBuilder sb);

    public int getIndex() {
        return index;
    }

    Metadata getMetadata() {
        try {
            return lazyMetadata.get();
        } catch (ConcurrentException e) {
            throw new RuntimeException(e);
        }
    }

    public ElementType getType() {
        return getMetadata().type;
    }

    public int encodingLength() {
        return getEndIndex() - index;
    }

    public int getEndIndex() {
        Metadata metadata = getMetadata();
        return metadata.dataIndex + metadata.dataLength;
    }

    public byte[] encoding() {
        return Arrays.copyOfRange(buffer, index, getEndIndex());
    }

    public int exportEncoding(byte[] dest, int destIndex) {
        int encodingLen = encodingLength();
        System.arraycopy(buffer, index, dest, destIndex, encodingLen);
        return destIndex + encodingLen;
    }

    public int getDataIndex() {
        return getMetadata().dataIndex;
    }

    public int getDataLength() {
        return getMetadata().dataLength;
    }

    public byte[] data() {
        Metadata metadata = getMetadata();
        return Arrays.copyOfRange(buffer, metadata.dataIndex, metadata.dataIndex + metadata.dataLength);
    }

    public int exportData(byte[] dest, int destIndex) {
        Metadata metadata = getMetadata();
        System.arraycopy(buffer, metadata.dataIndex, dest, destIndex, metadata.dataLength);
        return destIndex + metadata.dataLength;
    }

    private Metadata deriveMetadata() {

//        System.out.println("deriveMetadata() " + System.nanoTime());

        final byte leadByte = buffer[index];
        final ElementType type = ElementType.type(leadByte);

        if(type == ElementType.ITEM_SINGLE_BYTE) {
            return new Metadata(type, index, 1);
        }

        if(type.isShort()) {
            return new Metadata(type, index + 1, leadByte - type.offset);
        }
// TODO test edge cases
        int lengthOfLength = leadByte - type.offset;

        long length = NewRLP.decodeLong(buffer, index + 1, lengthOfLength);

        return new Metadata(type, index + 1 + lengthOfLength, (int) length);
    }

    static final class Metadata {
        final ElementType type;
        final int dataIndex;
        final int dataLength;
        int size = -1;
//        protected final List<NewRLPElement> elements;

        private Metadata(ElementType type, int dataIndex, int dataLength) {
            this.type = type;
            this.dataIndex = dataIndex;
            this.dataLength = dataLength;
        }
    }

    public String toUtf8() {
        StringBuilder sb = new StringBuilder();
        recursivePrint(false, sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        recursivePrint(true, sb);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NewRLPElement)) {
            return false;
        }

        NewRLPElement other = (NewRLPElement) obj;

        final int encLen = this.encodingLength();

        if(encLen != other.encodingLength()) {
            return false;
        }

        final int end = this.index + encLen;
        for (int i = this.index, j = other.index; i < end; i++, j++) {
            if(this.buffer[i] != other.buffer[j]) {
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
        final int end = getEndIndex();
        for (int i = index; i < end; i++) {
            result = 31 * result + buffer[i];
        }
        return result;
    }
}

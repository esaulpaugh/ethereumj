package org.ethereum.util.rlp;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.ethereum.util.ElementType;

import java.util.Arrays;

/**
 * Created by Evo on 1/19/2017.
 */
public abstract class NewRLPElement {

    protected final byte[] buffer;
    protected final int index;

    private transient final LazyInitializer<Metadata> lazyMetadata;

    NewRLPElement(byte[] buffer) {
        this(buffer, 0);
    }

    NewRLPElement(byte[] buffer, int index) {
        this.buffer = buffer;
        this.index = index;
        this.lazyMetadata = new LazyInitializer<Metadata>() {
            @Override
            protected Metadata initialize() {
                System.out.println("*** initialize() metadata ***");
                return deriveMetadata();
            }
        };
    }

    protected abstract void recursivePrint(StringBuilder sb);

    public byte[] getBuffer() {
        return buffer;
    }

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

    public byte[] getEncoding() {
        return Arrays.copyOfRange(buffer, index, getEndIndex());
    }

    public int getDataIndex() {
        return getMetadata().dataIndex;
    }

    public int getDataLength() {
        return getMetadata().dataLength;
    }

    public byte[] getData() {
        Metadata metadata = getMetadata();
        return Arrays.copyOfRange(buffer, metadata.dataIndex, metadata.dataIndex + metadata.dataLength);
    }

    private Metadata deriveMetadata() {

        final byte leadByte = buffer[index];
        final ElementType type = ElementType.type(leadByte);

        if(type == ElementType.SINGLE_BYTE) {
            return new Metadata(type, index, 1);
        }

        if(type.isShort()) {
            return new Metadata(type, index + 1, leadByte - type.offset);
        }

        int lengthOfLength = leadByte - type.offset;

        long length = NewRLP.bytesToLong(buffer, index + 1, lengthOfLength);

        return new Metadata(type, index + 1 + lengthOfLength, (int) length);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        recursivePrint(sb);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NewRLPElement)) {
            return false;
        }

        NewRLPElement other = (NewRLPElement) obj;

        if(this.encodingLength() != other.encodingLength()) {
            return false;
        }

        final int end = this.index + this.encodingLength();
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

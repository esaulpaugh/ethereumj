package org.ethereum.util;

/**
 * Created by Evo on 1/19/2017.
 */
public abstract class NewRLPElement {

    final byte[] data;
    final int index;
    boolean isShort;
    long length;

    NewRLPElement(byte[] data, int index) {
        this.data = data;
        this.index = index;
    }

    NewRLPElement(byte[] data, int index, boolean isShort, long length) {
        this(data, index);
        this.isShort = isShort;
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public int getIndex() {
        return index;
    }

    public boolean isShort() {
        return isShort;
    }

    public long getLength() {
        return length;
    }
}

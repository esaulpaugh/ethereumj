package org.ethereum.util;

/**
 * Created by Evo on 1/19/2017.
 */
public abstract class NewRLPElement {

    final byte[] data;
    final int index;
    final ElementType type;
    long length;

    NewRLPElement(byte[] data, int index) {
        this.data = data;
        this.index = index;
        final byte byteZero = data[index];
        this.type = ElementType.type(byteZero);
        this.length = decodeLength(type, byteZero);
    }

    NewRLPElement(byte[] data, int index, long length) {
        this(data, index);
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public int getIndex() {
        return index;
    }

    public long getLength() {
        return length;
    }

    private long decodeLength(ElementType type, byte byteZero) {

        if(type == ElementType.SINGLE_BYTE) {
            return 0;
        }

        if(type.isShort()) {
            return byteZero - type.getOffset();
        }

        int lengthOfLength = byteZero - type.getOffset();
        int length = 0;
        int shiftAmount = 0;
        switch (lengthOfLength) {
            case 8:
                length += (long) data[index + 8] << shiftAmount;
                shiftAmount += Byte.SIZE;
            case 7:
                length += (long) data[index + 7] << shiftAmount;
                shiftAmount += Byte.SIZE;
            case 6:
                length += (long) data[index + 6] << shiftAmount;
                shiftAmount += Byte.SIZE;
            case 5:
                length += (long) data[index + 5] << shiftAmount;
                shiftAmount += Byte.SIZE;
            case 4:
                length += (long) data[index + 4] << shiftAmount;
                shiftAmount += Byte.SIZE;
            case 3:
                length += (long) data[index + 3] << shiftAmount;
                shiftAmount += Byte.SIZE;
            case 2:
                length += (long) data[index + 2] << shiftAmount;
                shiftAmount += Byte.SIZE;
            case 1:
                length += (long) data[index + 1] << shiftAmount;
        }
        return length;
    }
}

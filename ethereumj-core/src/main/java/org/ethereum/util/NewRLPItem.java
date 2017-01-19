package org.ethereum.util;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPItem extends NewRLPElement {

    NewRLPItem(byte[] data, int index) {
        super(data, index);

        byte byteZero = data[index];

        if (byteZero > RLP.OFFSET_LONG_ITEM && byteZero < RLP.OFFSET_SHORT_LIST) {
            this.isShort = false;
        } else if (byteZero > RLP.OFFSET_SHORT_ITEM && byteZero <= RLP.OFFSET_LONG_ITEM) {
            this.isShort = true;
        } else {
            throw new RuntimeException("error decoding item offset");
        }
        this.length = decodeLength(byteZero);
    }

    @Override
    public byte[] getData() {
        if (data.length == 0)
            return null;
        return data;
    }

    private long decodeLength(byte byteZero) {
        if(isShort) {
            return byteZero - RLP.OFFSET_SHORT_ITEM;
        }

        int length = 0;
        int shiftAmount = 0;
        switch ((byte) (byteZero - RLP.OFFSET_LONG_ITEM)) {
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

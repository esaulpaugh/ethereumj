package org.ethereum.util;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPItem implements RLPElement {

    final byte[] rlpData;
    final int index;
    final boolean isShortItem;
    final long length;

    public NewRLPItem(byte[] rlpData, int index) {
        this.rlpData = rlpData;
        this.index = index;

        byte byteZero = rlpData[index];

        if (byteZero > RLP.OFFSET_LONG_ITEM && byteZero < RLP.OFFSET_SHORT_LIST) {
            this.isShortItem = false;
        } else if (byteZero > RLP.OFFSET_SHORT_ITEM && byteZero <= RLP.OFFSET_LONG_ITEM) {
            this.isShortItem = true;
        } else {
            throw new RuntimeException("error decoding item offset");
        }
        this.length = decodeLength(byteZero);
    }

    public byte[] getRLPData() {
        if (rlpData.length == 0)
            return null;
        return rlpData;
    }

    public int getIndex() {
        return index;
    }

    public boolean isShortItem() {
        return isShortItem;
    }

    public boolean isLongItem() {
        return !isShortItem;
    }

    public long getLength() {
        return length;
    }

    private long decodeLength(byte byteZero) {
        if(isShortItem) {
            return byteZero - RLP.OFFSET_SHORT_ITEM;
        }

        int length = 0;
        int shiftAmount = 0;
        switch ((byte) (byteZero - RLP.OFFSET_LONG_ITEM)) {
        case 8:
            length += (long) rlpData[index + 8] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 7:
            length += (long) rlpData[index + 7] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 6:
            length += (long) rlpData[index + 6] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 5:
            length += (long) rlpData[index + 5] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 4:
            length += (long) rlpData[index + 4] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 3:
            length += (long) rlpData[index + 3] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 2:
            length += (long) rlpData[index + 2] << shiftAmount;
            shiftAmount += Byte.SIZE;
        case 1:
            length += (long) rlpData[index + 1] << shiftAmount;
        }
        return length;
    }
}

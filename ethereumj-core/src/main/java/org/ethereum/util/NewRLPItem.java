package org.ethereum.util;

import java.util.Arrays;
import java.util.Queue;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPItem extends NewRLPElement {

    NewRLPItem(byte[] rlpData) {
        super(rlpData);
    }

    NewRLPItem(byte[] rlpData, int rlpIndex) {
        super(rlpData, rlpIndex);
    }

    NewRLPItem(byte[] rlpData, int rlpIndex, ElementType type) {
        super(rlpData, rlpIndex, type);
    }

    @Override
    public byte[] getRLPData() {
        byte[] rlpData = super.getRLPData();
        if (rlpData == null || rlpData.length == 0)
            return null;
        return rlpData;
    }



//    public void decode() {
//
//        switch (getType()) {
//            case SINGLE_BYTE:
//                break;
//            case ITEM_SHORT:
//            case ITEM_LONG:
//                default:
//        }
//
//        // It's an item with a payload more than 55 bytes
//        // data[0] - 0xB7 = how much next bytes allocated for
//        // the length of the string
//        if ((msgData[pos] & 0xFF) > OFFSET_LONG_ITEM
//                && (msgData[pos] & 0xFF) < OFFSET_SHORT_LIST) {
//
//            byte lengthOfLength = (byte) (msgData[pos] - OFFSET_LONG_ITEM);
//            int length = calcLength(lengthOfLength, msgData, pos);
//
//            // now we can parse an item for data[1]..data[length]
//            byte[] item = new byte[length];
//            System.arraycopy(msgData, pos + lengthOfLength + 1, item,
//                    0, length);
//
//            byte[] rlpPrefix = new byte[lengthOfLength + 1];
//            System.arraycopy(msgData, pos, rlpPrefix, 0,
//                    lengthOfLength + 1);
//
//            RLPItem rlpItem = new RLPItem(item);
//            rlpList.add(rlpItem);
//            pos += lengthOfLength + length + 1;
//
//            continue;
//        }
//        // It's an item less than 55 bytes long,
//        // data[0] - 0x80 == length of the item
//        if ((msgData[pos] & 0xFF) > OFFSET_SHORT_ITEM
//                && (msgData[pos] & 0xFF) <= OFFSET_LONG_ITEM) {
//
//            byte length = (byte) ((msgData[pos] & 0xFF) - OFFSET_SHORT_ITEM);
//
//            byte[] item = new byte[length];
//            System.arraycopy(msgData, pos + 1, item, 0, length);
//
//            byte[] rlpPrefix = new byte[2];
//            System.arraycopy(msgData, pos, rlpPrefix, 0, 2);
//
//            RLPItem rlpItem = new RLPItem(item);
//            rlpList.add(rlpItem);
//            pos += 1 + length;
//
//            continue;
//        }
//        // null item
//        if ((msgData[pos] & 0xFF) == OFFSET_SHORT_ITEM) {
//            byte[] item = ByteUtil.EMPTY_BYTE_ARRAY;
//            RLPItem rlpItem = new RLPItem(item);
//            rlpList.add(rlpItem);
//            pos += 1;
//            continue;
//        }
//        // single byte item
//        if ((msgData[pos] & 0xFF) < OFFSET_SHORT_ITEM) {
//
//            byte[] item = {(byte) (msgData[pos] & 0xFF)};
//
//            RLPItem rlpItem = new RLPItem(item);
//            rlpList.add(rlpItem);
//            pos += 1;
//        }
//    }
}

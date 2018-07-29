package org.ethereum.util.rlp;

import org.ethereum.util.ByteUtil;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPItem extends NewRLPElement {

    static final NewRLPElement ZERO_ITEM = new NewRLPItem(new byte[] { 0 }, 0);

    NewRLPItem(byte[] buffer) {
        this(buffer, 0);
    }

    NewRLPItem(byte[] buffer, int index) {
        super(buffer, index);
    }

    @Override
    protected void recursivePrint(StringBuilder sb) {
        sb.append(ByteUtil.toHexString(getData())).append(", ");
    }

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        recursivePrint(sb);
//        return sb.toString();
//    }

//    @Override
//    public boolean equals(Object obj) {
//        if(!(obj instanceof NewRLPItem)) {
//            return false;
//        }
//
//        NewRLPItem other = (NewRLPItem) obj;
//
//        if(this.encodingLength() != other.encodingLength()) {
//            return false;
//        }
//
//        final int end = this.index + this.encodingLength();
//        for (int i = this.index, j = other.index; i < end; i++, j++) {
//            if(this.buffer[i] != other.buffer[j]) {
//                return false;
//            }
//        }
//
//        return true;
//    }

//    @Override
//    public byte[] getRLPData() {
//        byte[] rlpData = super.getRLPData();
//        if (rlpData == null || rlpData.length == 0)
//            return null;
//        return rlpData;
//    }
}

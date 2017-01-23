package org.ethereum.util;

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
}

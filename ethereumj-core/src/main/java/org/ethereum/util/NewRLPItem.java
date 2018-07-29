package org.ethereum.util;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPItem extends NewRLPElement {

    static final NewRLPElement EMPTY_ITEM = new NewRLPItem(new byte[] { 0 }, 0);

    NewRLPItem(byte[] rlpData) {
        this(rlpData, 0);
    }

    NewRLPItem(byte[] rlpData, int rlpIndex) {
        super(rlpData, rlpIndex);
    }

    @Override
    protected void recursivePrint(StringBuilder sb) {
        sb.append(ByteUtil.toHexString(getData())).append(", ");
    }

    @Override
    public byte[] getRLPData() {
        byte[] rlpData = super.getRLPData();
        if (rlpData == null || rlpData.length == 0)
            return null;
        return rlpData;
    }
}

package org.ethereum.util.rlp;

import org.ethereum.util.ByteUtil;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPItem extends NewRLPElement {

    NewRLPItem(byte singleByte) {
        this(new byte[] { singleByte });
    }

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
}

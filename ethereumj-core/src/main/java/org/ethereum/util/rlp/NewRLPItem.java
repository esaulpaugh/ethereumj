package org.ethereum.util.rlp;

import org.ethereum.util.ByteUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPItem extends NewRLPElement {

    private NewRLPItem(byte[] buffer, int index) {
        super(buffer, index);
    }

    static NewRLPItem fromSingleByte(byte singleByte) {
        return new NewRLPItem(new byte[] { singleByte }, 0);
    }

    static NewRLPItem fromEncoding(byte[] buffer) {
        return fromEncoding(buffer, 0);
    }

    static NewRLPItem fromEncoding(byte[] buffer, int index) {
        return new NewRLPItem(buffer, index);
    }

    @Override
    protected void recursivePrint(boolean hex, StringBuilder sb) {
        sb.append(hex ? ByteUtil.toHexString(data()) : new String(data(), UTF_8)).append(", ");
    }
}

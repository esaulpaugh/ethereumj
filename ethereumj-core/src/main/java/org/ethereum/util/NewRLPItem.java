package org.ethereum.util;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPItem extends NewRLPElement {

    NewRLPItem(byte[] data, int index) {
        super(data, index);
    }

    @Override
    public byte[] getData() {
        if (data.length == 0)
            return null;
        return data;
    }
}

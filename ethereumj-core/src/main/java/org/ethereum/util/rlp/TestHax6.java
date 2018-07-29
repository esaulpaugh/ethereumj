package org.ethereum.util.rlp;

import org.ethereum.util.ElementType;

import java.util.Arrays;

public class TestHax6 {

    public static void main(String[] args0) {

        byte[] middle = new byte[] { 0x00, 0x00, (byte) 0x83, 'w', 'e', 'w', (byte) 0x81, 0x19 };

        NewRLPElement e = OORLP.decode(middle, 2);

        byte[] enc = e.getEncoding();

        System.out.println(Arrays.toString(enc));

        System.out.println(Arrays.toString(e.getData()));

        System.out.println(e.encodingLength());

        System.out.println(e.getType() == ElementType.ITEM_SHORT);
    }

}

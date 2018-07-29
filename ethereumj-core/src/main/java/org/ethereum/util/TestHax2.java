package org.ethereum.util;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TestHax2 {

    private static final String longString = "012345678901234567890123456789012345678901234567890123456789";

//    private static final byte[] singleByte = new byte[]{'z'};
//    private static final byte[] shortString = new byte[]{(byte) 0x83, 'w', 'e', 'w'};
//    private static final byte[] longString = new byte[]{(byte) 0xb8, (byte) 0x38, 'L', 'o', 'r', 'e', 'm', ' ', 'i', 'p', 's', 'u', 'm', ' ', 'd', 'o', 'l', 'o', 'r', ' ', 's', 'i', 't', ' ', 'a', 'm', 'e', 't', ',', ' ', 'c', 'o', 'n', 's', 'e', 'c', 't', 'e', 't', 'u', 'r', ' ', 'a', 'd', 'i', 'p', 'i', 's', 'i', 'c', 'i', 'n', 'g', ' ', 'e', 'l', 'i', 't'};
//    private static final byte[] shortList0 = new byte[]{(byte) 0xca, (byte) 0x84, 'l', 'o', 'n', 'g', (byte) 0x84, 'w', 'a', 'l', 'k'};
//    private static final byte[] shortList1 = new byte[]{(byte) 0xd1, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g', (byte) 0xc8, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g'};
//    private static final byte[] longList__;
//
//    static {
//        longList__ = new byte[longString.length];
//        longList__[0] = (byte) (0xf7 + 1);
//        longList__[1] = (byte) (longString.length - 2);
//        System.arraycopy(longString, 2, longList__, 2, 56);
//    }

    public static void test(byte[] theirs, byte[] mine) {
        System.out.println(Arrays.toString(theirs));
        System.out.println(Arrays.toString(mine));

        System.out.println(Arrays.equals(theirs, mine));
    }

    public static void main(String[] args0) {

        byte[] theirs, mine;

        System.out.println(Arrays.toString(NewRLP.encodeByte((byte) 0x00)));
        System.out.println(Arrays.toString(NewRLP.encodeShort((short) 0x0000).rlpData));
        System.out.println(Arrays.toString(NewRLP.encodeInt(0x00).rlpData));
//        System.out.println(Arrays.toString(NewRLP.encodeLong(0x00).rlpData)); // TODO


        theirs = RLP.encodeByte((byte) 'z');
        mine = NewRLP.encodeItem("z", UTF_8).getRLPData();
        test(theirs, mine);

        theirs = RLP.encodeString("pam");
        mine = NewRLP.encodeItem("pam", UTF_8).getRLPData();
        test(theirs, mine);

        theirs = RLP.encodeString(longString);
        mine = NewRLP.encodeItem(longString, UTF_8).getRLPData();
        test(theirs, mine);

        theirs = RLP.encodeList(NewRLP.encodeItem("long", UTF_8).rlpData, NewRLP.encodeItem("walk", UTF_8).rlpData);
        mine = NewRLP.encodeList(NewRLP.encodeItem("long", UTF_8), NewRLP.encodeItem("walk", UTF_8)).rlpData;
        test(theirs, mine);

        theirs = RLP.encodeList(
                RLP.encodeString("cat"), RLP.encodeString("dog"),
                NewRLP.encodeList(NewRLP.encodeItem("cat", UTF_8), NewRLP.encodeItem("dog", UTF_8)).rlpData
        );
        mine = NewRLP.encodeList(
                NewRLP.encodeItem("cat", UTF_8), NewRLP.encodeItem("dog", UTF_8),
                NewRLP.encodeList(NewRLP.encodeItem("cat", UTF_8), NewRLP.encodeItem("dog", UTF_8))
        ).rlpData;
        test(theirs, mine);

        NewRLPElement[] elements = new NewRLPElement[longString.length() - 2];
        for(int i = 2, j = 0; i < longString.length(); i++) {
            elements[j++] = NewRLP.encodeItem(new byte[] { (byte) longString.charAt(i) });
        }
        byte[][] datas = new byte[elements.length][];
        for(int i = 0; i < datas.length; i++) {
            datas[i] = elements[i].rlpData;
        }

        theirs = RLP.encodeList(datas);
        mine = NewRLP.encodeList(
                elements
        ).rlpData;
        test(theirs, mine);
    }

}

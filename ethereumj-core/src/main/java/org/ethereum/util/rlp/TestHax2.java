package org.ethereum.util.rlp;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.ethereum.util.RLP;
import org.ethereum.util.RLPList;
import org.ethereum.util.rlp.NewRLP;
import org.ethereum.util.rlp.NewRLPElement;
import org.ethereum.util.rlp.NewRLPList;
import org.ethereum.util.rlp.OORLP;

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

        byte[] x = new byte[80];
        int i = 0;
        i = NewRLP.encodeLong((byte) -0x01, x, i);
        i = NewRLP.encodeLong((short) -0x0001, x, i);
        i = NewRLP.encodeLong(-0x0001, x, i);
        i = NewRLP.encodeLong(-0x01L, x, i);
        i = NewRLP.encodeString("yipee", UTF_8, x, i);
        System.out.println(i);

        System.out.println(HexBin.encode(x));
        System.out.println(HexBin.encode(NewRLP.longToBytes(0)));


        theirs = RLP.encodeByte((byte) 'z');
        mine = OORLP.encodeItem("z", UTF_8).getEncoding();
        test(theirs, mine);
        System.out.println(OORLP.decode(mine).toString());

        theirs = RLP.encodeString("pam");
        mine = OORLP.encodeItem("pam", UTF_8).getEncoding();
        test(theirs, mine);

        theirs = RLP.encodeString(longString);
        mine = OORLP.encodeItem(longString, UTF_8).getEncoding();
        test(theirs, mine);

        theirs = RLP.encodeList(OORLP.encodeItem("long", UTF_8).buffer, OORLP.encodeItem("walk", UTF_8).buffer);
        mine = OORLP.encodeList(OORLP.encodeItem("long", UTF_8), OORLP.encodeItem("walk", UTF_8)).buffer;
        test(theirs, mine);

        theirs = RLP.encodeList(
                RLP.encodeString("cat"), RLP.encodeString("dog"),
                OORLP.encodeList(OORLP.encodeItem("cat", UTF_8), OORLP.encodeItem("dog", UTF_8)).buffer
        );
        mine = OORLP.encodeList(
                OORLP.encodeItem("cat", UTF_8), OORLP.encodeItem("dog", UTF_8),
                OORLP.encodeList(OORLP.encodeItem("cat", UTF_8), OORLP.encodeItem("dog", UTF_8))
        ).buffer;
        test(theirs, mine);

        NewRLPElement[] elements = new NewRLPElement[longString.length() - 2];
        for(int j = 2, k = 0; j < longString.length(); j++) {
            elements[k++] = OORLP.encodeItem(new byte[] { (byte) longString.charAt(j) });
        }
        byte[][] datas = new byte[elements.length][];
        for(int j = 0; j < datas.length; j++) {
            datas[j] = elements[j].buffer;
        }

        theirs = RLP.encodeList(datas);
        mine = OORLP.encodeList(
                elements
        ).buffer;
        test(theirs, mine);


//        RLPList li = new RLPList();
//        li.setRLPData(theirs);
        RLPList.recursivePrint(RLP.decode2(theirs));
        System.out.println();
        System.out.println(new NewRLPList(mine, 0, null).build().toString());
    }

}

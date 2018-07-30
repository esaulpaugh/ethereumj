package org.ethereum.util.rlp;

import org.ethereum.util.rlp.NewRLPElement;
import org.ethereum.util.rlp.NewRLPList;
import org.ethereum.util.rlp.OORLP;

public class TestHax5 {

    private static final byte[] zeroA = new byte[] {0x00};
    private static final byte[] zeroB = new byte[] {(byte) 0x80};
    private static final byte[] empty = new byte[] {  };
    private static final byte[] singleByte = new byte[]{'z'};
    private static final byte[] shortString = new byte[]{(byte) 0x83, 'w', 'e', 'w'};
    private static final byte[] longString = new byte[]{(byte) 0xb8, (byte) 0x38, 'L', 'o', 'r', 'e', 'm', ' ', 'i', 'p', 's', 'u', 'm', ' ', 'd', 'o', 'l', 'o', 'r', ' ', 's', 'i', 't', ' ', 'a', 'm', 'e', 't', ',', ' ', 'c', 'o', 'n', 's', 'e', 'c', 't', 'e', 't', 'u', 'r', ' ', 'a', 'd', 'i', 'p', 'i', 's', 'i', 'c', 'i', 'n', 'g', ' ', 'e', 'l', 'i', 't'};
    private static final byte[] shortList0 = new byte[]{(byte) 0xca, (byte) 0x84, 'l', 'o', 'n', 'g', (byte) 0x84, 'w', 'a', 'l', 'k'};
    private static final byte[] shortList1 = new byte[]{(byte) 0xd1, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g', (byte) 0xc8, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g'};
    private static final byte[] longList__;

    static {
        longList__ = new byte[longString.length];
        longList__[0] = (byte) (0xf7 + 1);
        longList__[1] = (byte) (longString.length - 2);
        System.arraycopy(longString, 2, longList__, 2, 56);
    }

    public static void main(String[] args0) {

        OORLP encoder = new OORLP();
        OORLP decoder = new OORLP();

        NewRLPElement[] elements = new NewRLPElement[longString.length - 2];
        for(int j = 2, k = 0; j < longString.length; j++) {
            elements[k++] = encoder.encodeItem(new byte[] { (byte) longString[j] });
        }
        byte[][] datas = new byte[elements.length][];
        for(int j = 0; j < datas.length; j++) {
            datas[j] = elements[j].buffer;
        }

//        theirs = RLP.encodeList(datas);
        byte[] mine = encoder.encodeList(
                elements
        ).buffer;

        NewRLPElement e = decoder.decode(longString);
        NewRLPElement f = encoder.encodeItem(0x00);

        System.out.println(
                e.equals(f)
        );

        NewRLPList g = decoder.decodeList(shortList0);
        g.size();
        g.get(0);
        for( NewRLPElement z : g) {
            System.out.println(z.toString());
        }
//        g.remove(0);

        System.out.println(
                !decoder.decode(longList__).equals(encoder.encodeItem(0x00))
        );

        try {
            decoder.decode(empty);
        } catch (ArrayIndexOutOfBoundsException aioobe) {

        }

        NewRLPItem sb = decoder.decodeItem(singleByte);
        NewRLPItem zz = encoder.encodeItem('z');

        System.out.println(sb.toString());
        System.out.println(zz.toString());

        System.out.println(
                decoder.decodeItem(singleByte).equals(encoder.encodeItem('z'))
        );

        System.out.println(
                encoder.encodeItem('z').equals(encoder.encodeItem((long) 'z'))
        );

    }

}

package org.ethereum.util;

import java.math.BigInteger;
import java.nio.charset.Charset;

import static org.spongycastle.util.BigIntegers.asUnsignedByteArray;

/**
 * Created by esaulpaugh on 1/22/17.
 */
public class TestHax {

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


        TestUtils.printBytes(RLP.encodeElement(asUnsignedByteArray(BigInteger.ZERO)));

        TestUtils.printBytes(RLP.encodeByte((byte) 0));







        byte[][] arrays = new byte[][]{
                singleByte,
                shortString,
                longString,
                shortList0,
                shortList1,
                longList__
        };

        for (byte[] a : arrays) {
            NewRLPElement e = NewRLPElement.decode(a);
            System.out.println(e.toString());
        }

        NewRLPElement a = NewRLPElement.decode(shortString);
        NewRLPElement b = NewRLPElement.encodeItem("wew", Charset.forName("UTF-8"));

        System.out.println(a.toString());
        System.out.println(b.toString());

        TestUtils.printChars(a.getRLPData());
        TestUtils.printChars(b.getRLPData());

        TestUtils.printBytes(a.getRLPData());
        TestUtils.printBytes(b.getRLPData());

        System.out.println(a.equals(b));

        System.out.println(a.hashCode());
        System.out.println(b.hashCode());

        System.out.println(a.hashCode() == b.hashCode());

    }

}

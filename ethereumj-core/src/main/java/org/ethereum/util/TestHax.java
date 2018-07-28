package org.ethereum.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
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

        NewRLPElement a, b;

        printRLPCustom(NewRLPElement.decode(singleByte));
        printRLPCustom(NewRLPElement.decode(shortString));
        printRLPCustom(NewRLPElement.decode(longString));
        printRLPCustom(NewRLPElement.decode(shortList0));
        printRLPCustom(NewRLPElement.decode(shortList1));
        printRLPCustom(NewRLPElement.decode(longList__));

        a = NewRLPElement.decode(shortList0);

//        b = NewRLPElement.encodeItem("z", UTF_8);
//        b = NewRLPElement.encodeItem("wew", UTF_8);
//        b = NewRLPElement.encodeItem("Lorem ipsum dolor sit amet, consectetur adipisicing elit", UTF_8);
        b = NewRLPElement.encodeList(NewRLPElement.encodeItem("long", UTF_8), NewRLPElement.encodeItem("walk", UTF_8));
//        b = NewRLPElement.encodeList(
//                NewRLPElement.encodeItem("cat", UTF_8), NewRLPElement.encodeItem("dog", UTF_8),
//                NewRLPElement.encodeList(NewRLPElement.encodeItem("cat", UTF_8), NewRLPElement.encodeItem("dog", UTF_8))
//        );
//        NewRLPElement[] elements = new NewRLPElement[longString.length - 2];
//        for(int i = 2, j = 0; i < longString.length; i++) {
//            elements[j++] = NewRLPElement.encodeItem(new byte[] { longString[i] });
//        }
//        b = NewRLPElement.encodeList(
//                elements
//        );


        System.out.println();

        printRLPCustom(a);
        printRLPCustom(b);

//        for(char c : "long".toCharArray()) {
//            System.out.print(HexBin.encode(new byte[] {(byte) c}));
//        }
//
//        for(char c : "walk".toCharArray()) {
//            System.out.print(HexBin.encode(new byte[] {(byte) c}));
//        }

//        for(byte by : longString) {
//            System.out.print(by + ",");
//        }
//        System.out.println();
//        for(byte by : longString) {
//            System.out.print(HexBin.encode(new byte[] { by }) + ",");
//        }
//        System.out.println();


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

    private static final void printRLPCustom(NewRLPElement element) {
        switch (element.getType()) {
        case SINGLE_BYTE: System.out.println((char) element.rlpData[0]); break;
        case ITEM_SHORT: System.out.println(HexBin.encode(new byte[] { element.rlpData[0] }) + ", " + (char) element.rlpData[1] + " . . ."); break;
        case ITEM_LONG: System.out.println(HexBin.encode(new byte[] { element.rlpData[0], element.rlpData[1], element.rlpData[2] }) + " . . ."); break;
        case LIST_SHORT: System.out.println(HexBin.encode(element.rlpData)); break;
        case LIST_LONG: System.out.println(HexBin.encode(new byte[] { element.rlpData[0], element.rlpData[1] }) + new String(Arrays.copyOfRange(element.rlpData, 2, element.rlpData.length), UTF_8)); break;
        }
    }

}

package org.ethereum.util.rlp;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.ethereum.util.RLP;
import org.ethereum.util.RLPList;
import org.ethereum.util.TestUtils;

import java.math.BigInteger;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.spongycastle.util.BigIntegers.asUnsignedByteArray;

/**
 * Created by esaulpaugh on 1/22/17.
 */
public class TestHax {

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


        TestUtils.printBytes(RLP.encodeElement(asUnsignedByteArray(BigInteger.ZERO)));

        TestUtils.printBytes(RLP.encodeByte((byte) 0));


        byte[][] arrays = new byte[][]{
                zeroA,
                zeroB,
                empty,
                singleByte,
                shortString,
                longString,
                shortList0,
                shortList1,
                longList__
        };

//        for (byte[] a : arrays) {
//            NewRLPElement e = NewRLPElement.decode(a);
//            System.out.println(e.toString());
//        }

        OORLP encoder = new OORLP();
        OORLP decoder = new OORLP();

        NewRLPElement a, b, c;
//        printRLPCustom(NewRLPElement.decode(zeroA));
//        printRLPCustom(NewRLPElement.decode(zeroB));
//        printRLPCustom(NewRLPElement.decode(empty));
//        printRLPCustom(NewRLPElement.decode(singleByte));
//        printRLPCustom(NewRLPElement.decode(shortString));
//        printRLPCustom(NewRLPElement.decode(longString));
//        printRLPCustom(NewRLPElement.decode(shortList0));
//        printRLPCustom(NewRLPElement.decode(shortList1));
//        printRLPCustom(NewRLPElement.decode(longList__));

        byte[] _a = encoder.encodeItem(zeroA).buffer;
        byte[] _b = encoder.encodeList(encoder.encodeItem(zeroA)).buffer;
        byte[] _c = new byte[] { (byte) 0xc1, 0x00 };

        a = decoder.decode(_a);
        b = decoder.decode(_b);
        c = decoder.decode(_c);

//        a = NewRLPElement.encodeItem(empty);
//        b = NewRLPElement.decode(zeroB);

        byte[] x = a.getData();
        byte[] y = b.getData();
        byte[] z = b.getData();

//        System.out.println(Arrays.toString(x));
//        System.out.println(Arrays.toString(y));
//        System.out.println(Arrays.toString(z));

//        b = NewRLPElement.encodeItem(zeroA);
//        b = NewRLPElement.encodeItem(zeroB);

//        b = NewRLPElement.encodeItem("z", UTF_8);
//        b = NewRLPElement.encodeItem("wew", UTF_8);
//        b = NewRLPElement.encodeItem("Lorem ipsum dolor sit amet, consectetur adipisicing elit", UTF_8);
//        b = NewRLPElement.encodeList(NewRLPElement.encodeItem("long", UTF_8), NewRLPElement.encodeItem("walk", UTF_8));
        b = encoder.encodeList(
                encoder.encodeItem("cat", UTF_8), encoder.encodeItem("dog", UTF_8),
                encoder.encodeList(
                        encoder.encodeItem("cat", UTF_8),
                        encoder.encodeItem("dog", UTF_8),
                        encoder.encodeList(encoder.encodeItem(new byte[] { (byte) 'p'}), encoder.encodeItem("owl", UTF_8), encoder.encodeItem("zebra", UTF_8))
                )
        );

//        NewRLPElement[] elements = new NewRLPElement[longString.length - 2];
//        for(int i = 2, j = 0; i < longString.length; i++) {
//            elements[j++] = NewRLPElement.encodeItem(new byte[] { longString[i] });
//        }
//        b = NewRLPElement.encodeList(
//                elements
//        );

        NewRLPElement e = decoder.decode(b.buffer, 0);

//        StringBuilder sb0 = new StringBuilder();
//        e.recursivePrint(sb0);
        System.out.println(e.toString());

//        StringBuilder sb1 = new StringBuilder();
//        b.recursivePrint(sb1);
        System.out.println(b.toString());

        byte[] iii = b.buffer;
        NewRLPElement decoded = decoder.decode(iii);

        System.out.println(decoded.equals(b));

        RLPList list = RLP.decode2(b.buffer);
        RLPList.recursivePrint(list);

//        printRLPCustom(a);
//        printRLPCustom(b);
//        printRLPCustom(c);

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


//        System.out.println(a.toString());
//        System.out.println(b.toString());
//        System.out.println(c.toString());
//
//        TestUtils.printChars(a.getRLPData());
//        TestUtils.printChars(b.getRLPData());
//        TestUtils.printChars(c.getRLPData());
//
//        TestUtils.printBytes(a.getRLPData());
//        TestUtils.printBytes(b.getRLPData());
//        TestUtils.printBytes(c.getRLPData());
//
//        System.out.println(a.equals(b));
//        System.out.println(a.equals(c));
//        System.out.println(b.equals(c));
//
//        System.out.println(a.hashCode());
//        System.out.println(b.hashCode());
//        System.out.println(c.hashCode());

//        System.out.println(a.hashCode() == b.hashCode());

    }

    private static final void printRLPCustom(NewRLPElement element) {
        switch (element.getType()) {
        case SINGLE_BYTE: System.out.println((char) element.buffer[0]); break;
        case ITEM_SHORT: System.out.println(HexBin.encode(new byte[] { element.buffer[0] }) + ", . . . "); break;
        case ITEM_LONG: System.out.println(HexBin.encode(new byte[] { element.buffer[0], element.buffer[1], element.buffer[2] }) + " . . ."); break;
        case LIST_SHORT: System.out.println(HexBin.encode(element.buffer)); break;
        case LIST_LONG: System.out.println(HexBin.encode(new byte[] { element.buffer[0], element.buffer[1] }) + new String(Arrays.copyOfRange(element.buffer, 2, element.buffer.length), UTF_8)); break;
        }
    }

}

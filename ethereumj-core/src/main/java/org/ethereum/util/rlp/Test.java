package org.ethereum.util.rlp;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.math.BigInteger;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Test {

    public static void main(String[] args0) {

//        char c = (char) -0x01;
//        byte b = -0x7f;
//        long lo = 0x80;
//
////        System.out.println((lo < 0x80) + " " + (lo & 0xFF));
//
//        NewRLPItem item = new OORLP().encodeAsItem("", UTF_8); // new OORLP().encodeAsItem(new byte[] {}); // new OORLP().encodeAsItem(lo);
//
//        System.out.println(item.getType() + " " + HexBin.encode(item.encoding()));
//        System.out.println(new String(item.data(), UTF_8));
//        System.out.println(Arrays.toString(item.data()));

//        new BigInteger(new byte[] {});

        byte[] dest = new byte[17];
        int destIndex = 0;
//        destIndex = NewRLP.encodeLong(127, dest, destIndex);
//        destIndex = NewRLP.encodeLong(128, dest, destIndex);
//        destIndex = NewRLP.encodeItem(new byte[]{}, dest, destIndex);
//        destIndex = NewRLP.encodeItem("", UTF_8, dest, destIndex);
//        destIndex = NewRLP.encodeBigInteger(BigInteger.TEN, dest, destIndex);
//        destIndex = NewRLP.encodeItem(new byte[] { 0 }, dest, destIndex);
//        destIndex = NewRLP.encodeItem("\0", UTF_8, dest, destIndex);
//        destIndex = NewRLP.encodeBigInteger(BigInteger.ZERO, dest, destIndex);
//        System.out.println(HexBin.encode(dest));
//        destIndex = NewRLP.encodeBigInteger(BigInteger.valueOf(127), dest, destIndex);
//        System.out.println(HexBin.encode(dest));
//        destIndex = NewRLP.encodeBigInteger(BigInteger.valueOf(129), dest, destIndex);
//        destIndex = NewRLP.encodeBigInteger(BigInteger.valueOf(-1), dest, destIndex);

        System.out.println(Arrays.toString(dest));
        System.out.println(HexBin.encode(dest));

        NewRLPItem item = OORLP.encodeAsItem(BigInteger.valueOf(128));

        System.out.println(item.getType() + " " + HexBin.encode(item.encoding()));
        System.out.println(new String(item.data(), UTF_8));
        System.out.println(Arrays.toString(item.data()));

        System.arraycopy(item.encoding(), 0, dest, 1, item.encodingLength());

        System.out.println(Arrays.toString(dest));
        System.out.println(HexBin.encode(dest));

        BigInteger bi = OORLP.decodeBigInteger(dest, 1);

        System.out.println(bi);

        int x = item.exportData(dest, 0);

        System.out.println(x);
        System.out.println(Arrays.toString(dest));
        System.out.println(HexBin.encode(dest));

        x = item.exportEncoding(dest, x);

        System.out.println(x);
        System.out.println(Arrays.toString(dest));
        System.out.println(HexBin.encode(dest));

        x = item.exportEncoding(dest, x);

        System.out.println(x);
        System.out.println(Arrays.toString(dest));
        System.out.println(HexBin.encode(dest));

        final byte[] shortList = new byte[] { (byte) 0xca, (byte) 0x84, 'l', 'o', 'n', 'g', (byte) 0x84, 'w', 'a', 'l', 'k' };
        final byte[] shortList1 = new byte[]{(byte) 0xd1, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g', (byte) 0xc8, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g'};

        byte[] longString = new byte[]{(byte) 0xb8, (byte) 0x38, 'L', 'o', 'r', 'e', 'm', ' ', 'i', 'p', 's', 'u', 'm', ' ', 'd', 'o', 'l', 'o', 'r', ' ', 's', 'i', 't', ' ', 'a', 'm', 'e', 't', ',', ' ', 'c', 'o', 'n', 's', 'e', 'c', 't', 'e', 't', 'u', 'r', ' ', 'a', 'd', 'i', 'p', 'i', 's', 'i', 'c', 'i', 'n', 'g', ' ', 'e', 'l', 'i', 't'};

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Math.pow(2, 4); i++) {
            sb.append('.');
        }
        byte[] longLongString = OORLP.encodeAsItem(sb.toString(), UTF_8).encoding();

//        System.out.println(Arrays.toString(longLongString));

        NewRLPList longLongList = OORLP.encodeAsList(longLongString);

//        System.out.println(Arrays.toString(longLongList.encoding()));

//        byte[] longList__ = new byte[longString.length];
//        longList__[0] = (byte) (0xf7 + 1);
//        longList__[1] = (byte) (longString.length - 2);
//        System.arraycopy(longString, 2, longList__, 2, 56);


        NewRLPList list = OORLP.decodeList(longLongList.encoding());

        System.out.println(Arrays.toString(list.encoding()));
        System.out.println(list.toUtf8());
        System.out.println(list.equals(longLongList));

//        for(NewRLPElement e : list.elements()) {
//            System.out.println(new String(e.data(), UTF_8));
//        }

//        System.out.println(Arrays.toString(list.encoding()));
//        System.out.println(list.toUtf8());

        NewRLPList subList = OORLP.encodeAsList(list.encoding(), list.getDataIndex(), list.getDataLength());

        System.out.println(Arrays.toString(subList.encoding()));
        System.out.println(subList.toUtf8());
        System.out.println(subList.equals(list));

//        bi = OORLP.decodeBigInteger(new byte[] { (byte) 0x83, 0, -1, -4 }, 0);
//        System.out.println(bi);
//
//        long longy = NewRLP.decodeLong(new byte[] { (byte) 0x82, -1, -4 }, 1, 2);
//        System.out.println(longy);

        NewRLPList dec = OORLP.decodeList(subList.encoding(), 0);

        System.out.println(Arrays.toString(dec.encoding()));
        System.out.println(dec.toUtf8());
        System.out.println(dec.equals(subList));

        NewRLPList enc = OORLP.encodeAsList(dec.data());

        System.out.println(Arrays.toString(enc.encoding()));
        System.out.println(enc.toUtf8());
        System.out.println(enc.equals(dec));

        NewRLPList fromElements = OORLP.encodeAsList(enc.elements());

        System.out.println(fromElements.toUtf8());

        System.out.println(Arrays.toString(fromElements.encoding()));
        System.out.println(fromElements.toUtf8());
        System.out.println(fromElements.equals(enc));

        NewRLPList fromElements2 = OORLP.encodeAsList(fromElements.elements().toArray(new NewRLPElement[fromElements.size()]));

        System.out.println(Arrays.toString(fromElements2.encoding()));
        System.out.println(fromElements2.toUtf8());
        System.out.println(fromElements2.equals(fromElements));

    }
}

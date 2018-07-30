package org.ethereum.util.rlp;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TestHax6 {

    public static void main(String[] args0) throws Exception {

//        byte[] middle = new byte[] { 0x00, 0x00, (byte) 0xc3, (byte) 0x82, 'e', 'w', (byte) 0x81, 0x19 };
//
//        NewRLPElement e = OORLP.decode(middle, 0);
//
//        byte[] enc = e.getEncoding();
//
//        System.out.println(Arrays.toString(enc));
//
//        System.out.println(Arrays.toString(e.getData()));
//
//        System.out.println(e.encodingLength());
//
//        System.out.println(e.getType() == ElementType.LIST_SHORT);
//
////        for(NewRLPElement pp : (NewRLPList) e) {
////            System.out.println(pp.toString());
////            System.out.println(pp.hashCode());
////            System.out.println(pp.equals(pp));
////            System.out.println(pp.equals(e));
////            System.out.println(e.equals(pp));
////        }
//
//        System.out.println(e.equals(e));
//
//        System.out.println(e.hashCode());

        // -----
        byte[] buffer = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };

        System.out.println(Long.toBinaryString(Long.MIN_VALUE));
        System.out.println(Long.toBinaryString(-1));
        System.out.println(Long.toBinaryString(0));
        System.out.println(Long.toBinaryString(1));
        System.out.println(Long.toBinaryString(Long.MAX_VALUE));
        System.out.println(Long.MIN_VALUE ^ Long.MAX_VALUE);

        for(long i = Long.MIN_VALUE; i < Long.MIN_VALUE + 1000; i++) {
            int s = NewRLP.putLong(i, buffer, 0);
            System.out.println(Arrays.toString(buffer));
            System.out.println(HexBin.encode(buffer));
            long x = NewRLP.decodeLong(buffer, 0, s);
            System.out.println(x);
        }

        if(true) return;

        long a, b, c;

//        ByteBuffer bb = ByteBuffer.allocate(8);
//        byte[] buf = bb.putLong(65537).array();
//        System.out.println(Arrays.toString(buf));
//        System.out.println(HexBin.encode(buf));
//        long negTwo = bb.getLong(0);
//        System.out.println(negTwo);


        a = 17000000;
        System.out.println(a);
        int s = NewRLP.putLong(a, buffer, 0);
        System.out.println(Arrays.toString(buffer));
        System.out.println(HexBin.encode(buffer));
        b = NewRLP.decodeLong(buffer, 0, s);
        System.out.println(b);
//        bb.flip()

        s = NewRLP.putLong(b, buffer, 0);
        System.out.println(Arrays.toString(buffer));
        System.out.println(HexBin.encode(buffer));
        c = NewRLP.decodeLong(buffer, 0, s);

//        bb = ByteBuffer.allocate(8);
//        buf = bb.putLong(-72057594037927937L).array();
//        System.out.println(Arrays.toString(buf));
//        System.out.println(HexBin.encode(buf));
//        guinea = bb.getLong(0);
//        System.out.println(guinea);

        System.out.println("c_reverse = " + Long.reverseBytes(c));

        // --

//        byte[] neg = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
//
//        for(int i = -2; i < 2; i++) {
//
//            int size = NewRLP.encodeLong(i, neg, 0);
////            System.out.println(size);
//            byte[] sub = Arrays.copyOfRange(neg, 0, size);
//            System.out.println(Arrays.toString(sub));
//            System.out.println(HexBin.encode(sub));
//
////        byte[] invalid = new byte[] { (byte) 0x82, 0x00, (byte) 0xFF };
//
////            byte[] test = new byte[] { -120, -1, -1, -1, -1, -1, -1, -1, -1 };
//
//            long result = NewRLP.decodeLong(neg, 0);
//
//            boolean same = result == i;
//            System.out.println(result + " == " + i + " " + same);
////            if(!same) {
////                System.out.println("not same");
//////                throw new Exception("not same");
////            }
//        }

//        int size = NewRLP.encodeLong(-1, neg, 0);
//        System.out.println(size);
//        System.out.println(Arrays.toString(neg));
//
////        byte[] invalid = new byte[] { (byte) 0x82, 0x00, (byte) 0xFF };
//
//        byte[] test = new byte[] { -120, -1, -1, -1, -1, -1, -1, -1, -1 };
//
//        long inval = NewRLP.decodeLong(test, 0);
//
//        System.out.println(inval + " " + HexBin.encode(test));

    }

}

package org.ethereum.util.rlp;

import org.ethereum.util.rlp.NewRLP;

public class TestHax4 {

    public static void main(String[] args0) throws Exception {

//        long x = 0, y = 1, z = Integer.MIN_VALUE;
//
//        byte[] xx = NewRLP.longToBytes(x);
//        byte[] yy = NewRLP.longToBytes(y);
//        byte[] zz = NewRLP.longToBytes(z);
//
//        byte[] xx2 = NewRLP.longToBytes2(x);
//        byte[] yy2 = NewRLP.longToBytes2(y);
//        byte[] zz2 = NewRLP.longToBytes2(z);
//
//        byte[] xx3 = NewRLP.longToBytes3(x);
//        byte[] yy3 = NewRLP.longToBytes3(y);
//        byte[] zz3 = NewRLP.longToBytes3(z);
//
//        System.out.println(Arrays.toString(xx));
//        System.out.println(Arrays.toString(yy));
//        System.out.println(Arrays.toString(zz));
//
//        System.out.println(Arrays.toString(xx2));
//        System.out.println(Arrays.toString(yy2));
//        System.out.println(Arrays.toString(zz2));
//
//        System.out.println(Arrays.toString(xx3));
//        System.out.println(Arrays.toString(yy3));
//        System.out.println(Arrays.toString(zz3));
//
//        long x_ = NewRLP.bytesToLong(xx);
//        long y_ = NewRLP.bytesToLong(yy);
//        long z_ = NewRLP.bytesToLong(zz);
//
//        long x2_ = NewRLP.bytesToLong(xx2);
//        long y2_ = NewRLP.bytesToLong(yy2);
//        long z2_ = NewRLP.bytesToLong(zz2);
//
//        long x3_ = NewRLP.bytesToLong(xx3);
//        long y3_ = NewRLP.bytesToLong(yy3);
//        long z3_ = NewRLP.bytesToLong(zz3);
//
//        System.out.println(x_);
//        System.out.println(y_);
//        System.out.println(z_);
//
//        System.out.println(x2_);
//        System.out.println(y2_);
//        System.out.println(z2_);
//
//        System.out.println(x3_);
//        System.out.println(y3_);
//        System.out.println(z3_);


        long start, end;
        double elapsed;

//        byte[] xx = new byte[30];
//        for(long i = Long.MAX_VALUE; i > Long.MAX_VALUE - 1000111000; i--) {
//            int n = NewRLP.putLong(i, xx, 5);
//            long lo = NewRLP.bytesToLong(xx, 5, n);
//            if(i != lo) {
//                throw new Exception("!!");
//            }
//        }
//        start = System.nanoTime();
//        for(long i = Long.MAX_VALUE; i > Long.MAX_VALUE - 1000111000; i--) {
//            int n = NewRLP.putLong(i, xx, 5);
//            long lo = NewRLP.bytesToLong(xx, 5, n);
//            if(i != lo) {
//                throw new Exception("!!");
//            }
//        }
//        end = System.nanoTime();
//
//        elapsed = (end - start) / 1000000.0;
//
//        System.out.println("elapsed = " + elapsed);

//        long x_ = NewRLP.bytesToLong(xx);


        // longToBytes()    elapsed = 50675.92973
        // longToBytes2()   elapsed = 61196.218651
        // longToBytes3()   elapsed = 74112.099589 (elapsed = 55187.8307 with static buffer)

    }

}

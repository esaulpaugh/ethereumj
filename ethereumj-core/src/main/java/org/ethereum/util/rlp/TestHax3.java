package org.ethereum.util.rlp;

import org.ethereum.util.rlp.NewRLP;

public class TestHax3 {

    private static byte[][] tests = new byte[][]{
//            NewRLP.encodeLong(-12),
//            RLP.encodeInt(129),
//            RLP.encodeInt(-300),
//            RLP.encodeInt(33333),
//            RLP.encodeInt(-66000),
//            RLP.encodeInt(999000),
//            RLP.encodeInt(-17000111),
//            RLP.encodeInt(Integer.MAX_VALUE - 1),
//            RLP.encodeInt(Integer.MIN_VALUE),
            new byte[] { (byte) (0x80 + 8), (byte) 0x3F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
};

    public static void main(String[] args0) {
        for(byte[] test : tests) {
            long lo = NewRLP.decodeLong(test, 0);
            System.out.println(lo);
        }
    }
}

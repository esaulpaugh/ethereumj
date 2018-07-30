package org.ethereum.util;

import org.ethereum.util.rlp.NewRLPItem;
import org.ethereum.util.rlp.NewRLPList;
import org.ethereum.util.rlp.OORLP;

public class Test2 {

    public static void main(String[] args0) {
//        NewRLPList list = OORLP.encodeAsList(new byte[0]);
//        NewRLPItem item = OORLP.encodeAsItem((int) 999999999L);

        NewRLPItem item = OORLP.encodeAsItem('~');
        char c = OORLP.decodeChar(item.data(), 0);
        System.out.println(c);

    }

}

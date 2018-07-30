package org.ethereum.util.rlp;

import java.util.*;

import static org.ethereum.util.rlp.ElementType.LIST_LONG;
import static org.ethereum.util.rlp.ElementType.LIST_SHORT;
import static org.ethereum.util.rlp.ElementType.LONG_DATA_THRESHOLD;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPList extends NewRLPElement {

    private NewRLPList(byte[] buffer, int index) { // , ArrayList<NewRLPElement> elements
        super(buffer, index);
    }

    public static NewRLPList fromElements(NewRLPElement... elements) {
        return fromElements(Arrays.asList(elements));
    }

    /**
     *
     * @param srcElements pre-encoded top-level elements of the list
     * @return
     */
    public static NewRLPList fromElements(Iterable<NewRLPElement> srcElements) {

        byte[] dest;

        int dataLen = 0;
        for (NewRLPElement element : srcElements) {
            dataLen += element.encodingLength();
        }

        if (dataLen < LONG_DATA_THRESHOLD) {
            dest = new byte[1 + dataLen];
            dest[0] = (byte) (LIST_SHORT.offset + dataLen);
            copyElements(srcElements, dest, 1);
        } else {
            dest = encodeListLong(dataLen, srcElements);
        }

        return NewRLPList.fromEncoding(dest);
    }

    static NewRLPList fromEncoding(byte[] buffer) {
        return fromEncoding(buffer, 0);
    }

    static NewRLPList fromEncoding(byte[] buffer, int index) {
        return new NewRLPList(buffer, index);
    }

    static NewRLPList fromData(byte[] buffer, int dataIndex, int dataLen) {
        byte[] dest;
        if (dataLen < LONG_DATA_THRESHOLD) {
            dest = new byte[1 + dataLen];
            dest[0] = (byte) (LIST_SHORT.offset + dataLen);
            System.arraycopy(buffer, dataIndex, dest, 1, dataLen);
        } else {
            dest = OORLP.encodeElementLong(LIST_LONG, buffer, dataIndex, dataLen);
//            dest = encodeListLong(dataLen, srcElements);
        }

        return fromEncoding(dest);
    }

    @Override
    protected void recursivePrint(boolean hex, StringBuilder sb) {
        sb.append("[");
        for(NewRLPElement e : elements()) {
            e.recursivePrint(hex, sb);
        }
        sb.append("]");
    }

    public int size() {
        return getMetadata().size;
    }

    public List<NewRLPElement> elements() {

        Metadata metadata = getMetadata();

        int i = metadata.dataIndex;
        final int end = i + metadata.dataLength;

        ArrayList<NewRLPElement> arrayList = new ArrayList<>();

        while (i < end) {
            NewRLPElement newElement = OORLP.decode(buffer, i);

//            NewRLPElement newElement;
//            ElementType type = ElementType.type(buffer[i]);
//            switch (type) {
//            case ITEM_SINGLE_BYTE:
//            case ITEM_SHORT:
//            case ITEM_LONG:
//                newElement = NewRLPItem.fromBufferAtIndex(buffer, i);
//                break;
//            case LIST_SHORT:
//            case LIST_LONG:
//                newElement = NewRLPList.fromBufferAtIndex(buffer, i);
//                break;
//            default:
//                throw new RuntimeException("???");
//            }

            arrayList.add(newElement);
            i += newElement.encodingLength();
        }

        getMetadata().size = arrayList.size();

        return Collections.unmodifiableList(arrayList);
    }

    private static byte[] encodeListLong(final int srcDataLen, final Iterable<NewRLPElement> srcElements) { // prefixOffset typically 0x80, 0xb7, 0xc0, or 0xf7

        int t = srcDataLen;

        byte a = 0, b = 0, c = 0, d;

        int n = 1;
        d = (byte) (t & 0xFF);
        t = t >>> Byte.SIZE;
        if(t != 0) {
            n = 2;
            c = (byte) (t & 0xFF);
            t = t >>> Byte.SIZE;
            if(t != 0) {
                n = 3;
                b = (byte) (t & 0xFF);
                t = t >>> Byte.SIZE;
                if(t != 0) {
                    n = 4;
                    a = (byte) (t & 0xFF);
                }
            }
        }

        int destDataIndex = 1 + n;
        byte[] dest = new byte[destDataIndex + srcDataLen];
        dest[0] = (byte) (ElementType.LIST_LONG.offset + n);

        OORLP.insertLengthBytes(dest, n, a, b, c, d);

        copyElements(srcElements, dest, destDataIndex);

        return dest;
    }

    private static void copyElements(Iterable<NewRLPElement> srcElements, byte[] dest, int destIndex) {
        for (final NewRLPElement element : srcElements) {
            final int elementLen = element.encodingLength();
            System.arraycopy(element.buffer, element.index, dest, destIndex, elementLen);
            destIndex += elementLen;
        }
    }
}

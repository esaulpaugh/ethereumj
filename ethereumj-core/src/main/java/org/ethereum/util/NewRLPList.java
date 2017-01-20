package org.ethereum.util;

import org.spongycastle.util.encoders.Hex;

import java.util.*;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPList extends NewRLPElement implements List<NewRLPElement>, Buildable<NewRLPList> {

    public static void main(String[] args0) {
        NewRLPList list = new NewRLPList(new byte[] { (byte) 0xc8, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g' }).build();

        list.add(new NewRLPList(new byte[] { (byte) 0xc8, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g' }).build());

        StringBuilder sb = new StringBuilder();
        list.recursivePrint(sb);
        System.out.println(sb.toString());
    }

    private ArrayList<NewRLPElement> arrayList;

    NewRLPList(byte[] rlpData) {
        this(rlpData, 0);
    }

    NewRLPList(byte[] rlpData, int rlpIndex) {
        super(rlpData, rlpIndex);
        this.arrayList = new ArrayList<NewRLPElement>();
    }

    NewRLPList(byte[] rlpData, int rlpIndex, ElementType type) {
        super(rlpData, rlpIndex, type);
        this.arrayList = new ArrayList<NewRLPElement>();
    }

    private static void decode(NewRLPList list) {
        //
    }



//    /**
//     * Get exactly one message payload
//     */
//    private static void fullTraverse(NewRLPList rlpList, byte[] msgData, int level, final int start,
//                                     final int end, int levelToIndex) {
//
//        try {
//            if (msgData == null || msgData.length == 0)
//                return;
//            int pos = start;
//
//            while (pos < end) {
//
////                logger.debug("fullTraverse: level: " + level + " startPos: " + pos + " endPos: " + endPos);
//
//
//                // It's a list with a payload more than 55 bytes
//                // data[0] - 0xF7 = how many next bytes allocated
//                // for the length of the list
//                if ((msgData[pos] & 0xFF) > OFFSET_LONG_LIST) {
//
//                    byte lengthOfLength = (byte) (msgData[pos] - OFFSET_LONG_LIST);
//                    int length = calcLength(lengthOfLength, msgData, pos);
//
//                    byte[] rlpData = new byte[lengthOfLength + length + 1];
//                    System.arraycopy(msgData, pos, rlpData, 0, lengthOfLength
//                            + length + 1);
//
//                    NewRLPList newLevelList = new NewRLPList();
////                    newLevelList.setRLPData(rlpData);
//
//                    fullTraverse(newLevelList, msgData, level + 1, pos + lengthOfLength + 1,
//                            pos + lengthOfLength + length + 1, levelToIndex);
//                    rlpList.add(newLevelList);
//
//                    pos += lengthOfLength + length + 1;
//                    continue;
//                }
//                // It's a list with a payload less than 55 bytes
//                if ((msgData[pos] & 0xFF) >= OFFSET_SHORT_LIST
//                        && (msgData[pos] & 0xFF) <= OFFSET_LONG_LIST) {
//
//                    byte length = (byte) ((msgData[pos] & 0xFF) - OFFSET_SHORT_LIST);
//
//                    byte[] rlpData = new byte[length + 1];
//                    System.arraycopy(msgData, pos, rlpData, 0, length + 1);
//
//                    NewRLPList newLevelList = new NewRLPList();
////                    newLevelList.setRLPData(rlpData);
//
//                    if (length > 0)
//                        fullTraverse(newLevelList, msgData, level + 1, pos + 1, pos + length
//                                + 1, levelToIndex);
//                    rlpList.add(newLevelList);
//
//                    pos += 1 + length;
//                    continue;
//                }
//                // It's an item with a payload more than 55 bytes
//                // data[0] - 0xB7 = how much next bytes allocated for
//                // the length of the string
//                if ((msgData[pos] & 0xFF) > OFFSET_LONG_ITEM
//                        && (msgData[pos] & 0xFF) < OFFSET_SHORT_LIST) {
//
//                    byte lengthOfLength = (byte) (msgData[pos] - OFFSET_LONG_ITEM);
//                    int length = calcLength(lengthOfLength, msgData, pos);
//
//                    // now we can parse an item for data[1]..data[length]
//                    byte[] item = new byte[length];
//                    System.arraycopy(msgData, pos + lengthOfLength + 1, item,
//                            0, length);
//
//                    byte[] rlpPrefix = new byte[lengthOfLength + 1];
//                    System.arraycopy(msgData, pos, rlpPrefix, 0,
//                            lengthOfLength + 1);
//
//                    NewRLPItem rlpItem = new NewRLPItem(item, 0);
//                    rlpList.add(rlpItem);
//                    pos += lengthOfLength + length + 1;
//
//                    continue;
//                }
//                // It's an item less than 55 bytes long,
//                // data[0] - 0x80 == length of the item
//                if ((msgData[pos] & 0xFF) > OFFSET_SHORT_ITEM
//                        && (msgData[pos] & 0xFF) <= OFFSET_LONG_ITEM) {
//
//                    byte length = (byte) ((msgData[pos] & 0xFF) - OFFSET_SHORT_ITEM);
//
//                    byte[] item = new byte[length];
//                    System.arraycopy(msgData, pos + 1, item, 0, length);
//
//                    byte[] rlpPrefix = new byte[2];
//                    System.arraycopy(msgData, pos, rlpPrefix, 0, 2);
//
//                    NewRLPItem rlpItem = new NewRLPItem(item, 0);
//                    rlpList.add(rlpItem);
//                    pos += 1 + length;
//
//                    continue;
//                }
//                // null item
//                if ((msgData[pos] & 0xFF) == OFFSET_SHORT_ITEM) {
//                    byte[] item = ByteUtil.EMPTY_BYTE_ARRAY;
//                    NewRLPItem rlpItem = new NewRLPItem(item, 0);
//                    rlpList.add(rlpItem);
//                    pos += 1;
//                    continue;
//                }
//                // single byte item
//                if ((msgData[pos] & 0xFF) < OFFSET_SHORT_ITEM) {
//
//                    byte[] item = {(byte) (msgData[pos] & 0xFF)};
//
//                    NewRLPItem rlpItem = new NewRLPItem(item, 0);
//                    rlpList.add(rlpItem);
//                    pos += 1;
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("RLP wrong encoding (" + Hex.toHexString(msgData, start, end - start) + ")", e);
//        } catch (OutOfMemoryError e) {
//            throw new RuntimeException("Invalid RLP (excessive mem allocation while parsing) (" + Hex.toHexString(msgData, start, end - start) + ")", e);
//        }
//    }




    @Override
    public int size() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return arrayList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return arrayList.contains(o);
    }

    @Override
    public Iterator<NewRLPElement> iterator() {
        return arrayList.iterator();
    }

    @Override
    public Object[] toArray() {
        return arrayList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return arrayList.toArray(a);
    }

    @Override
    public boolean add(NewRLPElement newRLPElement) {
        return arrayList.add(newRLPElement);
    }

    @Override
    public boolean remove(Object o) {
        return arrayList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return arrayList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends NewRLPElement> c) {
        return arrayList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends NewRLPElement> c) {
        return arrayList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return arrayList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return arrayList.retainAll(c);
    }

    @Override
    public void clear() {
        arrayList.clear();
    }

    @Override
    public NewRLPElement get(int index) {
        return arrayList.get(index);
    }

    @Override
    public NewRLPElement set(int index, NewRLPElement element) {
        return arrayList.set(index, element);
    }

    @Override
    public void add(int index, NewRLPElement element) {
        arrayList.add(index, element);
    }

    @Override
    public NewRLPElement remove(int index) {
        return arrayList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return arrayList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return arrayList.lastIndexOf(o);
    }

    @Override
    public ListIterator<NewRLPElement> listIterator() {
        return arrayList.listIterator();
    }

    @Override
    public ListIterator<NewRLPElement> listIterator(int index) {
        return arrayList.listIterator(index);
    }

    @Override
    public List<NewRLPElement> subList(int fromIndex, int toIndex) {
        return arrayList.subList(fromIndex, toIndex);
    }

    @Override
    public NewRLPList build() {

        final byte[] rlpData = getRLPData();
//        final byte[] data = getData();

        if(getType() == ElementType.LIST_SHORT) {
            final int start = (int) getDataIndex();
            final int end = start + (int) getDataLength();
            int pos = start;
            while(pos < end) {
                final byte byteZero = getByte(pos);
                ElementType type = ElementType.type(byteZero);
                NewRLPElement newElement;
                switch (type) {
                    case SINGLE_BYTE:
                    case ITEM_SHORT:
                    case ITEM_LONG:
                        newElement = new NewRLPItem(rlpData, pos, type);
                        break;
                    case LIST_SHORT:
//                        [ 0xc8, 0x83, 'c', 'a', 't', 0x83, 'd', 'o', 'g' ]
                    case LIST_LONG:
                        newElement = new NewRLPList(rlpData, pos, type).build();
                        break;
                    default:
                        throw new RuntimeException("???");

                }
                add(newElement);
                pos += newElement.rlpLength();
            }
        } else {

        }


        return this;
    }
}

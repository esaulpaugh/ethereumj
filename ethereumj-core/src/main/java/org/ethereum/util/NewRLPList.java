package org.ethereum.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPList extends NewRLPElement implements List<NewRLPElement>, Buildable<NewRLPList> {

    private static final byte[] singleByte = new byte[] { 'z' };
    private static final byte[] shortString = new byte[] { (byte) 0x83, 'w', 'e', 'w' };
    private static final byte[] longString = new byte[] { (byte) 0xb8, (byte) 0x38, 'L', 'o', 'r', 'e', 'm', ' ', 'i', 'p', 's', 'u', 'm', ' ', 'd', 'o', 'l', 'o', 'r', ' ', 's', 'i', 't', ' ', 'a', 'm', 'e', 't', ',', ' ', 'c', 'o', 'n', 's', 'e', 'c', 't', 'e', 't', 'u', 'r', ' ', 'a', 'd', 'i', 'p', 'i', 's', 'i', 'c', 'i', 'n', 'g', ' ', 'e', 'l', 'i', 't'};
    private static final byte[] shortList0 = new byte[]{ (byte) 0xca, (byte) 0x84, 'l', 'o', 'n', 'g', (byte) 0x84, 'w', 'a', 'l', 'k'};
    private static final byte[] shortList1 = new byte[]{ (byte) 0xd1, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g', (byte) 0xc8, (byte) 0x83, 'c', 'a', 't', (byte) 0x83, 'd', 'o', 'g'};
    private static final byte[] longList__;

    static {
        longList__ = new byte[longString.length];
        longList__[0] = (byte) (0xf7 + 1);
        longList__[1] = (byte) (longString.length - 2);
        System.arraycopy(longString, 2, longList__, 2, 56);
    }

    public static void main(String[] args0) {

        byte[][] arrays = new byte[][] {
                singleByte,
                shortString,
                longString,
                shortList0,
                shortList1,
                longList__
        };

        for(byte[] a : arrays) {
            NewRLPElement e = NewRLPElement.decode(a);
            System.out.println(e.toString());
        }

        NewRLPElement a = NewRLPElement.decode(shortString);
        NewRLPElement b = NewRLPElement.encodeString("wew", Charset.forName("UTF-8"));

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        recursivePrint(sb);
        return sb.toString();
    }

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

        int pos = (int) getDataIndex();
        final int end = pos + (int) getDataLength();

        while (pos < end) {
            NewRLPElement newElement;
            ElementType type = ElementType.type(rlpData[pos]);
            switch (type) {
                case SINGLE_BYTE:
                case ITEM_SHORT:
                case ITEM_LONG:
                    newElement = new NewRLPItem(rlpData, pos, type);
                    break;
                case LIST_SHORT:
                case LIST_LONG:
                    newElement = new NewRLPList(rlpData, pos, type).build();
                    break;
                default:
                    throw new RuntimeException("???");

            }
            add(newElement);
            pos += newElement.getRlpLength();
        }

        return this;
    }
}

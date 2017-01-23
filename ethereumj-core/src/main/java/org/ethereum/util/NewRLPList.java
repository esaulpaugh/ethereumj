package org.ethereum.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPList extends NewRLPElement implements List<NewRLPElement>, Buildable<NewRLPList> {

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        recursivePrint(sb);
        return sb.toString();
    }

    // --- List implementation ---

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
}

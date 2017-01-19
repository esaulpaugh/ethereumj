package org.ethereum.util;

import java.util.*;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPList extends NewRLPElement implements List<NewRLPElement> {

    private ArrayList<NewRLPElement> arrayList;

    NewRLPList(byte[] data, int index) {
        super(data, index);
        // TODO
        this.isShort = new Random().nextBoolean();
        this.length = -1111111111000000000L;

        this.arrayList = new ArrayList<NewRLPElement>();
    }

    public static void recursivePrint(RLPElement element) {

        if (element == null)
            throw new RuntimeException("RLPElement object can't be null");
        if (element instanceof RLPList) {

            RLPList rlpList = (RLPList) element;
            System.out.print("[");
            for (RLPElement singleElement : rlpList)
                recursivePrint(singleElement);
            System.out.print("]");
        } else {
            String hex = ByteUtil.toHexString(element.getRLPData());
            System.out.print(hex + ", ");
        }
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
}

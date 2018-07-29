package org.ethereum.util;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

import java.util.*;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPList extends NewRLPElement implements List<NewRLPElement>, Buildable<NewRLPList> {

    private final LazyInitializer<ArrayList<NewRLPElement>> lazyArrayList;

    NewRLPList(byte[] rlpData, int rlpIndex) {
        this(rlpData, rlpIndex, null);
    }

    NewRLPList(byte[] rlpData, int rlpIndex, final NewRLPElement[] elements) {
        super(rlpData, rlpIndex);
        this.lazyArrayList = new LazyInitializer<ArrayList<NewRLPElement>>() {
            @Override
            protected ArrayList<NewRLPElement> initialize() {
                System.out.println("=== initialize() arraylist ===");
                return elements == null
                        ? new ArrayList<NewRLPElement>()
                        : new ArrayList<>(Arrays.asList(elements));
            }
        };
    }

    @Override
    protected void recursivePrint(StringBuilder sb) {
        sb.append("[");
        for(NewRLPElement e : this) {
            e.recursivePrint(sb);
        }
        sb.append("]");
    }

    /**
     *
     *
     * @return
     */
    @Override
    public NewRLPList build() {

        Metadata metadata = getMetadata();

        int pos = metadata.dataIndex;
        final int end = pos + metadata.dataLength;

        while (pos < end) {
            NewRLPElement newElement;
            ElementType type = ElementType.type(rlpData[pos]);
            switch (type) {
            case SINGLE_BYTE:
            case ITEM_SHORT:
            case ITEM_LONG:
                newElement = new NewRLPItem(rlpData, pos);
                break;
            case LIST_SHORT:
            case LIST_LONG:
                newElement = new NewRLPList(rlpData, pos).build();
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

    public NewRLPItem getItem(int index) {
        return (NewRLPItem) get(index);
    }

    public NewRLPList getList(int index) {
        return (NewRLPList) get(index);
    }

    private ArrayList<NewRLPElement> getArrayList() {
        try {
            return lazyArrayList.get();
        } catch (ConcurrentException e) {
            throw new RuntimeException(e);
        }
    }

    // --- List implementation ---

    @Override
    public int size() {
        return getArrayList().size();
    }

    @Override
    public boolean isEmpty() {
        return getArrayList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getArrayList().contains(o);
    }

    @Override
    public Iterator<NewRLPElement> iterator() {
        return getArrayList().iterator();
    }

    @Override
    public Object[] toArray() {
        return getArrayList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getArrayList().toArray(a);
    }

    @Override
    public boolean add(NewRLPElement newRLPElement) {
        return getArrayList().add(newRLPElement);
    }

    @Override
    public boolean remove(Object o) {
        return getArrayList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getArrayList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends NewRLPElement> c) {
        return getArrayList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends NewRLPElement> c) {
        return getArrayList().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getArrayList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getArrayList().retainAll(c);
    }

    @Override
    public void clear() {
        getArrayList().clear();
    }

    @Override
    public NewRLPElement get(int index) {
        return getArrayList().get(index);
    }

    @Override
    public NewRLPElement set(int index, NewRLPElement element) {
        return getArrayList().set(index, element);
    }

    @Override
    public void add(int index, NewRLPElement element) {
        getArrayList().add(index, element);
    }

    @Override
    public NewRLPElement remove(int index) {
        return getArrayList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getArrayList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getArrayList().lastIndexOf(o);
    }

    @Override
    public ListIterator<NewRLPElement> listIterator() {
        return getArrayList().listIterator();
    }

    @Override
    public ListIterator<NewRLPElement> listIterator(int index) {
        return getArrayList().listIterator(index);
    }

    @Override
    public List<NewRLPElement> subList(int fromIndex, int toIndex) {
        return getArrayList().subList(fromIndex, toIndex);
    }
}

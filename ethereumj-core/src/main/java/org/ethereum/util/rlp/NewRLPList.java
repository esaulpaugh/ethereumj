package org.ethereum.util.rlp;

import org.ethereum.util.Buildable;
import org.ethereum.util.ElementType;

import java.util.*;

/**
 * Created by Evo on 1/19/2017.
 */
public class NewRLPList extends NewRLPElement implements List<NewRLPElement>, Buildable<NewRLPList>, RandomAccess {

    private List<NewRLPElement> elements;

    private NewRLPList(byte[] buffer, int index) {
        this(buffer, index, null);
    }

    NewRLPList(byte[] buffer, int index, ArrayList<NewRLPElement> elements) {
        super(buffer, index);
        setElements(elements);
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

        System.out.println("=== build() ===");

        Metadata metadata = getMetadata();

        int i = metadata.dataIndex;
        final int end = i + metadata.dataLength;

        ArrayList<NewRLPElement> arrayList = new ArrayList<>();

        while (i < end) {
            NewRLPElement newElement;
            ElementType type = ElementType.type(buffer[i]);
            switch (type) {
            case SINGLE_BYTE:
            case ITEM_SHORT:
            case ITEM_LONG:
                newElement = new NewRLPItem(buffer, i);
                break;
            case LIST_SHORT:
            case LIST_LONG:
                newElement = new NewRLPList(buffer, i).build();
                break;
            default:
                throw new RuntimeException("???");
            }
            arrayList.add(newElement);
            i += newElement.encodingLength();
        }

        setElements(arrayList);

        return this;
    }

    private void setElements(ArrayList<NewRLPElement> elements) {
        System.out.println("=== setElements() ===");
        this.elements = elements == null ? null : Collections.unmodifiableList(elements);

//                    ? Collections.unmodifiableList(new ArrayList<>())
//                    : Collections.unmodifiableList(elements);
    }

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        recursivePrint(sb);
//        return sb.toString();
//    }

    public NewRLPItem getItem(int index) {
        return (NewRLPItem) get(index);
    }

    public NewRLPList getList(int index) {
        return (NewRLPList) get(index);
    }

    private List<NewRLPElement> getList() {
        return elements;
    }

    // --- List implementation ---

    @Override
    public int size() {
        return getList().size();
    }

    @Override
    public boolean isEmpty() {
        return getList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getList().contains(o);
    }

    @Override
    public Iterator<NewRLPElement> iterator() {
        return getList().iterator();
    }

    @Override
    public Object[] toArray() {
        return getList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getList().toArray(a);
    }

    @Override
    public boolean add(NewRLPElement newRLPElement) {
        return getList().add(newRLPElement);
    }

    @Override
    public boolean remove(Object o) {
        return getList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends NewRLPElement> c) {
        return getList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends NewRLPElement> c) {
        return getList().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getList().retainAll(c);
    }

    @Override
    public void clear() {
        getList().clear();
    }

    @Override
    public NewRLPElement get(int index) {
        return getList().get(index);
    }

    @Override
    public NewRLPElement set(int index, NewRLPElement element) {
        return getList().set(index, element);
    }

    @Override
    public void add(int index, NewRLPElement element) {
        getList().add(index, element);
    }

    @Override
    public NewRLPElement remove(int index) {
        return getList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getList().lastIndexOf(o);
    }

    @Override
    public ListIterator<NewRLPElement> listIterator() {
        return getList().listIterator();
    }

    @Override
    public ListIterator<NewRLPElement> listIterator(int index) {
        return getList().listIterator(index);
    }

    @Override
    public List<NewRLPElement> subList(int fromIndex, int toIndex) {
        return getList().subList(fromIndex, toIndex);
    }
}

package io.github.totom3.commons.collect;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

/**
 *
 * @author Totom3
 * @param <E>
 * @param <F>
 */
public class ComputingList<E, F> extends ComputingCollection<E, F> implements List<E> {

    public ComputingList(List<F> deleguate, Function<E, F> interner, Function<F, E> externer) {
	super(deleguate, interner, externer);
    }

    public ComputingList(List<F> deleguate) {
	super(deleguate);
    }

    private List<F> list() {
	return (List<F>) deleguate;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
	int size = size();
	if (index < 0 || index >= size) {
	    throw new IndexOutOfBoundsException("Index " + index + " is out of bounds: 0 to " + (size - 1));
	}
	for (E obj : c) {
	    add(index++, obj);
	}
	return !c.isEmpty();
    }

    @Override
    public E get(int index) {
	return extern(list().get(index));
    }

    @Override
    public E set(int index, E element) {
	return extern(list().set(index, intern(element)));
    }

    @Override
    public void add(int index, E element) {
	list().add(index, intern(element));
    }

    @Override
    public E remove(int index) {
	return extern(list().remove(index));
    }

    @Override
    public int indexOf(Object o) {
	return list().indexOf(intern(o));
    }

    @Override
    public int lastIndexOf(Object o) {
	return list().lastIndexOf(intern(o));
    }

    @Override
    public ListIterator<E> listIterator() {
	return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
	return new ComputingListIt(list().listIterator(index));
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
	return new ComputingList<>(list().subList(fromIndex, toIndex), interner, externer);
    }

    class ComputingListIt extends ComputingIt implements ListIterator<E> {

	ComputingListIt(ListIterator<F> it) {
	    super(it);
	}

	ListIterator<F> it() {
	    return (ListIterator<F>) it;
	}

	@Override
	public boolean hasPrevious() {
	    return it().hasPrevious();
	}

	@Override
	public E previous() {
	    return extern(it().previous());
	}

	@Override
	public int nextIndex() {
	    return it().nextIndex();
	}

	@Override
	public int previousIndex() {
	    return it().previousIndex();
	}

	@Override
	public void set(E e) {
	    it().set(intern(e));
	}

	@Override
	public void add(E e) {
	    it().add(intern(e));
	}

    }
}

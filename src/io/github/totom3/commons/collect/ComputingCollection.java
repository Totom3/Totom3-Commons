package io.github.totom3.commons.collect;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 *
 * @author Totom3
 * @param <E>
 * @param <F>
 */
public class ComputingCollection<E, F> extends AbstractCollection<E> {

    protected final Collection<F> deleguate;

    protected final Function<E, F> interner;

    protected final Function<F, E> externer;

    public ComputingCollection(Collection<F> deleguate, Function<E, F> interner, Function<F, E> externer) {
	this.deleguate = checkNotNull(deleguate);
	this.interner = interner;
	this.externer = externer;
    }

    protected ComputingCollection(Collection<F> deleguate) {
	this.deleguate = checkNotNull(deleguate);
	this.interner = null;
	this.externer = null;
    }

    protected F intern(Object value) {
	if (value == null) {
	    return null;
	}
	if (interner == null) {
	    return null;
	}
	return interner.apply((E) value);
    }

    protected E extern(Object value) {
	if (value == null) {
	    return null;
	}
	if (externer == null) {
	    return null;
	}
	return externer.apply((F) value);
    }

    @Override
    public int size() {
	return deleguate.size();
    }

    @Override
    public boolean isEmpty() {
	return deleguate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
	return deleguate.contains(intern(o));
    }

    @Override
    public Iterator<E> iterator() {
	return new ComputingIt(deleguate.iterator());
    }

    @Override
    public boolean add(E e) {
	return deleguate.add(intern(e));
    }

    @Override
    public boolean remove(Object o) {
	return deleguate.remove(intern(o));
    }

    @Override
    public void clear() {
	deleguate.clear();
    }

    class ComputingIt implements Iterator<E> {

	final Iterator<F> it;

	ComputingIt(Iterator<F> it) {
	    this.it = it;
	}

	@Override
	public boolean hasNext() {
	    return it.hasNext();
	}

	@Override
	public E next() {
	    return extern(it.next());
	}
	
	@Override
	public void remove() {
	    it.remove();
	}
    }
}

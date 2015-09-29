package io.github.totom3.commons.binary;

import io.github.totom3.commons.misc.DataBank;
import java.io.IOException;
import java.util.Map.Entry;

/**
 *
 * @author Totom3
 * @param <E>
 * @param <T>
 */
public abstract class DataBankAdapter<E, T extends DataBank<E>> implements BinaryAdapter<T> {

    @Override
    public final T read(DeserializationContext context) throws IOException {
	T bank = supply();

	short id;
	while ((id = context.readShort()) != 0) {
	    E val;
	    try {
		val = readElement(context);
	    } catch (IOException ex) {
		throw new DeserializingException("Could not parse element with ID " + id, ex);
	    }

	    try {
		bank.insert(id, val);
	    } catch (IllegalArgumentException ex) {
		throw new DeserializingException("Invalid cache", ex);
	    }
	}

	return bank;
    }

    @Override
    public final void write(T obj, SerializationContext context) throws IOException {
	for (Entry<Short, E> entry : obj.all().entrySet()) {
	    short id = entry.getKey();
	    E val = entry.getValue();

	    // Write ID
	    context.writeShort(id);

	    // Write object
	    try {
		writeElement(val, context);
	    } catch (IOException ex) {
		throw new DeserializingException("Could not write element " + val + " with ID " + id, ex);
	    }
	}

	// Write ending mark
	context.writeShort(0);
    }

    protected abstract T supply();

    protected abstract E readElement(DeserializationContext context) throws IOException;

    protected abstract void writeElement(E obj,SerializationContext context) throws IOException;
}

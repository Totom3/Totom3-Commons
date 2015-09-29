package io.github.totom3.commons.binary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.testng.annotations.Test;

/**
 *
 * @author Totom3
 */
public class IDBinaryAdapterNGTest {

    final static IDBinaryAdapter<Root> ROOT_ADAPTER;
    final static File file;

    static {
	file = new File(".\\file.txt");
	file.getParentFile().mkdirs();
	try {
	    file.createNewFile();
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}

	ROOT_ADAPTER = IDBinaryAdapter.<Root>builder()
		.addAdapter((byte) 0, StringImpl.class, new BinaryAdapter<StringImpl>() {

		    @Override
		    public StringImpl read(DeserializationContext context) throws IOException {
			return new StringImpl(context.readString());
		    }

		    @Override
		    public void write(StringImpl obj, SerializationContext context) throws IOException {
			context.writeString((String) obj.get());
		    }
		})
		.addAdapter((byte) 1, NumberImpl.class, new BinaryAdapter<NumberImpl>() {

		    @Override
		    public NumberImpl read(DeserializationContext context) throws IOException {
			return new NumberImpl(context.readInt());
		    }

		    @Override
		    public void write(NumberImpl obj, SerializationContext context) throws IOException {
			context.writeInt(((Number) obj.get()).intValue());
		    }
		})
		.addAdapter((byte) 2, BooleanImpl.class, new BinaryAdapter<BooleanImpl>() {

		    @Override
		    public BooleanImpl read(DeserializationContext context) throws IOException {
			return new BooleanImpl(context.readBoolean());
		    }

		    @Override
		    public void write(BooleanImpl obj, SerializationContext context) throws IOException {
			context.writeBoolean((Boolean) obj.get());
		    }
		})
		.build();

	BinaryIO.get().registerAdapter(Root.class, ROOT_ADAPTER);
    }

    public IDBinaryAdapterNGTest() {

    }

    @Test
    public void test() throws FileNotFoundException, IOException {
	try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
	    SerializationContext context = new SerializationContext(out);
	    context.writeObject(new StringImpl("String!"));
	    context.writeObject(new NumberImpl(100));
	    context.writeObject(new BooleanImpl(false));
	}

	try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
	    DeserializationContext context = new DeserializationContext(in);
	    System.out.println(context.readObject(Root.class));
	    System.out.println(context.readObject(Root.class));
	    System.out.println(context.readObject(Root.class));
	}
    }

    private static abstract class Root {

	private final Object value;

	protected Root(Object value) {
	    this.value = value;
	}

	public Object get() {
	    return value;
	}

	@Override
	public String toString() {
	    return value.toString();
	}
    }

    private static class StringImpl extends Root {

	private StringImpl(String str) {
	    super(str);
	}
    }

    private static class NumberImpl extends Root {

	private NumberImpl(Number n) {
	    super(n);
	}
    }

    private static class BooleanImpl extends Root {

	private BooleanImpl(Boolean bool) {
	    super(bool);
	}
    }
}

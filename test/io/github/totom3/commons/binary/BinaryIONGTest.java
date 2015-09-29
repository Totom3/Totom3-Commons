package io.github.totom3.commons.binary;

import com.google.common.collect.ImmutableMap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author Totom3
 */
public class BinaryIONGTest {

    private final static File file;
    private final static Adapter<Comparable> comparableAdapter = new Adapter<>("Comparable");

    static {
	file = new File("./binaryIO.txt");

	file.getParentFile().mkdirs();
	try {
	    file.createNewFile();
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}

	BinaryIO.get()
		.registerAdapter(Double.class, new Adapter<>("Double"))
		.registerAdapter(Number.class, new Adapter<>("Number"))
		.registerAdapter(Comparable.class, comparableAdapter);

    }

    @DataProvider
    public static String[][] strings() {
	return new String[][]{
	    new String[]{"Test!"},
	    new String[]{"Another string!"},
	    new String[]{"Third string!"},
	    new String[]{null},
	    new String[]{""}
	};
    }

    @DataProvider
    public static TheEnum[][] enums() {
	return new TheEnum[][]{
	    new TheEnum[]{TheEnum.A},
	    new TheEnum[]{TheEnum.B},
	    new TheEnum[]{TheEnum.C},
	    new TheEnum[]{TheEnum.D},
	    new TheEnum[]{TheEnum.E}
	};
    }

    @DataProvider
    public static List<String>[][] lists() {
	return new List[][]{
	    new List[]{Arrays.asList("lol")},
	    new List[]{Arrays.asList("Teest!", "Another sfstring")},
	    new List[]{Arrays.asList("a FIfth stAwehuff", "A thiefefrd String!")},
	    new List[]{Arrays.asList()},};
    }

    @DataProvider
    public static Map[][] maps() {
	return new Map[][]{
	    new Map[]{ImmutableMap.of()},
	    new Map[]{ImmutableMap.of("Key!", 5)},
	    new Map[]{ImmutableMap.of("Another key", 10, "I'm THE other key", 0, "It's me!", 5, "I have the higher score!", 2000)}
	};
    }

    public BinaryIONGTest() {
    }

    private DataOutputStream getOut() throws FileNotFoundException {
	return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    }

    private DataInputStream getIn() throws FileNotFoundException {
	return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

    @Test(dataProvider = "strings")
    public void testWriteAndReadString(String str) throws Exception {
	System.out.println("TestWriteAndRead " + str);
	try (DataOutputStream out = getOut()) {

	    new SerializationContext(out).writeString(str);
	}

	String str2;
	try (DataInputStream in = getIn()) {
	    str2 = new DeserializationContext(in).readString();
	}

	assertEquals(str2, str);
    }

    @Test
    public void testGetReadAdapter() {
	System.out.println("TestReadAdapter");
	BinaryAdapter<UnknownNumber> adapter = BinaryIO.get().getReadAdapter(UnknownNumber.class);
	assertNull(adapter);
    }

    @Test
    public void testGetWriteAdapter() {
	System.out.println("TestReadAdapter");
	BinaryAdapter<? super SomethingComparable> adapter = BinaryIO.get().getWriteAdapter(SomethingComparable.class);

	assertEquals(adapter, comparableAdapter);
    }

    @Test(dataProvider = "strings")
    public void testWriteAndReadObject(String str) throws Exception {
	System.out.println("TestWriteAndReadObject");

	try (DataOutputStream out = getOut()) {
	    BinaryIO.get().writeObject(str, String.class, new SerializationContext(out));
	}

	String str2;
	try (DataInputStream in = getIn()) {
	    str2 = BinaryIO.get().readObject(String.class, new DeserializationContext(in));
	}

	assertEquals(str2, str);
    }

    @Test(dataProvider = "enums")
    public void testWriteAndReadEnum(TheEnum e) throws Exception {
	System.out.println("TestWriteAndWriteEnum");

	try (DataOutputStream out = getOut()) {
	    BinaryIO.get().writeEnum(e, new SerializationContext(out));
	}

	TheEnum e2;
	try (DataInputStream in = getIn()) {
	    e2 = BinaryIO.get().readEnum(TheEnum.class, new DeserializationContext(in));
	}

	assertEquals(e2, e);
    }

    @Test(dataProvider = "lists")
    public void testWriteAndReadCollection(List<String> list) throws Exception {
	System.out.println("TestWriteAndReadCollection " + list);

	try (DataOutputStream out = getOut()) {
	    BinaryIO.get().writeCollection(list, new SerializationContext(out));
	}

	List<?> list2;
	try (DataInputStream in = getIn()) {
	    list2 = BinaryIO.get().readList(String.class, new DeserializationContext(in));
	}

	assertEquals(list2, list);
    }

    @Test(dataProvider = "maps")
    public void testWriteAndReadMap(Map map) throws Exception {
	System.out.println("TestWriteAndReadMap " + map);

	try (DataOutputStream out = getOut()) {
	    BinaryIO.get().writeMap(map, new SerializationContext(out));
	}

	Map<String, Integer> map2;
	try (DataInputStream in = getIn()) {
	    map2 = BinaryIO.get().readMap(String.class, Integer.class, new DeserializationContext(in));
	}

	assertEquals(map2, map);
    }

    private static class Adapter<T> implements BinaryAdapter<T> {

	private final String name;

	Adapter(String name) {
	    this.name = name;
	}

	@Override
	public T read(DeserializationContext context) throws IOException {
	    throw new UnsupportedOperationException("Operation not supported.");
	}

	@Override
	public void write(T obj, SerializationContext context) throws IOException {
	    throw new UnsupportedOperationException("Operation not supported.");
	}

	@Override
	public String toString() {
	    return "Adapter{" + "name=" + name + '}';
	}
    }

    private static class UnknownNumber extends Number {

	private static final long serialVersionUID = 1L;

	@Override
	public int intValue() {
	    return 1;
	}

	@Override
	public long longValue() {
	    return 1;
	}

	@Override
	public float floatValue() {
	    return 1;
	}

	@Override
	public double doubleValue() {
	    return 1;
	}

    }

    private static class SomethingComparable implements Comparable<Object> {

	@Override
	public int compareTo(Object o) {
	    throw new UnsupportedOperationException("Operation not supported.");
	}

    }

    public static enum TheEnum {

	A,
	B,
	C,
	D,
	E;
    }
}

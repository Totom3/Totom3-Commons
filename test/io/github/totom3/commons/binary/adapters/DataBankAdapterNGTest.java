package io.github.totom3.commons.binary.adapters;

import io.github.totom3.commons.binary.BinaryIO;
import io.github.totom3.commons.binary.DataBankAdapter;
import io.github.totom3.commons.binary.DeserializationContext;
import io.github.totom3.commons.binary.SerializationContext;
import io.github.totom3.commons.bukkit.AbstractLocation;
import static io.github.totom3.commons.bukkit.AbstractLocation.create;
import io.github.totom3.commons.misc.DataBank;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author Totom3
 */
public class DataBankAdapterNGTest {

    private final static TheBank bank = new TheBank();
    private final static File file = new File("C:\\Users\\Totom3\\Desktop\\Minecraft Plugins\\Doors\\dist\\bank.txt");

    static {
	bank.getOrInsert(create(10, 20, 30));
	bank.getOrInsert(create(-1, -2, 3));
	bank.getOrInsert(create(1, 2, 3));
	bank.getOrInsert(create(10, 0, -10));
	try {
	    file.getParentFile().mkdirs();
	    file.createNewFile();
	} catch (IOException ex) {
	    throw new RuntimeException();
	}

	BinaryIO.get().registerAdapter(TheBank.class, new TheBankAdapter());
    }

    @DataProvider
    public static Object[][] bank() {
	return new Object[][]{
	    new Object[]{bank}
	};
    }

    @Test(dataProvider = "bank")
    public void testWriteAndRead(DataBank<String> bank) throws Exception {
	TheBank newBank;

	System.out.println("Test write & read");
	System.out.println("WRITE");
	try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
	    SerializationContext context = new SerializationContext(out);
	    context.writeObject(bank);
	    out.flush();
	}

	System.out.println("READ");
	try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
	    DeserializationContext context = new DeserializationContext(in);
	    newBank = context.readObject(TheBank.class);
	}

	Assert.assertEquals(newBank, bank);
    }

    static class TheBankAdapter extends DataBankAdapter<AbstractLocation, TheBank> {

	@Override
	protected TheBank supply() {
	    return new TheBank();
	}

	@Override
	protected void writeElement(AbstractLocation obj, SerializationContext context) throws IOException {
	    context.writeDouble(obj.getX());
	    context.writeDouble(obj.getY());
	    context.writeDouble(obj.getZ());
	}

	@Override
	protected AbstractLocation readElement(DeserializationContext context) throws IOException {
	    return create(context.readDouble(), context.readDouble(), context.readDouble());
	}
    }

    static class TheBank extends DataBank<AbstractLocation> {

    }
}

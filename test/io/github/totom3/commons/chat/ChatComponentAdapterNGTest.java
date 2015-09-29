package io.github.totom3.commons.chat;

import io.github.totom3.commons.binary.DeserializationContext;
import io.github.totom3.commons.binary.SerializationContext;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.bukkit.ChatColor;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author Totom3
 */
public class ChatComponentAdapterNGTest {

    private static final File file;

    static {
	file = new File(".\\ChatComponent.txt");
    }

    @DataProvider
    public static Object[][] chatComponents() {
	return new Object[][]{
	    new Object[]{new ChatComponent().setText("Test!")},
	    new Object[]{new ChatComponent().setText("Test2").setColor(ChatColor.BLUE)},
	    new Object[]{new ChatComponent().setText("Test3").setInsertion("Insertion").addFormat(ChatColor.MAGIC)},
	    new Object[]{new ChatComponent().setText("Parent").addChild(new ChatComponent().setText("Child"))}
	};
    }

    @Test(dataProvider = "chatComponents")
    public void testWriteAndRead(ChatComponent comp) throws IOException {
	try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
	    SerializationContext context = new SerializationContext(out);
	    context.writeObject(comp);
	}

	ChatComponent comp2;

	try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
	    DeserializationContext context = new DeserializationContext(in);

	    comp2 = context.readObject(ChatComponent.class);
	}

	assertEquals(comp2, comp);
    }

}

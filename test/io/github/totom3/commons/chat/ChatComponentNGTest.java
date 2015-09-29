package io.github.totom3.commons.chat;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.ChatColor;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author Totom3
 */
public class ChatComponentNGTest {
    
    @DataProvider(name = "chatComponents")
    public static Object[][] chatComponents() {
	return new Object[][] {
	    new Object[] { new ChatComponent().setItalic(true) },
	    new Object[] { new ChatComponent().setText("This is a sample text!") },
	    new Object[] { new ChatComponent().setText("Test!").setItalic(true) },
	    new Object[] { new ChatComponent().setText("Child test! ").setColor(ChatColor.YELLOW).addChild(new ChatComponent().setText("I'm a child!").setColor(ChatColor.GREEN)).addChild(new ChatComponent().setText("SomeText!")) },
	};
    }
    
    @DataProvider(name = "modifiers")
    public static Object[][] modifiers() {
	return new Object[][] {
	    new Object[] { ChatComponent.BOLD },
	    new Object[] { ChatComponent.ITALIC },
	    new Object[] { ChatComponent.UNDERLINED },
	    new Object[] { ChatComponent.STRIKE_THROUGH },
	    new Object[] { ChatComponent.OBFUSCATED }
	};
    }
    
    @Test(dataProvider = "modifiers")
    public void testModifiers(Byte modif) {
	System.out.println("Test modifier "+Integer.toBinaryString(modif));
	ChatComponent comp = new ChatComponent();
	comp.setFlag(modif, true);
	Boolean modifier = comp.getModifier(modif);	
	AssertJUnit.assertEquals(Boolean.TRUE, modifier);
    }
    
    @Test(dataProvider = "chatComponents")
    public void testFromAndToJson(ChatComponent comp) {
	System.out.println("From and to json");
	String serialized = ChatComponent.toJson(comp);
	ChatComponent comp2 = ChatComponent.fromJson(serialized);
	
	AssertJUnit.assertEquals(comp, comp2);
    }
    
    @Test(dataProvider = "chatComponents")
    public void testFromAndToNMS(ChatComponent comp) {
	System.out.println("From and to NMS");
	IChatBaseComponent nmsComp = comp.toNMS();
	ChatComponent comp2 = ChatComponent.fromNMS(nmsComp);
	
	AssertJUnit.assertEquals(comp, comp2);
    }
    
    @Test(dataProvider = "chatComponents")
    public void testFromAndToPlainText(ChatComponent comp) {
	System.out.println("From and to plain text");
	String plain = comp.toPlainText();
	System.out.println("Plain text: "+plain);
	ChatComponent comp2 = ChatComponent.fromPlainText(plain);
	
	AssertJUnit.assertEquals(comp, comp2);
    }
}

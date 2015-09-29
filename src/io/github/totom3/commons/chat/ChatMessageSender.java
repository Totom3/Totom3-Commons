package io.github.totom3.commons.chat;

import com.darkblade12.particledemo.particle.ReflectionUtils;
import com.google.common.base.Preconditions;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.amoebaman.amoebautils.nms.ReflectionUtil;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Totom3
 */
public final class ChatMessageSender {

    public static void send(ChatComponent comp, Player player) {
	send(comp, player, ChatMessageType.COMMAND);
    }

    public static void send(ChatComponent comp, Player player, ChatMessageType type) {

	Preconditions.checkNotNull(player);
	Preconditions.checkNotNull(type);
	if (comp == null) {
	    comp = new ChatComponent();
	}

	PacketPlayOutChat packet = new PacketPlayOutChat(comp.toNMS());

	try {
	    Object handle = ReflectionUtil.getHandle(player);
	    Object connection = ReflectionUtils.getValue(handle, false, "playerConnection");
	    ReflectionUtils.invokeMethod(connection, "sendPacket", packet);
	} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException ex) {
	    Logger.getLogger(ChatMessageSender.class.getName()).log(Level.SEVERE, "Could not send chat packet to player " + player.getName(), ex);
	}
    }

    public static void send(ChatComponent comp, CommandSender sender) {
	if (sender instanceof Player) {
	    send(comp, (Player) sender);
	} else{
	    sender.sendMessage(comp.toPlainText());
	}
	
    }

    private ChatMessageSender() {
    }

    public static enum ChatMessageType {

	CHAT,
	COMMAND,
	ACTION_BAR
    }
}

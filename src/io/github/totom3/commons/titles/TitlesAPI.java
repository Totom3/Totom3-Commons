package io.github.totom3.commons.titles;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import com.comphenix.packetwrapper.WrapperPlayServerTitle;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TitlesAPI implements Listener {

    public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title) {
	sendTimings(player, fadeIn, stay, fadeOut);
	sendTitle(player, title);
    }

    public static void sendSubtitle(Player player, int fadeIn, int stay, int fadeOut, String subtitle) {
	sendTimings(player, fadeIn, stay, fadeOut);
	sendSubTitle(player, subtitle);
    }

    public static void sendAll(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
	sendTimings(player, fadeIn, stay, fadeOut);
	sendTitle(player, title);
	sendSubTitle(player, subtitle);
    }

    public static void sendTimings(Player player, int fadeIn, int stay, int fadeOut) {
	WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
	wrapper.setAction(TitleAction.TIMES);
	wrapper.setFadeIn(fadeIn);
	wrapper.setFadeOut(fadeOut);
	wrapper.setStay(stay);
	wrapper.sendPacket(player);
    }

    public static void sendTitles(Player player, String title, String subtitle) {
	sendTitle(player, title);
	sendSubTitle(player, subtitle);
    }

    public static void sendTitle(Player player, String title) {
	WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
	wrapper.setAction(TitleAction.TITLE);
	wrapper.setTitle(WrappedChatComponent.fromText(title));
	wrapper.sendPacket(player);
    }

    public static void sendSubTitle(Player player, String subtitle) {
	WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
	wrapper.setAction(TitleAction.SUBTITLE);
	wrapper.setTitle(WrappedChatComponent.fromText(subtitle));
	wrapper.sendPacket(player);
    }

    public static void sendTabTitle(Player player, String header, String footer) {
	WrapperPlayServerPlayerListHeaderFooter wrapper = new WrapperPlayServerPlayerListHeaderFooter();
	wrapper.setHeader(WrappedChatComponent.fromText(header));
	wrapper.setFooter(WrappedChatComponent.fromText(footer));
	wrapper.sendPacket(player);
    }

    public static void clearTitle(Player player) {
	WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
	wrapper.setAction(TitleAction.CLEAR);
	wrapper.sendPacket(player);
    }

    public static void resetTitle(Player player) {
	WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle();
	wrapper.setAction(TitleAction.RESET);
	wrapper.sendPacket(player);
    }

    private TitlesAPI() {
    }
}

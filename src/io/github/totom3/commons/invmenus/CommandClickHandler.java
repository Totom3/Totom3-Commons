package io.github.totom3.commons.invmenus;

import org.bukkit.Bukkit;

/**
 *
 * @author Totom3
 */
public class CommandClickHandler implements ClickHandler {

    private final String command;

    public CommandClickHandler(String command) {
	this.command = command;
    }

    @Override
    public void onClick(MenuClickEvent event) {
	Bukkit.dispatchCommand(event.getPlayer(), command);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.totom3.commons.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Totom3
 */
public class UnsupportedCommandExecutor implements CommandExecutor {

    private String reason;

    public UnsupportedCommandExecutor(String reason) {
	this.reason = reason;
    }

    public String getReason() {
	return reason;
    }

    public void setReason(String reason) {
	this.reason = reason;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
	String r = reason;
	
	if (StringUtils.isBlank(r)) {
	    cs.sendMessage(ChatColor.RED+"Command not supported!");
	} else {
	    cs.sendMessage(ChatColor.RED+"Command not supported: "+r);
	}
	
	return true;
    }
    
}

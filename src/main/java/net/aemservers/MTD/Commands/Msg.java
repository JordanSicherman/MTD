package main.java.net.aemservers.MTD.Commands;

import main.java.net.aemservers.MTD.MTD;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Msg {

	public static void send(CommandSender p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', MTD.instance.getConfig().getString(msg)));
	}

	public static String format(String msg) {
		return "* " + ChatColor.translateAlternateColorCodes('&', MTD.instance.getConfig().getString(msg));
	}
}

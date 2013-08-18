package main.java.net.aemservers.MTD.Commands;

import main.java.net.aemservers.MTD.MTD;
import main.java.net.aemservers.MTD.Automation.Game;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Games implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (Game g : MTD.instance.getMaps())
			sender.sendMessage("* " + g.getName());
		return true;
	}

}

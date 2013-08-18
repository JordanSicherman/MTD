package main.java.net.aemservers.MTD.Commands;

import main.java.net.aemservers.MTD.Automation.Game;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Quit implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Game g = Game.getGame((Player) sender);
		if (g == null) {
			Msg.send(sender, "error.not_in_game");
			return true;
		}
		g.quit((Player) sender);
		return true;
	}

}

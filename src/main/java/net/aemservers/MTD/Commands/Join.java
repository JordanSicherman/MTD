package main.java.net.aemservers.MTD.Commands;

import main.java.net.aemservers.MTD.Automation.Game;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Join implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0 || !(sender instanceof Player))
			return false;
		String name = "";
		for (String s : args)
			name += s + " ";
		name = name.trim();
		Game g = Game.getGame(name);
		if (g == null) {
			Msg.send(sender, "error.no_game");
			return true;
		}
		if (!g.join((Player) sender))
			Msg.send(sender, "error.unable_to_join");
		return true;
	}

}

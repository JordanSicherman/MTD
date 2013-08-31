package main.java.net.aemservers.MTD.Commands;

import java.util.ArrayList;
import java.util.List;

import main.java.net.aemservers.MTD.MTD;
import main.java.net.aemservers.MTD.Automation.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class Join implements CommandExecutor, TabCompleter {

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

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> games = new ArrayList<String>();
		for (Game g : MTD.instance.getMaps()) {
			games.add(g.getName());
		}
		return tabComplete(args, games);
	}

	private List<String> tabComplete(String[] args, List<String> possibilities) {
		String arg = args[args.length - 1];

		List<String> completions = new ArrayList<String>();

		for (String foundString : possibilities) {
			if (foundString != null)
				if (arg == null || foundString.regionMatches(true, 0, arg, 0, arg.length())) {
					completions.add(foundString);
				}
		}

		return completions;
	}
}

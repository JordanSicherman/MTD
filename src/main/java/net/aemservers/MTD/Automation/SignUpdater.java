package main.java.net.aemservers.MTD.Automation;

import main.java.net.aemservers.MTD.MTD;
import main.java.net.aemservers.MTD.MobDisguise;
import main.java.net.aemservers.MTD.Automation.Game.Gamestate;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

public class SignUpdater {

	public static void update() {
		for (Game g : MTD.instance.getMaps())
			for (Sign s : g.getSigns()) {
				if (s.getLine(0) != null && s.getLine(0).equals("[MTD]")) {
					s.setLine(0, ChatColor.translateAlternateColorCodes('&', s.getLine(1)));
					s.setLine(1, "");
				}
				if (g.getState() == Gamestate.STARTED) {
					s.setLine(2, MobDisguise.alive(g.getPlayers()).size() + ":" + MobDisguise.dead(g.getPlayers()).size());
					s.setLine(3, ChatColor.YELLOW + "In progress.");
				} else if (g.getState() == Gamestate.WAITING) {
					s.setLine(2, g.getPlayers().size() + " players");
					s.setLine(3, ChatColor.YELLOW + "Waiting...");
				} else if (g.getState() == Gamestate.BUILDING) {
					s.setLine(2, MobDisguise.alive(g.getPlayers()).size() + ":" + MobDisguise.dead(g.getPlayers()).size());
					s.setLine(3, ChatColor.YELLOW + "Fortifying...");
				}
				s.update();
			}
	}
}

package main.java.net.aemservers.MTD.Listeners;

import main.java.net.aemservers.MTD.Automation.Game;
import main.java.net.aemservers.MTD.Automation.Game.Gamestate;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class LeaveDuringGame implements Listener {

	@EventHandler
	private void onSpawn(PlayerCommandPreprocessEvent e) {
		Game g = Game.getGame(e.getPlayer());
		if (g != null && g.getState() != Gamestate.ENDING)
			if (e.getMessage().toLowerCase().startsWith("/spawn")) {
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot do that while you're in a game.");
				e.setCancelled(true);
			}
	}

}

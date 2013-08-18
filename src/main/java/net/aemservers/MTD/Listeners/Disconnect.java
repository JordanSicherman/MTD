package main.java.net.aemservers.MTD.Listeners;

import main.java.net.aemservers.MTD.Automation.Game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Disconnect implements Listener {

	@EventHandler
	private void onDC(PlayerQuitEvent e) {
		Game g;
		if ((g = Game.getGame(e.getPlayer())) != null)
			g.quit(e.getPlayer());
	}
}

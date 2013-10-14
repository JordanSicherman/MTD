package main.java.net.teepee.MTD.Listeners;

import main.java.net.teepee.MTD.Automation.Game;
import main.java.net.teepee.MTD.Automation.Teleport;
import main.java.net.teepee.MTD.Automation.Game.Gamestate;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Movement implements Listener {

	@EventHandler
	private void onMoveBlock(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Game g = Game.getGame(p);
		if (g != null && g.isAlive(p) && g.getState() == Gamestate.STARTED) {
			/*
			 * Prevent archer players from stepping off their block except for falling and going up.
			 */
			if (!e.getFrom().getBlock().equals(e.getTo().getBlock())
					&& !e.getFrom().getBlock().getRelative(BlockFace.DOWN).equals(e.getTo().getBlock())
					&& !e.getFrom().getBlock().getRelative(BlockFace.UP).equals(e.getTo().getBlock()))
				e.setTo(e.getFrom());
		} else if (g != null && !g.isAlive(p) && g.getState() == Gamestate.STARTED)
			// Check finish lines crosses
			if (g.getFinishLine().didCross(e)) {
				Teleport.teleport(p, g.getMobSpawn(), true);
				if (g.getCrosses() != null) {
					g.getCrosses().setScore(g.getCrosses().getScore() - 1);
					g.stop(false);
				}
			}
		if (g != null && !g.isAlive(p) && g.getState() == Gamestate.BUILDING)
			/*
			 * Prevent mobs from stepping off their block while archers are building except for vertical.
			 */
			if (!e.getFrom().getBlock().equals(e.getTo().getBlock())
					&& !e.getFrom().getBlock().getRelative(BlockFace.DOWN).equals(e.getTo().getBlock())
					&& !e.getFrom().getBlock().getRelative(BlockFace.UP).equals(e.getTo().getBlock()))
				e.setTo(e.getFrom());
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent e) {
		if (Game.inGame(e.getPlayer()))
			e.setCancelled(true);
	}
}

package main.java.net.teepee.MTD.Listeners;

import main.java.net.teepee.MTD.Automation.Game;
import main.java.net.teepee.MTD.Automation.Game.Gamestate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PreStart implements Listener {

	@EventHandler
	private void onBreak(BlockBreakEvent e) {
		Game g;
		if ((g = Game.getGame(e.getPlayer())) != null
				&& (!g.hasStarted() || !g.isAlive(e.getPlayer()) && g.getState() == Gamestate.BUILDING || g.isAlive(e.getPlayer())
						&& g.getState() == Gamestate.STARTED))
			e.setCancelled(true);
	}

	@EventHandler
	private void onMobTarget(EntityTargetEvent e) {
		// To prevent creepers from targetting/exploding mainly.
		if (e.getTarget() instanceof Player) {
			Game g;
			if ((g = Game.getGame((Player) e.getTarget())) != null && !g.hasStarted())
				e.setCancelled(true);
		}
	}

	@EventHandler
	private void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Game g;
			if ((g = Game.getGame((Player) e.getEntity())) != null)
				if (g.getState() != Gamestate.STARTED)
					e.setCancelled(true);
		}
	}

	@EventHandler
	private void onDamageEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Game g;
			if ((g = Game.getGame((Player) e.getEntity())) != null)
				if (g.getState() != Gamestate.STARTED)
					e.setCancelled(true);
		}
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		Game g;
		if ((g = Game.getGame(e.getPlayer())) != null)
			if (!g.hasStarted() || !g.isAlive(e.getPlayer()) && g.getState() == Gamestate.BUILDING)
				e.setCancelled(true);
	}

	@EventHandler
	private void onInteractEntity(PlayerInteractEntityEvent e) {
		Game g;
		if ((g = Game.getGame(e.getPlayer())) != null)
			if (!g.hasStarted() || !g.isAlive(e.getPlayer()) && g.getState() == Gamestate.BUILDING)
				e.setCancelled(true);
	}
}

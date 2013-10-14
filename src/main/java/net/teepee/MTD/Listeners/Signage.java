package main.java.net.teepee.MTD.Listeners;

import main.java.net.teepee.MTD.MTD;
import main.java.net.teepee.MTD.Automation.Game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Signage implements Listener {

	@EventHandler
	private void onCreate(SignChangeEvent e) {
		if (!e.getPlayer().hasPermission("MTD.sign"))
			return;
		if (e.getLine(0) != null && e.getLine(0).equals("[MTD]"))
			for (Game g : MTD.instance.getMaps())
				if (g.getName().equals(e.getLine(1)) && !g.getSigns().contains(e.getBlock().getState())) {
					MTD.debug("Sign created in game " + g.getName());
					g.addSign((Sign) e.getBlock().getState());
				}
	}

	@EventHandler
	private void onBreak(BlockBreakEvent e) {
		if (!e.getPlayer().hasPermission("MTD.sign"))
			return;
		Material m = e.getBlock().getType();
		if (m == Material.WALL_SIGN || m == Material.SIGN)
			for (Game g : MTD.instance.getMaps())
				if (g.getSigns().contains(e.getBlock().getState())) {
					MTD.debug("Sign removed in game " + g.getName());
					g.removeSign((Sign) e.getBlock().getState());
				}
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		if (Game.inGame(e.getPlayer()))
			return;

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material m = e.getClickedBlock().getType();
			if (m == Material.WALL_SIGN || m == Material.SIGN)
				for (Game g : MTD.instance.getMaps())
					if (g.getSigns().contains(e.getClickedBlock().getState())) {
						MTD.debug("Sign interacted with in game " + g.getName());
						Bukkit.dispatchCommand(e.getPlayer(), "join " + g.getName());
					}
		}
	}
}

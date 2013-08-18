package main.java.orange451.pvpgunplus.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.net.aemservers.MTD.MTD;
import main.java.orange451.pvpgunplus.events.PVPGunPlusGunDamageEntityEvent;
import main.java.orange451.pvpgunplus.gun.Gun;
import main.java.orange451.pvpgunplus.gun.GunPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PluginPlayerListener implements Listener {
	private final MTD plugin;
	List<String> aliases = Arrays.asList(new String[] { "guns" });

	public PluginPlayerListener(MTD plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getPVPGuns().onJoin(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.getPVPGuns().onQuit(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBulletHit(PVPGunPlusGunDamageEntityEvent event) {
		Entity blood = event.getEntityDamaged();
		blood.getWorld().playEffect(blood.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
		blood.getWorld().playEffect(blood.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Item dropped = event.getItemDrop();
		Player dropper = event.getPlayer();
		GunPlayer gp = plugin.getPVPGuns().getGunPlayer(dropper);
		if (gp != null) {
			ItemStack lastHold = gp.getLastItemHeld();
			if (lastHold != null) {
				Gun gun = gp.getGun(dropped.getItemStack().getTypeId());
				if (gun != null && lastHold.equals(dropped.getItemStack()) && gun.hasClip && gun.changed && gun.reloadGunOnDrop) {
					gun.reloadGun();
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack itm1 = player.getItemInHand();
		if (itm1 != null
				&& (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR) || action
						.equals(Action.RIGHT_CLICK_BLOCK))) {
			String clickType = "left";
			if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))
				clickType = "right";
			GunPlayer gp = plugin.getPVPGuns().getGunPlayer(player);
			if (gp != null && gp.onClick(clickType))
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		split[0] = split[0].substring(1);
		String label = split[0];
		String[] args = new String[split.length - 1];
		for (int i = 1; i < split.length; i++)
			args[i - 1] = split[i];
		if (aliases.contains(label.toLowerCase()))
			event.setCancelled(true);

		if (aliases.contains(label.toLowerCase()) && args.length == 0) {
			player.sendMessage(ChatColor.DARK_GRAY + "----" + ChatColor.GRAY + "[" + ChatColor.YELLOW + "PvPGuns++" + ChatColor.GRAY + "]"
					+ ChatColor.DARK_GRAY + "----");

			player.sendMessage(ChatColor.GRAY + "/" + label.toLowerCase() + " " + ChatColor.GREEN + "reload" + ChatColor.WHITE
					+ " to reload.");

			player.sendMessage(ChatColor.GRAY + "/" + label.toLowerCase() + " " + ChatColor.GREEN + "list" + ChatColor.WHITE
					+ " to list the guns available.");
		}
		try {
			if (aliases.contains(label.toLowerCase()) && args[0].equals("reload") && player.hasPermission("pvpgunplus.reload")) {
				plugin.getPVPGuns().reload(true);
				player.sendMessage("PVPGuns++ was reloaded.");
			}

			if (aliases.contains(label.toLowerCase()) && args[0].equals("list")) {
				player.sendMessage("------- PvPGuns++ -------");
				player.sendMessage("");

				ArrayList<Gun> loadedGuns = plugin.getPVPGuns().getLoadedGuns();

				for (int i = 0; i < loadedGuns.size(); i++) {
					Gun g = loadedGuns.get(i);
					player.sendMessage(" - " + g.getName() + ChatColor.YELLOW + " ("
							+ g.getGunMaterial().toString().toLowerCase().replaceAll("_", " ") + ")" + ChatColor.GRAY + " ammo: "
							+ ChatColor.RED + g.getAmmoMaterial().toString().toLowerCase().replaceAll("_", " ") + ChatColor.GRAY + " x"
							+ ChatColor.RED + Integer.toString(g.getAmmoAmtNeeded()));
				}

				player.sendMessage("");
				player.sendMessage("-------------------------");
			}
		} catch (Exception localException) {
		}
	}
}
package main.java.orange451.pvpgunplus.gun;

import java.util.ArrayList;

import main.java.net.teepee.MTD.MTD;
import main.java.orange451.pvpgunplus.InventoryHelper;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GunPlayer {
	private Player controller;
	private ItemStack lastHeldItem;
	private final ArrayList<Gun> guns;
	private Gun currentlyFiring;
	public boolean enabled = true;

	public GunPlayer(MTD plugin, Player player) {
		controller = player;
		guns = plugin.getPVPGuns().getLoadedGuns();
		for (int i = 0; i < guns.size(); i++)
			guns.get(i).owner = this;
	}

	public boolean isAimedIn() {
		if (controller == null)
			return false;
		if (!controller.isOnline())
			return false;
		return controller.hasPotionEffect(PotionEffectType.SLOW);
	}

	public boolean onClick(String clickType) {
		if (!enabled)
			return false;
		Gun holding = null;
		ItemStack hand = controller.getItemInHand();
		if (hand != null) {
			ArrayList<Gun> tempgun = getGunsByType(hand);
			ArrayList<Gun> canFire = new ArrayList<Gun>();
			for (int i = 0; i < tempgun.size(); i++)
				if (controller.hasPermission(tempgun.get(i).node) || !tempgun.get(i).needsPermission)
					canFire.add(tempgun.get(i));
			if (tempgun.size() > canFire.size() && canFire.size() == 0) {
				if (tempgun.get(0).permissionMessage != null && tempgun.get(0).permissionMessage.length() > 0)
					controller.sendMessage(tempgun.get(0).permissionMessage);
				return false;
			}
			tempgun.clear();
			for (int i = 0; i < canFire.size(); i++) {
				Gun check = canFire.get(i);
				byte gunDat = check.getGunTypeByte();
				byte itmDat = hand.getData().getData();

				if (gunDat == itmDat || check.ignoreItemData)
					holding = check;
			}
			canFire.clear();
		}
		if (holding != null) {
			if ((holding.canClickRight || holding.canAimRight()) && clickType.equals("right")) {
				if (!holding.canAimRight()) {
					holding.heldDownTicks += 1;
					holding.lastFired = 0;
					if (currentlyFiring == null)
						fireGun(holding);
				} else
					checkAim();
				return true;
			}
			if ((holding.canClickLeft || holding.canAimLeft()) && clickType.equals("left")) {
				if (!holding.canAimLeft()) {
					holding.heldDownTicks = 0;
					if (currentlyFiring == null)
						fireGun(holding);
				} else
					checkAim();
				return true;
			}
		}
		return false;
	}

	protected void checkAim() {
		if (isAimedIn())
			controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 0), true);
		else
			controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 12000, 4), true);
	}

	private void fireGun(Gun gun) {
		if (controller.hasPermission(gun.node) || !gun.needsPermission) {
			if (gun.timer <= 0) {
				currentlyFiring = gun;
				gun.firing = true;
			}
		} else if (gun.permissionMessage != null && gun.permissionMessage.length() > 0)
			controller.sendMessage(gun.permissionMessage);
	}

	public void tick() {
		if (controller != null) {
			ItemStack hand = controller.getItemInHand();
			lastHeldItem = hand;

			for (int i = guns.size() - 1; i >= 0; i--) {
				Gun g = guns.get(i);
				if (g != null) {
					g.tick();

					if (controller.isDead())
						g.finishReloading();

					if (hand != null && g.getGunType() == hand.getTypeId() && isAimedIn() && !g.canAimLeft() && !g.canAimRight())
						controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 0), true);

					if (currentlyFiring != null && g.timer <= 0 && currentlyFiring.equals(g))
						currentlyFiring = null;
				}
			}
		}
		renameGuns(controller);
	}

	protected void renameGuns(Player p) {
		Inventory inv = p.getInventory();
		ItemStack[] items = inv.getContents();
		for (ItemStack item : items)
			if (item != null) {
				String name = getGunName(item);
				if (name != null && name.length() > 0)
					setName(item, name);
			}
	}

	protected ArrayList<Gun> getGunsByType(ItemStack item) {
		ArrayList<Gun> ret = new ArrayList<Gun>();
		for (int i = 0; i < guns.size(); i++)
			if (guns.get(i).getGunMaterial().equals(item.getType()))
				ret.add(guns.get(i));

		return ret;
	}

	protected String getGunName(ItemStack item) {
		ArrayList<Gun> tempgun = getGunsByType(item);
		int amtGun = tempgun.size();
		if (amtGun > 0)
			for (int i = 0; i < tempgun.size(); i++)
				if (controller.hasPermission(tempgun.get(i).node) || !tempgun.get(i).needsPermission) {
					Gun current = tempgun.get(i);
					if (current.getGunMaterial() != null && current.getGunMaterial().getId() == item.getTypeId()) {
						byte gunDat = tempgun.get(i).getGunTypeByte();
						byte itmDat = item.getData().getData();

						if (gunDat == itmDat || tempgun.get(i).ignoreItemData)
							return getGunName(current);
					}
				}
		return "";
	}

	private String getGunName(Gun current) {
		String add = "";
		String refresh = "";
		if (current.hasClip) {
			int leftInClip = 0;
			int ammoLeft = 0;
			int maxInClip = current.maxClipSize;

			int currentAmmo = (int) Math.floor(InventoryHelper.amtItem(controller.getInventory(), current.getAmmoType(),
					current.getAmmoTypeByte())
					/ current.getAmmoAmtNeeded());

			ammoLeft = currentAmmo - maxInClip + current.roundsFired;
			if (ammoLeft < 0)
				ammoLeft = 0;
			leftInClip = currentAmmo - ammoLeft;
			String bullets = new StringBuilder().append("     ").append(ChatColor.GRAY).toString();
			for (int i = 0; i < leftInClip; i++)
				bullets = new StringBuilder().append(bullets).append("«").toString();
			bullets = new StringBuilder().append(bullets).append(ChatColor.BLACK).append("").toString();
			for (int i = 0; i < maxInClip - leftInClip; i++)
				bullets = new StringBuilder().append(bullets).append("-").toString();
			add = bullets;

			if (current.reloading) {
				int loaded = current.gunReloadTimer / (current.getReloadTime() / maxInClip);

				refresh = new StringBuilder().append(ChatColor.BLACK).append("").toString();
				for (int ii = 0; ii < loaded + 1; ii++)
					refresh = new StringBuilder().append(refresh).append("-").toString();
				refresh = new StringBuilder().append(refresh).append(ChatColor.RED).append("").toString();

				int amtloaded = 0;
				for (int ii = 0; ii < maxInClip - loaded - 1; ii++) {
					refresh = new StringBuilder().append(refresh).append(ammoLeft - amtloaded > 0 ? "«" : "-").toString();
					amtloaded++;
				}

				add = new StringBuilder().append("     ").append(refresh).toString();
			}
		}
		String name = current.getName();
		return new StringBuilder().append(name).append(add).toString();
	}

	protected ItemStack setName(ItemStack item, String name) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);

		return item;
	}

	public Player getPlayer() {
		return controller;
	}

	public void unload() {
		controller = null;
		currentlyFiring = null;
		for (int i = 0; i < guns.size(); i++)
			guns.get(i).clear();
	}

	public void reloadAllGuns() {
		for (int i = guns.size() - 1; i >= 0; i--) {
			Gun g = guns.get(i);
			if (g != null) {
				g.reloadGun();
				g.finishReloading();
			}
		}
	}

	public boolean checkAmmo(Gun gun, int amount) {
		return InventoryHelper.amtItem(controller.getInventory(), gun.getAmmoType(), gun.getAmmoTypeByte()) >= amount;
	}

	public void removeAmmo(Gun gun, int amount) {
		if (amount == 0)
			return;
		InventoryHelper.removeItem(controller.getInventory(), gun.getAmmoType(), gun.getAmmoTypeByte(), amount);
	}

	public ItemStack getLastItemHeld() {
		return lastHeldItem;
	}

	public Gun getGun(int materialId) {
		for (int i = guns.size() - 1; i >= 0; i--) {
			Gun check = guns.get(i);
			if (check.getGunType() == materialId)
				return check;
		}
		return null;
	}
}
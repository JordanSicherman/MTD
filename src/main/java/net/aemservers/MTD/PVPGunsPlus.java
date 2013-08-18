package main.java.net.aemservers.MTD;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import main.java.orange451.pvpgunplus.gun.Bullet;
import main.java.orange451.pvpgunplus.gun.EffectType;
import main.java.orange451.pvpgunplus.gun.Gun;
import main.java.orange451.pvpgunplus.gun.GunPlayer;
import main.java.orange451.pvpgunplus.gun.WeaponReader;
import main.java.orange451.pvpgunplus.listeners.PluginEntityListener;
import main.java.orange451.pvpgunplus.listeners.PluginPlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PVPGunsPlus {
	private class UpdateTimer implements Runnable {

		@Override
		public void run() {
			for (int i = players.size() - 1; i >= 0; i--) {
				GunPlayer gp = players.get(i);
				if (gp != null)
					gp.tick();
			}
			for (int i = bullets.size() - 1; i >= 0; i--) {
				Bullet t = bullets.get(i);
				if (t != null)
					t.tick();
			}
			for (int i = effects.size() - 1; i >= 0; i--) {
				EffectType eff = effects.get(i);
				if (eff != null)
					eff.tick();
			}
		}
	}

	public PluginPlayerListener playerListener;
	public PluginEntityListener entityListener;
	private final ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private final ArrayList<Gun> loadedGuns = new ArrayList<Gun>();
	private final ArrayList<GunPlayer> players = new ArrayList<GunPlayer>();
	private final ArrayList<EffectType> effects = new ArrayList<EffectType>();

	public Random random;

	public static Sound getSound(String gunSound) {
		String snd = gunSound.toUpperCase().replace(" ", "_");
		Sound sound = Sound.valueOf(snd);
		return sound;
	}

	public static void playEffect(Effect e, Location l, int num) {
		for (int i = 0; i < Bukkit.getServer().getOnlinePlayers().length; i++)
			Bukkit.getServer().getOnlinePlayers()[i].playEffect(l, e, num);
	}

	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

	public void addEffect(EffectType effectType) {
		effects.add(effectType);
	}

	public void doDisable() {
		clearMemory(true);
	}

	public void doEnable(MTD p) {
		p.getServer().getScheduler().runTaskTimer(p, new UpdateTimer(), 20L, 1L);
		startup(true);
	}

	public Bullet getBullet(Entity proj) {
		for (int i = bullets.size() - 1; i >= 0; i--)
			if (bullets.get(i).getProjectile().getEntityId() == proj.getEntityId())
				return bullets.get(i);
		return null;
	}

	public Gun getGun(int materialId) {
		for (int i = loadedGuns.size() - 1; i >= 0; i--)
			if (loadedGuns.get(i).getGunMaterial() != null && loadedGuns.get(i).getGunMaterial().getId() == materialId)
				return loadedGuns.get(i);

		return null;
	}

	public Gun getGun(String gunName) {
		for (int i = loadedGuns.size() - 1; i >= 0; i--)
			if (loadedGuns.get(i).getName().toLowerCase().equals(gunName) || loadedGuns.get(i).getFilename().toLowerCase().equals(gunName))
				return loadedGuns.get(i);

		return null;
	}

	public GunPlayer getGunPlayer(Player player) {
		for (int i = players.size() - 1; i >= 0; i--)
			if (players.get(i).getPlayer().equals(player))
				return players.get(i);
		return null;
	}

	public ArrayList<Gun> getGunsByType(ItemStack item) {
		ArrayList<Gun> ret = new ArrayList<Gun>();
		for (int i = 0; i < loadedGuns.size(); i++)
			if (loadedGuns.get(i).getGunMaterial().equals(item.getType()))
				ret.add(loadedGuns.get(i));
		return ret;
	}

	public ArrayList<Gun> getLoadedGuns() {
		ArrayList<Gun> ret = new ArrayList<Gun>();
		for (int i = loadedGuns.size() - 1; i >= 0; i--)
			ret.add(loadedGuns.get(i).copy());
		return ret;
	}

	public void onJoin(Player player) {
		if (getGunPlayer(player) == null) {
			GunPlayer gp = new GunPlayer(MTD.instance, player);
			players.add(gp);
		}
	}

	public void onQuit(Player player) {
		for (int i = players.size() - 1; i >= 0; i--)
			if (players.get(i).getPlayer().getName().equals(player.getName()))
				players.remove(i);
	}

	public void reload() {
		reload(false);
	}

	public void reload(boolean b) {
		clearMemory(b);
		startup(b);
	}

	public void removeBullet(Bullet bullet) {
		bullets.remove(bullet);
	}

	public void removeEffect(EffectType effectType) {
		effects.remove(effectType);
	}

	private String getMTDFolder() {
		return MTD.instance.getDataFolder().getAbsolutePath();
	}

	private void loadGuns() {
		String path = getMTDFolder() + "/guns";
		File dir = new File(path);
		String[] children = dir.list();
		if (children != null)
			for (String filename : children)
				if (filename.endsWith(".gun")) {
					WeaponReader f = new WeaponReader(MTD.instance, new File(path + "/" + filename), "gun");
					if (f.loaded) {
						f.ret.node = "pvpgunplus." + filename.toLowerCase();
						loadedGuns.add(f.ret);
						MTD.instance.getLogger().info("Loaded gun: " + ChatColor.stripColor(f.ret.getName()));
					} else
						MTD.instance.getLogger().info("Failed to load gun: " + ChatColor.stripColor(f.ret.getName()));
				}
	}

	private void loadProjectile() {
		String path = getMTDFolder() + "/projectile";
		File dir = new File(path);
		String[] children = dir.list();
		if (children != null)
			for (String filename : children)
				if (filename.endsWith(".proj")) {
					WeaponReader f = new WeaponReader(MTD.instance, new File(path + "/" + filename), "gun");
					if (f.loaded) {
						f.ret.node = "pvpgunplus." + filename.toLowerCase();
						loadedGuns.add(f.ret);
						f.ret.setIsThrowable(true);
						MTD.instance.getLogger().info("Loaded projectile: " + ChatColor.stripColor(f.ret.getName()));
					} else
						MTD.instance.getLogger().info("Failed to load projectile: " + ChatColor.stripColor(f.ret.getName()));
				}
	}

	protected void clearMemory(boolean init) {
		for (int i = bullets.size() - 1; i >= 0; i--)
			bullets.get(i).destroy();
		for (int i = players.size() - 1; i >= 0; i--)
			players.get(i).unload();
		if (init)
			loadedGuns.clear();
		effects.clear();
		bullets.clear();
		players.clear();
	}

	protected void getOnlinePlayers() {
		Player[] plist = Bukkit.getOnlinePlayers();
		for (Player element : plist) {
			GunPlayer g = new GunPlayer(MTD.instance, element);
			players.add(g);
		}
	}

	protected void startup(boolean init) {
		playerListener = new PluginPlayerListener(MTD.instance);
		entityListener = new PluginEntityListener(MTD.instance);

		random = new Random();

		File dir = new File(getMTDFolder());
		if (!dir.exists())
			dir.mkdir();

		File dir2 = new File(getMTDFolder() + "/guns");
		if (!dir2.exists())
			dir2.mkdir();

		dir2 = new File(getMTDFolder() + "/projectile");
		if (!dir2.exists())
			dir2.mkdir();

		File deag = new File(getMTDFolder() + "guns/Deagle.gun");
		if (!deag.exists())
			MTD.instance.saveResource("guns/Deagle.gun", true);
		File dbar = new File(getMTDFolder() + "guns/DoubleBarrel.gun");
		if (!dbar.exists())
			MTD.instance.saveResource("guns/DoubleBarrel.gun", true);
		File l118 = new File(getMTDFolder() + "guns/L118a.gun");
		if (!l118.exists())
			MTD.instance.saveResource("guns/L118a.gun", true);
		File LAW = new File(getMTDFolder() + "guns/LAW.gun");
		if (!LAW.exists())
			MTD.instance.saveResource("guns/LAW.gun", true);
		File M16 = new File(getMTDFolder() + "guns/M16.gun");
		if (!M16.exists())
			MTD.instance.saveResource("guns/M16.gun", true);
		File M4 = new File(getMTDFolder() + "guns/M4A1.gun");
		if (!M4.exists())
			MTD.instance.saveResource("guns/M4A1.gun", true);
		File RPG = new File(getMTDFolder() + "guns/RPG.gun");
		if (!RPG.exists())
			MTD.instance.saveResource("guns/RPG.gun", true);
		File sniper = new File(getMTDFolder() + "guns/Sniper.gun");
		if (!sniper.exists())
			MTD.instance.saveResource("guns/Sniper.gun", true);
		File spas = new File(getMTDFolder() + "guns/Spas12.gun");
		if (!spas.exists())
			MTD.instance.saveResource("guns/Spas12.gun", true);
		File flash = new File(getMTDFolder() + "projectile/Flashbang.proj");
		if (!flash.exists())
			MTD.instance.saveResource("projectile/Flashbang.proj", true);
		File gre = new File(getMTDFolder() + "projectile/Grenade.proj");
		if (!gre.exists())
			MTD.instance.saveResource("projectile/Grenade.proj", true);
		File mol = new File(getMTDFolder() + "projectile/Molotov.proj");
		if (!mol.exists())
			MTD.instance.saveResource("projectile/Molotov.proj", true);
		File smoke = new File(getMTDFolder() + "projectile/Smoke.proj");
		if (!smoke.exists())
			MTD.instance.saveResource("projectile/Smoke.proj", true);

		if (init) {
			loadGuns();
			loadProjectile();
		}

		getOnlinePlayers();
	}
}

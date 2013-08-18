package main.java.net.aemservers.MTD.Automation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.java.net.aemservers.MTD.MTD;
import main.java.net.aemservers.MTD.MobDisguise;
import main.java.net.aemservers.MTD.Commands.Msg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Game {

	// Players in the game
	private List<String> players = new ArrayList<String>();
	// Locations of signs and towers
	private List<SerializableLocation> towers = new ArrayList<SerializableLocation>();
	private List<SerializableLocation> signs = new ArrayList<SerializableLocation>();
	// Points of the finish line
	private SerializableLocation finish1, finish2;
	// Mob spawn and lobby spawn location
	private SerializableLocation mobspawn, lobby;
	// The constructed finish line from the two points
	private final FinishLine finish;
	// The map name
	private final String name;
	// The scoreboard sidebar objective.
	private Objective obj;
	private int countdowntime = -1;
	private Gamestate state;
	private Team mobs, archers;

	public Game(String name) {
		this.name = name;
		deserialize();
		serialize();
		MTD.instance.getMaps().add(this);
		finish = new FinishLine(finish1, finish2);
		state = Gamestate.WAITING;
	}

	public FinishLine getFinishLine() {
		return finish;
	}

	private void serialize() {
		YamlConfiguration file = MTD.instance.getMapsFile();

		ConfigurationSection section;
		if ((section = file.getConfigurationSection(name)) == null)
			section = file.createSection(name);

		section.set("mobs_spawnpoint", mobspawn == null ? 0 : mobspawn.x + "," + mobspawn.y + "," + mobspawn.z);
		section.set("lobby_spawnpoint", lobby == null ? 0 : lobby.x + "," + lobby.y + "," + lobby.z);

		section.set("finishline.p1", finish1 == null ? 0 : finish1.x + "," + finish1.y + "," + finish1.z);
		section.set("finishline.p2", finish2 == null ? 0 : finish2.x + "," + finish2.y + "," + finish2.z);

		List<String> savedtowers = new ArrayList<String>();
		for (SerializableLocation sl : towers) {
			Block b = sl.asLocation().getBlock();
			if (!savedtowers.contains(b.getX() + "," + b.getY() + "," + b.getZ()))
				savedtowers.add(b.getX() + "," + b.getY() + "," + b.getZ());
		}
		section.set("towers", savedtowers);

		List<String> savedsigns = new ArrayList<String>();
		for (SerializableLocation sl : signs)
			try {
				Sign s = (Sign) sl.asLocation().getBlock().getState();
				if (!savedsigns.contains(s.getX() + "," + s.getY() + "," + s.getZ()))
					savedsigns.add(s.getX() + "," + s.getY() + "," + s.getZ());
			} catch (Exception exc) {
				MTD.instance.getLogger().warning(
						"Sign specified at " + sl.x + "," + sl.y + "," + sl.z + " but did not find a sign at that location.");
			}
		section.set("signs", savedsigns);

		try {
			file.save(new File(MTD.instance.getDataFolder().getAbsolutePath() + File.separator + "maps.yml"));
			MTD.debug("Saved game " + name);
		} catch (IOException e) {
			MTD.instance.getLogger().warning("Unable to save game state: " + e.getMessage());
		}
	}

	private void deserialize() {
		YamlConfiguration file = MTD.instance.getMapsFile();

		ConfigurationSection section = file.getConfigurationSection(name);
		String world = section.getString("game_world");
		String m = section.getString("mobs_spawnpoint");
		if (parseLocation(m, world) != null)
			mobspawn = new SerializableLocation(parseLocation(m, world), world);
		m = section.getString("lobby_spawnpoint");
		if (parseLocation(m, world) != null)
			lobby = new SerializableLocation(parseLocation(m, world), world);
		m = section.getString("finishline.p1");
		if (parseLocation(m, world) != null)
			finish1 = new SerializableLocation(parseLocation(m, world), world);
		m = section.getString("finishline.p2");
		if (parseLocation(m, world) != null)
			finish2 = new SerializableLocation(parseLocation(m, world), world);
		for (String t : section.getStringList("towers"))
			if (parseLocation(t, world) != null)
				towers.add(new SerializableLocation(parseLocation(t, world).getBlock().getLocation(), world));
		for (String s : section.getStringList("signs"))
			try {
				if (parseLocation(s, world) != null) {
					@SuppressWarnings("unused")
					Sign sign = (Sign) parseLocation(s, world).getBlock().getState(); // Throws
					signs.add(new SerializableLocation(parseLocation(s, world), world));
				}
			} catch (Exception exc) {
				MTD.instance.getLogger().warning("Sign specified at " + s + " but did not find a sign at that location.");
			}
	}

	private Location parseLocation(String s, String world) {
		if (s == null)
			return null;
		if (!s.contains(","))
			return null;
		try {
			return new Location(Bukkit.getWorld(world), Double.parseDouble(s.split(",")[0]), Double.parseDouble(s.split(",")[1]),
					Double.parseDouble(s.split(",")[2]));
		} catch (ArrayIndexOutOfBoundsException exc) {
			MTD.instance.getLogger().warning("Did not specify enough arguments in entry '" + s + "' in maps.yml. (Requires x,y,z)");
			return null;
		} catch (NumberFormatException exc) {
			MTD.instance.getLogger().warning("Invalid arguments. Requires x,y,z (you provided " + s + ")");
			return null;
		}
	}

	public boolean hasStarted() {
		return state != Gamestate.WAITING;
	}

	public void start(boolean force) {
		if (force || players.size() >= MTD.instance.getConfig().getInt("map_capacity")) {
			state = Gamestate.WAITING;
			doBackup();
			MTD.debug("Game initialized in game " + name);
			Ticker.beginCountdown(this);
		}
	}

	public void doBackup() {
		MTD.debug("Backing up world for game " + name);
		Rollback.backup(lobby != null ? Bukkit.getWorld(lobby.world) : mobspawn != null ? Bukkit.getWorld(mobspawn.world) : !getPlayers()
				.isEmpty() ? getPlayers().get(0).getWorld() : null);
	}

	public void postCountdownStart() {
		MTD.debug("Building initialized in game " + name);
		state = Gamestate.BUILDING;
		Ticker.beginCountdown(this);
		int numMobs = (int) (players.size() * MTD.instance.getConfig().getDouble("mobs"));
		List<Integer> playerIDs = new ArrayList<Integer>();
		Random r = new Random();
		int timeout = 100;
		while (playerIDs.size() < numMobs) {
			int i = r.nextInt(players.size());
			if (!playerIDs.contains(i))
				playerIDs.add(i);
			timeout--;
			if (timeout < 0)
				break;
		}

		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		obj = sb.registerNewObjective("alive", "dummy");
		mobs = sb.registerNewTeam("mobs");
		archers = sb.registerNewTeam("archers");
		mobs.setAllowFriendlyFire(false);
		mobs.setPrefix(ChatColor.RED + "");
		archers.setAllowFriendlyFire(false);
		archers.setPrefix(ChatColor.YELLOW + "");

		for (int i : playerIDs) {
			MobDisguise.disguise(getPlayers().get(i));
			mobs.addPlayer(getPlayers().get(i));
		}
		for (Player p : MobDisguise.alive(getPlayers()))
			archers.addPlayer(p);

		obj.setDisplayName(ChatColor.YELLOW + name.toUpperCase() + " - " + timeformat(countdowntime));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "Players Left")).setScore(MobDisguise.alive(getPlayers()).size());
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "Lives")).setScore(MTD.instance.getConfig().getInt("lives"));
		for (Player p : getPlayers())
			p.setScoreboard(sb);

		for (Player p : getPlayers())
			Teleport.teleport(p, getMobSpawn(), true);
		for (Player p : MobDisguise.alive(getPlayers())) {
			buildingInventory(p);
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * countdowntime, 8));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * countdowntime, 4));
		}
	}

	private void buildingInventory(Player p) {
		p.getInventory().addItem(new ItemStack(Material.DIRT, 64));
		p.getInventory().addItem(new ItemStack(Material.LOG, 48));
		p.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 32));
		p.getInventory().addItem(new ItemStack(Material.OBSIDIAN, 16));
		p.getInventory().addItem(new ItemStack(Material.TNT, 8));
	}

	private String timeformat(int i) {
		if (i < 60)
			return "00:" + (i < 10 ? "0" : "") + i;
		int minutes = i / 60;
		i -= 60 * minutes;
		return (minutes < 10 ? "0" : "") + minutes + ":" + (i < 10 ? "0" : "") + i;
	}

	public void setTeam(Player p, int team) {
		if (archers == null && mobs == null)
			return;
		if (team == 0) {
			if (archers.hasPlayer(p))
				archers.removePlayer(p);
			mobs.addPlayer(p);
		} else {
			if (mobs.hasPlayer(p))
				mobs.removePlayer(p);
			archers.addPlayer(p);
		}
	}

	public void postBuildingStart() {
		MTD.debug("Game started in game " + name);
		state = Gamestate.STARTED;
		Ticker.beginCountdown(this);
		updateTime();

		int h = 0;
		List<Player> p = MobDisguise.alive(getPlayers());
		if (towers.isEmpty()) {
			World w = Bukkit.getWorlds().get(0);
			MTD.instance.getLogger().warning("No tower locations specified. Spawning at spawnpoint in world " + w.getName());
			for (Player t : p) {
				Teleport.teleport(t, w.getSpawnLocation());
				archerInventory(t);
			}
			return;
		}
		for (int i = 0; i < p.size(); i++) {
			if (towers.size() > h)
				Teleport.teleport(p.get(i), towers.get(h).asLocation());
			else {
				h = 0;
				Teleport.teleport(p.get(i), towers.get(h).asLocation());
			}
			archerInventory(p.get(i));
			h++;
		}
	}

	private void archerInventory(Player p) {
		p.getInventory().setContents(new ItemStack[p.getInventory().getSize()]);
		for (PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		p.getInventory().setArmorContents(null);
		p.getInventory().setHeldItemSlot(1);
		ItemStack bow = new ItemStack(Material.BOW, 1);
		bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
		bow.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
		bow.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
		bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
		p.getInventory().addItem(bow);
		p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setSaturation(20);
	}

	public enum Gamestate {
		WAITING, BUILDING, STARTED;
	}

	public void updateTime() {
		if (obj != null)
			obj.setDisplayName(ChatColor.YELLOW + name.toUpperCase() + " - " + timeformat(countdowntime));
	}

	public Score getScore() {
		if (obj != null)
			return obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "Players Left"));
		return null;
	}

	public Score getCrosses() {
		if (obj != null)
			return obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "Lives"));
		return null;
	}

	public Score getMrTeePeeScore() {
		if (obj != null)
			return obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "MrTeePee Kills"));
		return null;
	}

	public void broadcast(String msg) {
		for (Player p : getPlayers())
			p.sendMessage(msg);
	}

	public Gamestate getState() {
		return state;
	}

	public void stop(boolean force) {
		if ((force || MobDisguise.alive(getPlayers()).isEmpty() || getCrosses() != null && getCrosses().getScore() == 0 || countdowntime <= 0)
				&& hasStarted()) {
			state = Gamestate.WAITING;
			MobDisguise.undisguise(getPlayers());
			if (MobDisguise.alive(getPlayers()).isEmpty())
				broadcast(Msg.format("mob_domination"));
			else if (countdowntime <= 0)
				broadcast(Msg.format("timer_out"));
			else if (getCrosses() != null && getCrosses().getScore() == 0)
				broadcast(Msg.format("mob_conquer"));
			else
				broadcast(Msg.format("game_ended"));
			countdowntime = -1;
			World w = lobby != null ? Bukkit.getWorld(lobby.world) : mobspawn != null ? Bukkit.getWorld(mobspawn.world) : !getPlayers()
					.isEmpty() ? getPlayers().get(0).getWorld() : null;
			for (Player p : getPlayers())
				if (p != null) {
					p.getInventory().setContents(new ItemStack[p.getInventory().getSize()]);
					for (PotionEffect effect : p.getActivePotionEffects())
						p.removePotionEffect(effect.getType());
					p.getInventory().setArmorContents(null);
					toSpawn(p);
					try {
						p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					} catch (Exception e) {

					}
				}
			if (w != null) {
				for (Player p : w.getPlayers())
					toSpawn(p);
				/*
				 * Any stragglers (if the command failed to execute)
				 */
				for (Player p : w.getPlayers())
					toSpawn(p, null);
				MTD.instance.getLogger().info("Rolling back " + w.getName());
				Rollback.rollback(w);
			}

			players.clear();
		}
	}

	private void toSpawn(Player p) {
		String cmd = MTD.instance.getConfig().getString("leave_command");
		if (cmd == null || cmd.isEmpty()) {
			toSpawn(p, Bukkit.getWorld(MTD.instance.getConfig().getString("world")));
			return;
		}
		Bukkit.dispatchCommand(p, MTD.instance.getConfig().getString("leave_command"));
	}

	private void toSpawn(Player p, World w) {
		if (w == null) {
			if (p != null)
				p.kickPlayer("The world you were on is being rolled-back.");
			return;
		}
		Teleport.teleport(p, w.getSpawnLocation());
		if (MTD.instance.getServer().getPluginManager().isPluginEnabled("essentials"))
			Bukkit.dispatchCommand(p, "spawn");
	}

	public static boolean inGame(Player player) {
		for (Game g : MTD.instance.getMaps())
			if (g.players.contains(player.getName()))
				return true;
		return false;
	}

	public static Game getGame(Player player) {
		for (Game g : MTD.instance.getMaps())
			if (g.players.contains(player.getName()))
				return g;
		return null;
	}

	public static Game getGame(String name) {
		for (Game g : MTD.instance.getMaps())
			if (g.name.equals(name))
				return g;
		return null;
	}

	public String getName() {
		return name;
	}

	public List<Player> getPlayers() {
		List<Player> ret = new ArrayList<Player>();
		for (String s : players)
			if (Bukkit.getPlayer(s) != null)
				ret.add(Bukkit.getPlayer(s));
		return ret;
	}

	public List<Sign> getSigns() {
		List<Sign> ret = new ArrayList<Sign>();
		for (SerializableLocation s : signs)
			try {
				ret.add((Sign) s.asLocation().getBlock().getState());
			} catch (Exception e) {

			}
		return ret;
	}

	public boolean join(Player p) {
		if (!hasStarted()) {
			players.add(p.getName());
			if (MTD.instance.getConfig().getBoolean("bungeecord"))
				Bukkit.dispatchCommand(p, "server " + name);
			Teleport.teleport(p, getLobbySpawn());
			p.getInventory().setContents(new ItemStack[p.getInventory().getSize()]);
			for (PotionEffect effect : p.getActivePotionEffects())
				p.removePotionEffect(effect.getType());
			p.getInventory().setArmorContents(null);
			start(false);
			return true;
		}
		return false;
	}

	public boolean quit(Player p) {
		if (!players.contains(p.getName()))
			return false;
		MobDisguise.undisguise(p);
		p.getInventory().setContents(new ItemStack[p.getInventory().getSize()]);
		for (PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		p.getInventory().setArmorContents(null);
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		toSpawn(p);
		boolean b = players.remove(p.getName());
		stop(false);
		return b;
	}

	public boolean addTower(Location l) {
		if (towers.contains(new SerializableLocation(l.getBlock().getLocation(), l.getWorld().getName())))
			return false;
		boolean b = towers.add(new SerializableLocation(l.getBlock().getLocation(), l.getWorld().getName()));
		serialize();
		return b;
	}

	public boolean removeTower(Location l) {
		if (!towers.contains(new SerializableLocation(l.getBlock().getLocation(), l.getWorld().getName())))
			return false;
		boolean b = towers.remove(l.getBlock());
		serialize();
		return b;
	}

	public boolean addSign(Sign s) {
		if (signs.contains(new SerializableLocation(s.getLocation(), s.getWorld().getName())))
			return false;
		boolean b = signs.add(new SerializableLocation(s.getLocation(), s.getWorld().getName()));
		serialize();
		return b;
	}

	public boolean removeSign(Sign s) {
		if (!signs.contains(new SerializableLocation(s.getLocation(), s.getWorld().getName())))
			return false;
		boolean b = signs.remove(new SerializableLocation(s.getLocation(), s.getWorld().getName()));
		serialize();
		return b;
	}

	public void setMobSpawn(Location l) {
		mobspawn = new SerializableLocation(l, l.getWorld().getName());
		serialize();
	}

	public boolean isAlive(Player p) {
		if (!players.contains(p.getName()))
			return false;
		return MobDisguise.isAlive(p);
	}

	public void setCountdownTime(int i) {
		countdowntime = i;
	}

	public int getCountdownTime() {
		return countdowntime;
	}

	public Location getMobSpawn() {
		if (mobspawn == null) {
			World w = Bukkit.getWorlds().get(0);
			MTD.instance.getLogger().warning("No mob spawn location specified. Spawning at spawnpoint in world " + w.getName());
			return w.getSpawnLocation();
		}
		Location spawn = mobspawn.asLocation();
		Random r = new Random();
		spawn.add(r.nextInt(3) * (r.nextInt(2) == 0 ? -1 : 1), 0, r.nextInt(2) * (r.nextInt(2) == 0 ? -1 : 1));
		return spawn;
	}

	public Location getLobbySpawn() {
		if (lobby == null) {
			World w = Bukkit.getWorlds().get(0);
			MTD.instance.getLogger().warning("No lobby spawn location specified. Spawning at spawnpoint in world " + w.getName());
			return w.getSpawnLocation();
		}
		Location spawn = lobby.asLocation();
		Random r = new Random();
		spawn.add(r.nextInt(3) * (r.nextInt(2) == 0 ? -1 : 1), 0, r.nextInt(2) * (r.nextInt(2) == 0 ? -1 : 1));
		return spawn;
	}
}

package main.java.net.teepee.MTD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import main.java.net.teepee.MTD.Automation.Game;
import main.java.net.teepee.MTD.Automation.Ticker;
import main.java.net.teepee.MTD.Commands.Classes;
import main.java.net.teepee.MTD.Commands.End;
import main.java.net.teepee.MTD.Commands.Games;
import main.java.net.teepee.MTD.Commands.Join;
import main.java.net.teepee.MTD.Commands.Quit;
import main.java.net.teepee.MTD.Commands.Start;
import main.java.net.teepee.MTD.Listeners.DeathDamage;
import main.java.net.teepee.MTD.Listeners.Disconnect;
import main.java.net.teepee.MTD.Listeners.LeaveDuringGame;
import main.java.net.teepee.MTD.Listeners.Movement;
import main.java.net.teepee.MTD.Listeners.PreStart;
import main.java.net.teepee.MTD.Listeners.Signage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MTD extends JavaPlugin {

	private List<Game> maps = new ArrayList<Game>();
	private YamlConfiguration mapsFile;
	public static MTD instance;
	private static boolean debug = false;
	private PVPGunsPlus pvpgp;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		pvpgp = new PVPGunsPlus();
		pvpgp.doEnable(this);
		mapsFile = YamlConfiguration.loadConfiguration(loadMapsFile());
		loadGames();

		getServer().getPluginManager().registerEvents(new DeathDamage(), this);
		getServer().getPluginManager().registerEvents(new Movement(), this);
		getServer().getPluginManager().registerEvents(new Disconnect(), this);
		getServer().getPluginManager().registerEvents(new PreStart(), this);
		getServer().getPluginManager().registerEvents(new Signage(), this);
		getServer().getPluginManager().registerEvents(new LeaveDuringGame(), this);

		getServer().getPluginManager().registerEvents(pvpgp.playerListener, this);
		getServer().getPluginManager().registerEvents(pvpgp.entityListener, this);

		getCommand("join").setExecutor(new Join());
		getCommand("quit").setExecutor(new Quit());
		getCommand("games").setExecutor(new Games());
		getCommand("start").setExecutor(new Start());
		getCommand("end").setExecutor(new End());
		getCommand("classes").setExecutor(new Classes());

		getServer().getScheduler().runTaskTimer(this, new Ticker(), 20L, 20L);
	}

	@Override
	public void onDisable() {
		pvpgp.doDisable();

		reloadConfig();
		getServer().getScheduler().cancelTasks(this);

		for (Game g : maps)
			g.stop(true);
	}

	public static void debug(String msg) {
		if (debug)
			instance.getLogger().info(msg);
	}

	public List<Game> getMaps() {
		return maps;
	}

	public YamlConfiguration getMapsFile() {
		return mapsFile;
	}

	public PVPGunsPlus getPVPGuns() {
		return pvpgp;
	}

	private File loadMapsFile() {
		File f = new File(getDataFolder() + File.separator + "maps.yml");
		if (!f.exists())
			saveResource("maps.yml", true);
		return f;
	}

	private void loadGames() {
		for (String s : mapsFile.getKeys(false)) {
			debug("Loading game " + s);
			new Game(s);
		}
	}
}

package main.java.net.teepee.MTD.Automation;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Rollback {

	/**
	 * Backup the world to its current state.
	 * 
	 * @param w
	 *            The world
	 */
	public static void backup(World w) {
		w.setAutoSave(false);
		w.save();
	}

	/**
	 * Rollback the world to the point where it was backed up. WORLD MUST BE
	 * EMPTY (no players)
	 * 
	 * @param w
	 *            The world
	 */
	public static void rollback(World w) {
		Bukkit.unloadWorld(w, false);
		Bukkit.getServer().createWorld(new WorldCreator(w.getName()));
		w.setAutoSave(true);
	}
}

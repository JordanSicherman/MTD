package main.java.net.aemservers.MTD.Automation;

import java.io.Serializable;

import main.java.net.aemservers.MTD.MTD;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public class SerializableLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5892627789864148906L;
	public final double x, y, z;
	public final String world;

	public SerializableLocation(Location l, String name) {
		x = l.getX();
		y = l.getY();
		z = l.getZ();
		if (l.getWorld() == null || Bukkit.getWorld(name) == null) {
			MTD.instance.getLogger().warning(
					"The world " + name + " was specified in the map but does not exist! Creating a new world (superflat)");
			MTD.instance.getLogger().warning("If you're using a world on a different server, ignore this.");
			world = new WorldCreator(name).generateStructures(false).type(WorldType.FLAT).createWorld().getName();
			return;
		}
		world = l.getWorld().getName();
	}

	public Location asLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	@Override
	public String toString() {
		return world + " " + x + ", " + y + ", " + z;
	}

	@Override
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
}

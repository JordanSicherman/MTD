package main.java.net.teepee.MTD.Automation;

import main.java.net.teepee.MTD.MTD;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;

public class FinishLine {

	private SerializableLocation r1, r2;
	private Coordinate shared;

	public FinishLine(SerializableLocation p1, SerializableLocation p2) {
		if (p1 == null || p2 == null)
			throw new IllegalArgumentException("One or both finish line points are unspecified.");
		if (p1.y != p2.y) {
			MTD.instance.getLogger().warning(
					"Finish line points " + p1 + " and " + p2 + " do not share the same Z-coordinate. Please respecify.");
			p1 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
			p2 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
			r1 = p1;
			r2 = p2;
			return;
		}
		if (!p1.world.equals(p2.world)) {
			MTD.instance.getLogger().warning("Finish line points " + p1 + " and " + p2 + " do not share the same world. Please respecify.");
			p1 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
			p2 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
			r1 = p1;
			r2 = p2;
		}
		if (p1.x == p2.x) {
			shared = Coordinate.Z;
			if (p1.z == p2.z) {
				MTD.instance.getLogger().warning("Finish line points " + p1 + " and " + p2 + " are identical. Please respecify.");
				p1 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
				p2 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
			}
			if (p1.z < p2.z) {
				r1 = p1;
				r2 = p2;
			} else {
				r1 = p2;
				r2 = p1;
			}
		} else if (p1.z == p2.z) {
			shared = Coordinate.X;
			if (p1.x == p2.x) {
				MTD.instance.getLogger().warning("Finish line points " + p1 + " and " + p2 + " are identical. Please respecify.");
				p1 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
				p2 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
			}
			if (p1.x < p2.x) {
				r1 = p1;
				r2 = p2;
			} else {
				r1 = p2;
				r2 = p1;
			}
		} else {
			MTD.instance.getLogger().warning("Finish line points " + p1 + " and " + p2 + " are not linear. Please respecify.");
			p1 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
			p2 = new SerializableLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), Bukkit.getWorlds().get(0).getName());
		}
	}

	public boolean didCross(PlayerMoveEvent e) {
		if (e.isCancelled())
			return false;
		Block from = e.getFrom().getBlock();
		Block to = e.getTo().getBlock();
		if (from.getWorld() == to.getWorld()) {
			if (from.getX() != to.getX())
				if (shared == Coordinate.Z)
					if (to.getX() == (int) r1.x)
						if (to.getZ() <= (int) r2.z && to.getZ() >= (int) r1.z)
							return true;
			if (from.getZ() != to.getZ())
				if (shared == Coordinate.X)
					if (to.getZ() == (int) r1.z)
						if (to.getX() <= (int) r2.x && to.getX() >= (int) r1.x)
							return true;
		}
		/*
		 * Moved in the Y plane or extra-worldly, ignore.
		 */
		return false;
	}

	public enum Coordinate {
		X, Z
	}
}

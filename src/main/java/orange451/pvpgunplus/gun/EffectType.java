package main.java.orange451.pvpgunplus.gun;

import main.java.net.aemservers.MTD.MTD;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EffectType {
	private final int maxDuration;
	private int duration;
	private final Effect type;
	private final double radius;
	private Location location;
	private byte specialDat = -1;

	public EffectType(int duration, double radius, Effect type) {
		this.duration = duration;
		maxDuration = duration;
		this.type = type;
		this.radius = radius;
	}

	public void start(Location location) {
		this.location = location;
		duration = maxDuration;
		MTD.instance.getPVPGuns().addEffect(this);
	}

	@Override
	public EffectType clone() {
		return new EffectType(maxDuration, radius, type).setSpecialDat(specialDat);
	}

	public void tick() {
		duration -= 1;

		if (duration < 0) {
			MTD.instance.getPVPGuns().removeEffect(this);
			return;
		}

		double yRad = radius;
		if (type.equals(Effect.MOBSPAWNER_FLAMES)) {
			yRad = 0.75D;
			Player[] players = Bukkit.getOnlinePlayers();
			for (int i = players.length - 1; i >= 0; i--)
				if (players[i].getWorld().equals(location.getWorld()) && location.distance(players[i].getLocation()) < radius)
					players[i].setFireTicks(20);
		}

		for (double i = -radius; i <= radius; i += 1.0D)
			for (double ii = -radius; ii <= radius; ii += 1.0D)
				for (double iii = 0.0D; iii <= yRad * 2.0D; iii += 1.0D) {
					int rand = MTD.instance.getPVPGuns().random.nextInt(8);
					if (rand == 2) {
						Location newloc = location.clone().add(i, iii - 1.0D, ii);
						Location testLoc = location.clone().add(0.0D, yRad - 1.0D, 0.0D);
						if (newloc.distance(testLoc) <= radius) {
							byte dat = (byte) MTD.instance.getPVPGuns().random.nextInt(8);
							if (specialDat > -1)
								dat = specialDat;
							newloc.getWorld().playEffect(newloc, type, dat);
						}
					}
				}
	}

	public EffectType setSpecialDat(byte specialDat) {
		this.specialDat = specialDat;
		return this;
	}
}
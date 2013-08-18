package main.java.orange451.pvpgunplus.events;

import main.java.orange451.pvpgunplus.gun.Gun;
import main.java.orange451.pvpgunplus.gun.GunPlayer;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PVPGunPlusGunKillEntityEvent extends PVPGunPlusEvent {
	private final Gun gun;
	private final GunPlayer shooter;
	private final Entity shot;

	public PVPGunPlusGunKillEntityEvent(GunPlayer shooter, Gun gun, Entity killed) {
		this.gun = gun;
		this.shooter = shooter;
		shot = killed;
	}

	public GunPlayer getKiller() {
		return shooter;
	}

	public Player getKillerAsPlayer() {
		return shooter.getPlayer();
	}

	public Entity getKilled() {
		return shot;
	}

	public Gun getGun() {
		return gun;
	}
}
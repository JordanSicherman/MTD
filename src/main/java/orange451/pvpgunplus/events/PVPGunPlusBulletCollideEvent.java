package main.java.orange451.pvpgunplus.events;

import main.java.orange451.pvpgunplus.gun.Gun;
import main.java.orange451.pvpgunplus.gun.GunPlayer;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PVPGunPlusBulletCollideEvent extends PVPGunPlusEvent {
	private final Gun gun;
	private final GunPlayer shooter;
	private final Block blockHit;

	public PVPGunPlusBulletCollideEvent(GunPlayer shooter, Gun gun, Block block) {
		this.gun = gun;
		this.shooter = shooter;
		blockHit = block;
	}

	public Gun getGun() {
		return gun;
	}

	public GunPlayer getShooter() {
		return shooter;
	}

	public Player getShooterAsPlayer() {
		return shooter.getPlayer();
	}

	public Block getBlockHit() {
		return blockHit;
	}
}
package main.java.orange451.pvpgunplus.events;

import main.java.orange451.pvpgunplus.gun.Gun;
import main.java.orange451.pvpgunplus.gun.GunPlayer;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PVPGunPlusGunDamageEntityEvent extends PVPGunPlusEvent {
	private final Gun gun;
	private final GunPlayer shooter;
	private final Entity shot;
	private boolean isHeadshot;
	private final int damage;
	private EntityDamageByEntityEvent event;

	public PVPGunPlusGunDamageEntityEvent(EntityDamageByEntityEvent event, GunPlayer shooter, Gun gun, Entity shot, boolean headshot) {
		this.gun = gun;
		this.shooter = shooter;
		this.shot = shot;
		isHeadshot = headshot;
		damage = gun.getGunDamage();
	}

	public EntityDamageByEntityEvent getEntityDamageEntityEvent() {
		return event;
	}

	public boolean isHeadshot() {
		return isHeadshot;
	}

	public void setHeadshot(boolean b) {
		isHeadshot = b;
	}

	public GunPlayer getShooter() {
		return shooter;
	}

	public Entity getEntityDamaged() {
		return shot;
	}

	public Player getShooterAsPlayer() {
		return shooter.getPlayer();
	}

	public Gun getGun() {
		return gun;
	}

	public int getDamage() {
		return damage;
	}
}
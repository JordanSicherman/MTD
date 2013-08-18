package main.java.orange451.pvpgunplus.events;

import main.java.orange451.pvpgunplus.gun.Gun;
import main.java.orange451.pvpgunplus.gun.GunPlayer;

import org.bukkit.entity.Player;

public class PVPGunPlusFireGunEvent extends PVPGunPlusEvent {
	private final Gun gun;
	private final GunPlayer shooter;
	private int amountAmmoNeeded;
	private double accuracy;

	public PVPGunPlusFireGunEvent(GunPlayer shooter, Gun gun) {
		this.gun = gun;
		this.shooter = shooter;
		amountAmmoNeeded = gun.getAmmoAmtNeeded();
		accuracy = gun.getAccuracy();
		if (shooter.getPlayer().isSneaking() && gun.getAccuracy_crouched() > -1.0D)
			accuracy = gun.getAccuracy_crouched();
		if (shooter.isAimedIn() && gun.getAccuracy_aimed() > -1.0D)
			accuracy = gun.getAccuracy_aimed();
	}

	public PVPGunPlusEvent setAmountAmmoNeeded(int i) {
		amountAmmoNeeded = i;
		return this;
	}

	public int getAmountAmmoNeeded() {
		return amountAmmoNeeded;
	}

	public double getGunAccuracy() {
		return accuracy;
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

	public void setGunAccuracy(double d) {
		accuracy = d;
	}
}
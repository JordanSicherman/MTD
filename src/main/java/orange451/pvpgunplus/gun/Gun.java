package main.java.orange451.pvpgunplus.gun;

import java.util.ArrayList;
import java.util.Random;

import main.java.net.aemservers.MTD.MTD;
import main.java.net.aemservers.MTD.PVPGunsPlus;
import main.java.orange451.pvpgunplus.events.PVPGunPlusFireGunEvent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Gun {
	private boolean canHeadshot;
	private boolean isThrowable;
	private boolean hasSmokeTrail;
	private boolean localGunSound;
	private boolean canAimLeft;
	private boolean canAimRight;
	private boolean canGoPastMaxDistance;
	private byte gunByte;
	private byte ammoByte;
	private int gunType;
	private int ammoType;
	private int ammoAmtNeeded;
	private int gunDamage;
	private int explosionDamage = -1;
	private int roundsPerBurst;
	private int reloadTime;
	private int maxDistance;
	private int bulletsPerClick;
	private int bulletsShot;
	private int armorPenetration;
	private int releaseTime = -1;
	private double bulletSpeed;
	private double accuracy;
	private double accuracy_aimed = -1.0D;
	private double accuracy_crouched = -1.0D;
	private double explodeRadius;
	private double fireRadius;
	private double flashRadius;
	private double knockback;
	private double recoil;
	private double gunVolume = 1.0D;
	private String gunName;
	private String fileName;
	public String projType = "";
	public ArrayList<String> gunSound = new ArrayList<String>();
	public String outOfAmmoMessage = "";
	public String permissionMessage = "";
	public boolean needsPermission;
	public boolean canClickRight;
	public boolean canClickLeft;
	public boolean hasClip = true;
	public boolean ignoreItemData = false;
	public boolean reloadGunOnDrop = true;
	public int maxClipSize = 30;
	public int bulletDelayTime = 10;
	public int roundsFired;
	public int gunReloadTimer;
	public int timer;
	public int lastFired;
	public int ticks;
	public int heldDownTicks;
	public boolean firing = false;
	public boolean reloading;
	public boolean changed = false;
	public GunPlayer owner;
	public String node;
	public String reloadType = "NORMAL";
	public EffectType releaseEffect;

	public Gun(String name) {
		gunName = name;
		fileName = name;
		outOfAmmoMessage = "Out of ammo!";
	}

	public void shoot() {
		if (owner != null && owner.getPlayer().isOnline() && !reloading) {
			PVPGunPlusFireGunEvent event = new PVPGunPlusFireGunEvent(owner, this);
			MTD.instance.getServer().getPluginManager().callEvent(event);
			if (!event.isCancelled())
				if (owner.checkAmmo(this, event.getAmountAmmoNeeded()) && event.getAmountAmmoNeeded() > 0
						|| event.getAmountAmmoNeeded() == 0) {
					owner.removeAmmo(this, event.getAmountAmmoNeeded());
					if (roundsFired >= maxClipSize && hasClip) {
						reloadGun();
						return;
					}
					doRecoil(owner.getPlayer());
					changed = true;
					roundsFired += 1;
					for (int i = 0; i < gunSound.size(); i++) {
						Sound sound = PVPGunsPlus.getSound(gunSound.get(i));
						if (sound != null)
							if (localGunSound)
								owner.getPlayer().playSound(owner.getPlayer().getLocation(), sound, (float) gunVolume, 2.0F);
							else
								owner.getPlayer().getWorld().playSound(owner.getPlayer().getLocation(), sound, (float) gunVolume, 2.0F);
					}
					for (int i = 0; i < bulletsPerClick; i++) {
						int acc = (int) (event.getGunAccuracy() * 1000.0D);

						if (acc <= 0)
							acc = 1;
						Location ploc = owner.getPlayer().getLocation();
						Random rand = new Random();
						double dir = -ploc.getYaw() - 90.0F;
						double pitch = -ploc.getPitch();
						double xwep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5D) / 1000.0D;
						double ywep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5D) / 1000.0D;
						double zwep = (rand.nextInt(acc) - rand.nextInt(acc) + 0.5D) / 1000.0D;
						double xd = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch)) + xwep;
						double yd = Math.sin(Math.toRadians(pitch)) + ywep;
						double zd = -Math.sin(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch)) + zwep;
						Vector vec = new Vector(xd, yd, zd);
						vec.multiply(bulletSpeed);
						Bullet bullet = new Bullet(owner, vec, this);
						MTD.instance.getPVPGuns().addBullet(bullet);
					}

					if (roundsFired >= maxClipSize && hasClip)
						reloadGun();
				} else {
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.ITEM_BREAK, 20.0F, 20.0F);
					owner.getPlayer().sendMessage(outOfAmmoMessage);
					finishShooting();
				}
		}
	}

	public void tick() {
		ticks += 1;
		lastFired += 1;
		timer -= 1;
		gunReloadTimer -= 1;

		if (gunReloadTimer < 0) {
			if (reloading)
				finishReloading();
			reloading = false;
		}

		gunSounds();

		if (lastFired > 6)
			heldDownTicks = 0;
		if (heldDownTicks >= 2 && timer <= 0 || firing && !reloading)
			if (roundsPerBurst > 1) {
				if (ticks % 2 == 0) {
					bulletsShot += 1;
					if (bulletsShot <= roundsPerBurst)
						shoot();
					else
						finishShooting();
				}
			} else {
				shoot();
				finishShooting();
			}

		if (reloading)
			firing = false;
	}

	public Gun copy() {
		Gun g = new Gun(gunName);
		g.gunName = gunName;
		g.gunType = gunType;
		g.gunByte = gunByte;
		g.ammoByte = ammoByte;
		g.ammoAmtNeeded = ammoAmtNeeded;
		g.ammoType = ammoType;
		g.roundsPerBurst = roundsPerBurst;
		g.bulletsPerClick = bulletsPerClick;
		g.bulletSpeed = bulletSpeed;
		g.accuracy = accuracy;
		g.accuracy_aimed = accuracy_aimed;
		g.accuracy_crouched = accuracy_crouched;
		g.maxDistance = maxDistance;
		g.gunVolume = gunVolume;
		g.gunDamage = gunDamage;
		g.explodeRadius = explodeRadius;
		g.fireRadius = fireRadius;
		g.flashRadius = flashRadius;
		g.canHeadshot = canHeadshot;
		g.reloadTime = reloadTime;
		g.canAimLeft = canAimLeft;
		g.canAimRight = canAimRight;
		g.canClickLeft = canClickLeft;
		g.canClickRight = canClickRight;
		g.hasSmokeTrail = hasSmokeTrail;
		g.armorPenetration = armorPenetration;
		g.isThrowable = isThrowable;
		g.ignoreItemData = ignoreItemData;
		g.outOfAmmoMessage = outOfAmmoMessage;
		g.projType = projType;
		g.needsPermission = needsPermission;
		g.node = node;
		g.gunSound = gunSound;
		g.bulletDelayTime = bulletDelayTime;
		g.hasClip = hasClip;
		g.maxClipSize = maxClipSize;
		g.reloadGunOnDrop = reloadGunOnDrop;
		g.localGunSound = localGunSound;
		g.fileName = fileName;
		g.explosionDamage = explosionDamage;
		g.recoil = recoil;
		g.knockback = knockback;
		g.reloadType = reloadType;
		g.releaseTime = releaseTime;
		g.canGoPastMaxDistance = canGoPastMaxDistance;
		g.permissionMessage = permissionMessage;
		if (releaseEffect != null)
			g.releaseEffect = releaseEffect.clone();
		return g;
	}

	public void reloadGun() {
		reloading = true;
		gunReloadTimer = reloadTime;
	}

	private void gunSounds() {
		if (reloading) {
			int amtReload = reloadTime - gunReloadTimer;
			if (reloadType.equalsIgnoreCase("bolt")) {
				if (amtReload == 6)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.DOOR_OPEN, 2.0F, 1.5F);
				if (amtReload == reloadTime - 4)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.DOOR_CLOSE, 1.0F, 1.5F);
			} else if (reloadType.equalsIgnoreCase("pump") || reloadType.equals("INDIVIDUAL_BULLET")) {
				int rep = (reloadTime - 10) / maxClipSize;
				if (amtReload >= 5 && amtReload <= reloadTime - 5 && amtReload % rep == 0) {
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.NOTE_SNARE_DRUM, 1.0F, 2.0F);
				}

				if (amtReload == reloadTime - 3)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.PISTON_EXTEND, 1.0F, 2.0F);
				if (amtReload == reloadTime - 1)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.PISTON_RETRACT, 1.0F, 2.0F);
			} else {
				if (amtReload == 6) {
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.FIRE_IGNITE, 2.0F, 2.0F);
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.DOOR_OPEN, 1.0F, 2.0F);
				}
				if (amtReload == reloadTime / 2)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.PISTON_RETRACT, 0.33F, 2.0F);
				if (amtReload == reloadTime - 4) {
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.FIRE_IGNITE, 2.0F, 2.0F);
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.DOOR_CLOSE, 1.0F, 2.0F);
				}
			}
		} else {
			if (reloadType.equalsIgnoreCase("pump")) {
				if (timer == 8)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.PISTON_EXTEND, 1.0F, 2.0F);
				if (timer == 6)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.PISTON_RETRACT, 1.0F, 2.0F);
			}
			if (reloadType.equalsIgnoreCase("bolt")) {
				if (timer == bulletDelayTime - 4)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.DOOR_OPEN, 2.0F, 1.25F);
				if (timer == 6)
					owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.DOOR_CLOSE, 1.0F, 1.25F);
			}
		}
	}

	private void doRecoil(Player player) {
		if (recoil != 0.0D) {
			Location ploc = player.getLocation();
			double dir = -ploc.getYaw() - 90.0F;
			double pitch = -ploc.getPitch() - 180.0F;
			double xd = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch));
			double yd = Math.sin(Math.toRadians(pitch));
			double zd = -Math.sin(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch));
			Vector vec = new Vector(xd, yd, zd);
			vec.multiply(recoil / 2.0D).setY(0);
			player.setVelocity(player.getVelocity().add(vec));
		}
	}

	public void doKnockback(LivingEntity entity, Vector speed) {
		if (knockback > 0.0D) {
			speed.normalize().setY(0.6D).multiply(knockback / 4.0D);
			entity.setVelocity(speed);
		}
	}

	public void finishReloading() {
		bulletsShot = 0;
		roundsFired = 0;
		changed = false;
		gunReloadTimer = 0;
	}

	private void finishShooting() {
		bulletsShot = 0;
		timer = bulletDelayTime;
		firing = false;
	}

	public String getName() {
		return gunName;
	}

	public Material getAmmoMaterial() {
		int id = getAmmoType();
		Material mat = Material.getMaterial(id);
		if (mat != null)
			return mat;
		return null;
	}

	public int getAmmoType() {
		return ammoType;
	}

	public int getAmmoAmtNeeded() {
		return ammoAmtNeeded;
	}

	public Material getGunMaterial() {
		int id = getGunType();
		Material mat = Material.getMaterial(id);
		if (mat != null)
			return mat;
		System.out.println("Gun material is null in: " + gunName + " with typeID: " + id);
		return null;
	}

	public int getGunType() {
		return gunType;
	}

	public double getExplodeRadius() {
		return explodeRadius;
	}

	public double getFireRadius() {
		return fireRadius;
	}

	public boolean isThrowable() {
		return isThrowable;
	}

	public void setName(String val) {
		val = val.replace("&", "§");
		gunName = val;
	}

	public int getValueFromString(String str) {
		if (str.contains(":")) {
			String news = str.substring(0, str.indexOf(":"));
			return Integer.parseInt(news);
		}
		return Integer.parseInt(str);
	}

	public byte getByteDataFromString(String str) {
		if (str.contains(":")) {
			String news = str.substring(str.indexOf(":") + 1, str.length());
			return Byte.parseByte(news);
		}
		return -1;
	}

	public void setGunType(String val) {
		gunType = getValueFromString(val);
		gunByte = getByteDataFromString(val);
		if (gunByte == -1) {
			ignoreItemData = true;
			gunByte = 0;
		}
	}

	public void setAmmoType(String val) {
		ammoType = getValueFromString(val);
		ammoByte = getByteDataFromString(val);
		if (ammoByte == -1)
			ammoByte = 0;
	}

	public void setAmmoAmountNeeded(int parseInt) {
		ammoAmtNeeded = parseInt;
	}

	public void setRoundsPerBurst(int parseInt) {
		roundsPerBurst = parseInt;
	}

	public void setBulletsPerClick(int parseInt) {
		bulletsPerClick = parseInt;
	}

	public void setBulletSpeed(double parseDouble) {
		bulletSpeed = parseDouble;
	}

	public void setAccuracy(double parseDouble) {
		accuracy = parseDouble;
	}

	public void setAccuracyAimed(double parseDouble) {
		accuracy_aimed = parseDouble;
	}

	public void setAccuracyCrouched(double parseDouble) {
		accuracy_crouched = parseDouble;
	}

	public void setExplodeRadius(double parseDouble) {
		explodeRadius = parseDouble;
	}

	public void setFireRadius(double parseDouble) {
		fireRadius = parseDouble;
	}

	public void setCanHeadshot(boolean parseBoolean) {
		canHeadshot = parseBoolean;
	}

	public void setCanClickLeft(boolean parseBoolean) {
		canClickLeft = parseBoolean;
	}

	public void setCanClickRight(boolean parseBoolean) {
		canClickRight = parseBoolean;
	}

	public void clear() {
		owner = null;
	}

	public void setReloadTime(int parseInt) {
		reloadTime = parseInt;
	}

	public int getReloadTime() {
		return reloadTime;
	}

	public int getGunDamage() {
		return gunDamage;
	}

	public void setGunDamage(int parseInt) {
		gunDamage = parseInt;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int i) {
		maxDistance = i;
	}

	public boolean canAimLeft() {
		return canAimLeft;
	}

	public boolean canAimRight() {
		return canAimRight;
	}

	public void setCanAimLeft(boolean parseBoolean) {
		canAimLeft = parseBoolean;
	}

	public void setCanAimRight(boolean parseBoolean) {
		canAimRight = parseBoolean;
	}

	public void setOutOfAmmoMessage(String val) {
		val = ChatColor.translateAlternateColorCodes('&', val);
		outOfAmmoMessage = val;
	}

	public void setPermissionMessage(String val) {
		val = ChatColor.translateAlternateColorCodes('&', val);
		permissionMessage = val;
	}

	public void setFlashRadius(double parseDouble) {
		flashRadius = parseDouble;
	}

	public double getFlashRadius() {
		return flashRadius;
	}

	public void setIsThrowable(boolean b) {
		isThrowable = b;
	}

	public boolean canHeadShot() {
		return canHeadshot;
	}

	public boolean hasSmokeTrail() {
		return hasSmokeTrail;
	}

	public void setSmokeTrail(boolean b) {
		hasSmokeTrail = b;
	}

	public boolean isLocalGunSound() {
		return localGunSound;
	}

	public void setLocalGunSound(boolean b) {
		localGunSound = b;
	}

	public void setArmorPenetration(int parseInt) {
		armorPenetration = parseInt;
	}

	public int getArmorPenetration() {
		return armorPenetration;
	}

	public void setExplosionDamage(int i) {
		explosionDamage = i;
	}

	public int getExplosionDamage() {
		return explosionDamage;
	}

	public String getFilename() {
		return fileName;
	}

	public void setFilename(String string) {
		fileName = string;
	}

	public void setGunTypeByte(byte b) {
		gunByte = b;
	}

	public byte getGunTypeByte() {
		return gunByte;
	}

	public void setAmmoTypeByte(byte b) {
		ammoByte = b;
	}

	public byte getAmmoTypeByte() {
		return ammoByte;
	}

	public void setRecoil(double d) {
		recoil = d;
	}

	public double getRecoil() {
		return recoil;
	}

	public void setKnockback(double d) {
		knockback = d;
	}

	public double getKnockback() {
		return knockback;
	}

	public void addGunSounds(String val) {
		String[] sounds = val.split(",");
		for (String sound : sounds)
			gunSound.add(sound);
	}

	public int getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(int v) {
		releaseTime = v;
	}

	public void setCanGoPastMaxDistance(boolean parseBoolean) {
		canGoPastMaxDistance = parseBoolean;
	}

	public boolean canGoPastMaxDistance() {
		return canGoPastMaxDistance;
	}

	public void setGunVolume(double parseDouble) {
		gunVolume = parseDouble;
	}

	public double getGunVolume() {
		return gunVolume;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public double getAccuracy_aimed() {
		return accuracy_aimed;
	}

	public double getAccuracy_crouched() {
		return accuracy_crouched;
	}
}
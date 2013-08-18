package main.java.orange451.pvpgunplus.gun;

import java.util.ArrayList;

import main.java.net.aemservers.MTD.MTD;
import main.java.orange451.pvpgunplus.PVPGunExplosion;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Bullet {
	private int ticks;
	private int releaseTime;
	private boolean dead = false;
	private boolean active = true;
	private boolean destroyNextTick = false;
	private boolean released = false;
	private Entity projectile;
	private Vector velocity;
	private Location lastLocation;
	private Location startLocation;
	private GunPlayer shooter;
	private Gun shotFrom;

	public Bullet(GunPlayer owner, Vector vec, Gun gun) {
		shotFrom = gun;
		shooter = owner;
		velocity = vec;

		if (gun.isThrowable()) {
			ItemStack thrown = new ItemStack(gun.getGunType(), 1, gun.getGunTypeByte());
			projectile = owner.getPlayer().getWorld().dropItem(owner.getPlayer().getEyeLocation(), thrown);
			((Item) projectile).setPickupDelay(9999999);
			startLocation = projectile.getLocation();
		} else {
			Class<? extends Projectile> mclass = Snowball.class;
			String check = gun.projType.replace(" ", "").replace("_", "");
			if (check.equalsIgnoreCase("egg"))
				mclass = Egg.class;
			if (check.equalsIgnoreCase("arrow"))
				mclass = Arrow.class;
			projectile = owner.getPlayer().launchProjectile(mclass);
			((Projectile) projectile).setShooter(owner.getPlayer());
			startLocation = projectile.getLocation();
		}

		if (shotFrom.getReleaseTime() == -1)
			releaseTime = 80 + (!gun.isThrowable() ? 1 : 0) * 400;
		else
			releaseTime = shotFrom.getReleaseTime();
	}

	public void tick() {
		if (!dead) {
			ticks += 1;
			if (projectile != null) {
				lastLocation = projectile.getLocation();

				if (ticks > releaseTime) {
					EffectType eff = shotFrom.releaseEffect;
					if (eff != null)
						eff.start(lastLocation);
					dead = true;
					return;
				}

				if (shotFrom.hasSmokeTrail())
					lastLocation.getWorld().playEffect(lastLocation, Effect.SMOKE, 0);

				if (shotFrom.isThrowable() && ticks == 90) {
					remove();
					return;
				}

				if (active) {
					if (lastLocation.getWorld().equals(startLocation.getWorld())) {
						double dis = lastLocation.distance(startLocation);
						if (dis > shotFrom.getMaxDistance()) {
							active = false;
							if (!shotFrom.isThrowable() && !shotFrom.canGoPastMaxDistance())
								velocity.multiply(0.25D);
						}
					}
					projectile.setVelocity(velocity);
				}
			} else
				dead = true;
			if (ticks > 200)
				dead = true;
		} else
			remove();

		if (destroyNextTick)
			dead = true;
	}

	public Gun getGun() {
		return shotFrom;
	}

	public GunPlayer getShooter() {
		return shooter;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void remove() {
		dead = true;
		MTD.instance.getPVPGuns().removeBullet(this);
		projectile.remove();
		onHit();
		destroy();
	}

	public void onHit() {
		if (released)
			return;
		released = true;
		if (projectile != null) {
			lastLocation = projectile.getLocation();

			if (shotFrom != null) {
				int rad = (int) shotFrom.getExplodeRadius();
				int rad2 = rad;
				if (shotFrom.getFireRadius() > rad) {
					rad = (int) shotFrom.getFireRadius();
					rad2 = 2;
					for (int i = -rad; i <= rad; i++)
						for (int ii = -rad2 / 2; ii <= rad2 / 2; ii++)
							for (int iii = -rad; iii <= rad; iii++) {
								Location nloc = lastLocation.clone().add(i, ii, iii);
								if (nloc.distance(lastLocation) <= rad && MTD.instance.getPVPGuns().random.nextInt(5) == 1)
									lastLocation.getWorld().playEffect(nloc, Effect.MOBSPAWNER_FLAMES, 2);
							}
				} else if (rad > 0) {
					for (int i = -rad; i <= rad; i++)
						for (int ii = -rad2 / 2; ii <= rad2 / 2; ii++)
							for (int iii = -rad; iii <= rad; iii++) {
								Location nloc = lastLocation.clone().add(i, ii, iii);
								if (nloc.distance(lastLocation) <= rad && MTD.instance.getPVPGuns().random.nextInt(10) == 1)
									new PVPGunExplosion(nloc).explode();
							}
					new PVPGunExplosion(lastLocation).explode();
				}

				explode();
				fireSpread();
				flash();
			}
		}
	}

	public void explode() {
		if (shotFrom.getExplodeRadius() > 0.0D) {
			lastLocation.getWorld().createExplosion(lastLocation, 0.0F);

			if (shotFrom.isThrowable())
				projectile.teleport(projectile.getLocation().add(0.0D, 1.0D, 0.0D));
			int c = (int) shotFrom.getExplodeRadius();
			ArrayList<Entity> entities = (ArrayList<Entity>) projectile.getNearbyEntities(c, c, c);
			for (int i = 0; i < entities.size(); i++)
				if (entities.get(i) instanceof LivingEntity && ((LivingEntity) entities.get(i)).hasLineOfSight(projectile)) {
					double dmg = shotFrom.getExplosionDamage();
					if (dmg == -1.0D)
						dmg = shotFrom.getGunDamage();
					((LivingEntity) entities.get(i)).setLastDamage(0.0D);
					((LivingEntity) entities.get(i)).damage(dmg, shooter.getPlayer());
					((LivingEntity) entities.get(i)).setLastDamage(0.0D);
				}
		}
	}

	public void fireSpread() {
		if (shotFrom.getFireRadius() > 0.0D) {
			lastLocation.getWorld().playSound(lastLocation, Sound.GLASS, 20.0F, 20.0F);
			int c = (int) shotFrom.getFireRadius();
			ArrayList<Entity> entities = (ArrayList<Entity>) projectile.getNearbyEntities(c, c, c);
			for (int i = 0; i < entities.size(); i++)
				if (entities.get(i) instanceof LivingEntity) {
					EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(shooter.getPlayer(), entities.get(i), DamageCause.CUSTOM,
							0.0D);

					Bukkit.getServer().getPluginManager().callEvent(e);
					if (!e.isCancelled() && ((LivingEntity) entities.get(i)).hasLineOfSight(projectile)) {
						((LivingEntity) entities.get(i)).setFireTicks(140);
						((LivingEntity) entities.get(i)).setLastDamage(0.0D);
						((LivingEntity) entities.get(i)).damage(1.0D, shooter.getPlayer());
					}
				}
		}
	}

	public void flash() {
		if (shotFrom.getFlashRadius() > 0.0D) {
			lastLocation.getWorld().playSound(lastLocation, Sound.SPLASH, 20.0F, 20.0F);
			int c = (int) shotFrom.getFlashRadius();
			ArrayList<Entity> entities = (ArrayList<Entity>) projectile.getNearbyEntities(c, c, c);
			for (int i = 0; i < entities.size(); i++)
				if (entities.get(i) instanceof LivingEntity) {
					EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(shooter.getPlayer(), entities.get(i), DamageCause.CUSTOM,
							0.0D);

					Bukkit.getServer().getPluginManager().callEvent(e);
					if (!e.isCancelled() && ((LivingEntity) entities.get(i)).hasLineOfSight(projectile))
						((LivingEntity) entities.get(i)).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 140, 1));
				}
		}
	}

	public void destroy() {
		projectile = null;
		velocity = null;
		shotFrom = null;
		shooter = null;
	}

	public Entity getProjectile() {
		return projectile;
	}

	public void setNextTickDestroy() {
		destroyNextTick = true;
	}
}
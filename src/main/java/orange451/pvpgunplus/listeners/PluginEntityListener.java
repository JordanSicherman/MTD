package main.java.orange451.pvpgunplus.listeners;

import main.java.net.teepee.MTD.MTD;
import main.java.net.teepee.MTD.PVPGunsPlus;
import main.java.orange451.pvpgunplus.events.PVPGunPlusBulletCollideEvent;
import main.java.orange451.pvpgunplus.events.PVPGunPlusGunDamageEntityEvent;
import main.java.orange451.pvpgunplus.events.PVPGunPlusGunKillEntityEvent;
import main.java.orange451.pvpgunplus.gun.Bullet;
import main.java.orange451.pvpgunplus.gun.Gun;
import main.java.orange451.pvpgunplus.gun.GunPlayer;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class PluginEntityListener implements Listener {
	MTD plugin;

	public PluginEntityListener(MTD plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile check = event.getEntity();
		Bullet bullet = plugin.getPVPGuns().getBullet(check);
		if (bullet != null) {
			bullet.onHit();
			bullet.setNextTickDestroy();
			Projectile p = event.getEntity();
			Block b = p.getLocation().getBlock();
			int id = b.getTypeId();
			for (double i = 0.2D; i < 4.0D; i += 0.2D)
				if (id == 0) {
					b = p.getLocation().add(p.getVelocity().normalize().multiply(i)).getBlock();
					id = b.getTypeId();
				}
			if (id > 0)
				p.getLocation().getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, id);

			PVPGunPlusBulletCollideEvent evv = new PVPGunPlusBulletCollideEvent(bullet.getShooter(), bullet.getGun(), b);
			plugin.getServer().getPluginManager().callEvent(evv);
			event.getEntity().remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity dead = event.getEntity();
		if (dead.getLastDamageCause() != null) {
			EntityDamageEvent e = dead.getLastDamageCause();
			if (e instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent ede = (EntityDamageByEntityEvent) e;
				Entity damager = ede.getDamager();
				if (damager instanceof Projectile) {
					Projectile proj = (Projectile) damager;
					Bullet bullet = plugin.getPVPGuns().getBullet(proj);
					if (bullet != null) {
						Gun used = bullet.getGun();
						GunPlayer shooter = bullet.getShooter();

						PVPGunPlusGunKillEntityEvent pvpgunkill = new PVPGunPlusGunKillEntityEvent(shooter, used, dead);
						plugin.getServer().getPluginManager().callEvent(pvpgunkill);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		Entity damager = event.getDamager();
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity hurt = (LivingEntity) event.getEntity();
			if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				Bullet bullet = plugin.getPVPGuns().getBullet(proj);
				if (bullet != null) {
					boolean headshot = false;
					if (isNear(proj.getLocation(), hurt.getEyeLocation(), 0.26D) && bullet.getGun().canHeadShot())
						headshot = true;
					PVPGunPlusGunDamageEntityEvent pvpgundmg = new PVPGunPlusGunDamageEntityEvent(event, bullet.getShooter(),
							bullet.getGun(), event.getEntity(), headshot);

					plugin.getServer().getPluginManager().callEvent(pvpgundmg);
					if (!pvpgundmg.isCancelled()) {
						double damage = pvpgundmg.getDamage();
						double mult = 1.0D;
						if (pvpgundmg.isHeadshot()) {
							PVPGunsPlus.playEffect(Effect.ZOMBIE_DESTROY_DOOR, hurt.getLocation(), 3);
							mult = 2.0D;
						}
						hurt.setLastDamage(0.0D);
						event.setDamage(Math.ceil(damage * mult));
						int armorPenetration = bullet.getGun().getArmorPenetration();
						if (armorPenetration > 0) {
							double health = hurt.getHealth();
							double newHealth = health - armorPenetration;
							if (newHealth < 0.0D)
								newHealth = 0.0D;
							if (newHealth > 20.0D)
								newHealth = 20.0D;
							hurt.setHealth(newHealth);
						}

						bullet.getGun().doKnockback(hurt, bullet.getVelocity());

						bullet.remove();
					} else
						event.setCancelled(true);
				}
			}
		}
	}

	private boolean isNear(Location location, Location eyeLocation, double d) {
		return Math.abs(location.getY() - eyeLocation.getY()) <= d;
	}
}
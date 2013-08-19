package main.java.net.aemservers.MTD.Listeners;

import java.util.Random;

import main.java.net.aemservers.MTD.MTD;
import main.java.net.aemservers.MTD.MobDisguise;
import main.java.net.aemservers.MTD.Automation.Game;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class DeathDamage implements Listener {

	@EventHandler
	private void onRespawn(final PlayerRespawnEvent e) {
		final Game g = Game.getGame(e.getPlayer());
		if (g != null && g.hasStarted())
			g.setTeam(e.getPlayer(), 0);
		if (g.isAlive(e.getPlayer()))
			if (g.getScore() != null)
				g.getScore().setScore(g.getScore().getScore() - 1);

		MTD.instance.getServer().getScheduler().runTaskLater(MTD.instance, new Runnable() {
			@Override
			public void run() {
				MobDisguise.disguise(e.getPlayer());
				e.getPlayer().teleport(g.getMobSpawn());
				g.stop(false);
			}
		}, 1L);
	}

	@EventHandler
	private void onDeath(PlayerDeathEvent e) {
		Game g = Game.getGame(e.getEntity());
		if (g != null && g.hasStarted()) {
			e.getDrops().clear();

			if (!g.isAlive(e.getEntity()) && MobDisguise.getDisguise(e.getEntity()) == DisguiseType.Creeper)
				e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 3f);
			if (e.getEntity().getKiller() != null && e.getEntity().getKiller().getName().equals("MrTeePee"))
				if (g.getMrTeePeeScore() == null)
					g.getMrTeePeeScore().setScore(g.getMrTeePeeScore().getScore() + 1);
		}
	}

	/**
	 * Deal extra disguise related damage and effects.
	 * 
	 * @param e
	 */
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		Entity damaged = e.getEntity();
		Entity damager = e.getDamager();
		if (damager instanceof Snowball) {
			if (((Projectile) damager).getShooter() instanceof Player)
				if (damaged instanceof Player) {
					Game g;
					if ((g = Game.getGame((Player) damaged)) != null)
						if (!g.isAlive((Player) ((Projectile) damager).getShooter())
								&& MobDisguise.getDisguise((Player) ((Projectile) damager).getShooter()) == DisguiseType.Snowman)
							e.setDamage((e.getDamage() + 1) * 3);
				}
		} else if (damager instanceof Player)
			if (damaged instanceof Player) {
				Game g;
				if ((g = Game.getGame((Player) damager)) != null)
					if (!g.isAlive((Player) damager)) {
						Random r = new Random();
						DisguiseType disguise = MobDisguise.getDisguise((Player) damager);
						switch (disguise) {
						case CaveSpider:
							if (r.nextInt(3) == 1)
								((Player) damaged).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
							break;
						case Wither:
							if (r.nextInt(3) == 1)
								((Player) damaged).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
						case Blaze:
						case Creeper:
						case Enderman:
						case Ghast:
						case Giant:
						case IronGolem:
						case MagmaCube:
						case PigZombie:
						case Silverfish:
						case Skeleton:
						case Snowman:
						case Spider:
						case Witch:
						case Wolf:
						case Zombie:
							break;
						}
					}
			}
	}
}

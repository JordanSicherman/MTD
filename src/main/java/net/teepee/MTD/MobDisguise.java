package main.java.net.teepee.MTD;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class MobDisguise {

	private static final List<DisguiseType> validEntities = generateEntities();
	private static final Random r = new Random();

	private static List<DisguiseType> generateEntities() {
		List<DisguiseType> valid = new ArrayList<DisguiseType>();
		valid.add(DisguiseType.Blaze);
		valid.add(DisguiseType.CaveSpider);
		valid.add(DisguiseType.Creeper);
		valid.add(DisguiseType.Enderman);
		valid.add(DisguiseType.Ghast);
		valid.add(DisguiseType.Giant);
		valid.add(DisguiseType.IronGolem);
		valid.add(DisguiseType.MagmaCube);
		valid.add(DisguiseType.PigZombie);
		valid.add(DisguiseType.Silverfish);
		valid.add(DisguiseType.Skeleton);
		valid.add(DisguiseType.Snowman);
		valid.add(DisguiseType.Spider);
		valid.add(DisguiseType.Witch);
		valid.add(DisguiseType.Wither);
		valid.add(DisguiseType.Wolf);
		valid.add(DisguiseType.Zombie);

		// valid.add(DisguiseType.FallingBlock); // Placeholder for Horseman.
		return valid;
	}

	public static List<Player> alive(List<Player> input) {
		List<Player> alive = new ArrayList<Player>();
		for (Player p : input)
			if (!DisguiseCraft.getAPI().isDisguised(p))
				alive.add(p);
		return alive;
	}

	public static List<Player> dead(List<Player> input) {
		List<Player> dead = new ArrayList<Player>();
		for (Player p : input)
			if (DisguiseCraft.getAPI().isDisguised(p))
				dead.add(p);
		return dead;
	}

	public static void disguise(Player player, int entityId, DisguiseType type) {
		DisguiseCraft.getAPI().undisguisePlayer(player);
		DisguiseCraft.getAPI().disguisePlayer(player, new Disguise(entityId, type));
		/*
		 * Armor and buff the mob
		 */
		player.getInventory().setContents(new ItemStack[player.getInventory().getSize()]);
		switch (type) {
		case Blaze:
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 10));
			ItemStack blazebow = new ItemStack(Material.BOW, 1);
			blazebow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 8);
			blazebow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
			player.getInventory().addItem(blazebow);
			player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			break;
		case CaveSpider:
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 6));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
			ItemStack cavespiderboots = new ItemStack(Material.DIAMOND_BOOTS, 1);
			cavespiderboots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 10);
			Potion cavepoison = new Potion(PotionType.POISON);
			cavepoison.setHasExtendedDuration(true);
			cavepoison.setLevel(2);
			cavepoison.setSplash(true);
			player.getInventory().addItem(cavepoison.toItemStack(32));
			player.getInventory().setBoots(cavespiderboots);
			break;
		case Creeper:
			player.getInventory().addItem(new ItemStack(Material.TNT, 32));
			player.getInventory().addItem(new ItemStack(Material.LEVER, 32));
			break;
		case Enderman:
			player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 16));
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 10));
			break;
		case Ghast:
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 10));
			ItemStack ghastbow = new ItemStack(Material.BOW, 1);
			ghastbow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 5);
			ghastbow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
			player.getInventory().addItem(ghastbow);
			player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			break;
		case Giant:
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 5));
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 5));
			break;
		case IronGolem:
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 7));
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 5));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));
			ItemStack rose = new ItemStack(Material.RED_ROSE, 1);
			rose.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
			player.getInventory().addItem(rose);
			SpawnEgg villagerEgg = new SpawnEgg();
			villagerEgg.setSpawnedType(EntityType.VILLAGER);
			player.getInventory().addItem(villagerEgg.toItemStack(16));
			break;
		case MagmaCube:
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3));
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 10));
			break;
		case PigZombie:
			ItemStack sword = new ItemStack(Material.GOLD_SWORD, 1);
			sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
			player.getInventory().addItem(sword);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
			break;
		case Silverfish:
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10));
			player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 2));
			break;
		case Skeleton:
			ItemStack skelebow = new ItemStack(Material.BOW, 1);
			skelebow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
			skelebow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
			skelebow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
			player.getInventory().addItem(skelebow);
			player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			break;
		case Snowman:
			player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
			player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
			player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
			player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
			break;
		case Spider:
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 6));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
			ItemStack spiderboots = new ItemStack(Material.DIAMOND_BOOTS, 1);
			spiderboots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 10);
			player.getInventory().setBoots(spiderboots);
			break;
		case Witch:
			Potion poison = new Potion(PotionType.POISON);
			poison.setHasExtendedDuration(true);
			poison.setLevel(2);
			poison.setSplash(true);
			Potion damage = new Potion(PotionType.INSTANT_DAMAGE);
			damage.setLevel(2);
			damage.setSplash(true);
			player.getInventory().addItem(poison.toItemStack(16));
			player.getInventory().addItem(damage.toItemStack(16));
			break;
		case Wither:
			ItemStack witherchest = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
			witherchest.addUnsafeEnchantment(Enchantment.THORNS, 5);
			player.getInventory().setChestplate(witherchest);
			player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
			break;
		case Wolf:
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 4));
			break;
		case Zombie:
			player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 1));
			player.getInventory().addItem(new ItemStack(Material.IRON_SPADE, 1));
			break;
		default:
			MTD.debug("Case " + type + " not specified.");
			break;
		}
		player.getInventory().setHeldItemSlot(0);
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.sendMessage("You are a "
				+ type.toString().replaceAll("PigZombie", "Zombie Pigman").replaceAll("CaveSpider", "Cave Spider")
						.replaceAll("MagmaCube", "Magma Cube").replaceAll("IronGolem", "Iron Golem").replaceAll("FallingBlock", "Horseman")
						.toLowerCase() + ".");
	}

	public static void disguise(Player player) {
		DisguiseType type = getRandomDisguise(player);
		
		/*
		 * Horseman code. Non functioning as no convenient method to check if player is dead. Also would run into problems with horse ownership.
		 */
		if (type == DisguiseType.FallingBlock) {
			Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
			horse.setAdult();
			horse.setOwner(player);
			horse.setVariant(Variant.SKELETON_HORSE);
			horse.setPassenger(player);
			horse.setJumpStrength(horse.getJumpStrength() * 1.5);

			player.getInventory().setContents(new ItemStack[player.getInventory().getSize()]);

			ItemStack bow = new ItemStack(Material.BOW, 1);
			bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
			bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
			player.getInventory().addItem(bow);
			player.getInventory().addItem(new ItemStack(Material.ARROW, 1));

			player.getInventory().setHeldItemSlot(0);
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			player.setSaturation(20);
			player.sendMessage("You are a "
					+ type.toString().replaceAll("PigZombie", "Zombie Pigman").replaceAll("CaveSpider", "Cave Spider")
							.replaceAll("MagmaCube", "Magma Cube").replaceAll("IronGolem", "Iron Golem")
							.replaceAll("FallingBlock", "Horseman").toLowerCase() + ".");
			return;
		}
		disguise(player, type.id, type);
	}

	public static List<DisguiseType> getApplicableKits(Player p) {
		List<DisguiseType> applicable = new ArrayList<DisguiseType>();
		for (DisguiseType t : validEntities)
			if (p.hasPermission("MTD." + t.toString().replaceAll("FallingBlock", "Horseman").toLowerCase()))
				applicable.add(t);
		return applicable;
	}

	private static DisguiseType getRandomDisguise(Player p) {
		List<DisguiseType> applicable = getApplicableKits(p);
		return applicable.get(r.nextInt(applicable.size()));
	}

	public static boolean isAlive(Player p) {
		return !DisguiseCraft.getAPI().isDisguised(p);
	}

	public static boolean isDead(Player p) {
		return DisguiseCraft.getAPI().isDisguised(p);
	}

	public static void undisguise(List<Player> players) {
		for (Player p : players)
			DisguiseCraft.getAPI().undisguisePlayer(p);
	}

	public static void undisguise(Player player) {
		DisguiseCraft.getAPI().undisguisePlayer(player);
	}

	public static DisguiseType getDisguise(Player player) {
		if (isDead(player))
			return DisguiseCraft.getAPI().getDisguise(player).type;
		return null;
	}
}

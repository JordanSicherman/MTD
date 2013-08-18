package main.java.orange451.pvpgunplus;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class PVPGunExplosion {
	private final Location location;
	Random rand = new Random();

	public PVPGunExplosion(Location location) {
		this.location = location;
	}

	public void explode() {
		net.minecraft.server.v1_6_R2.World world = ((CraftWorld) location.getWorld()).getHandle();
		Firework bfirework = location.getWorld().spawn(location, Firework.class);
		bfirework.setFireworkMeta((FireworkMeta) getFirework().getItemMeta());
		CraftFirework a = (CraftFirework) bfirework;
		world.broadcastEntityEffect(a.getHandle(), (byte) 17);
		bfirework.remove();
	}

	public ItemStack getFirework() {
		FireworkEffect.Type type = FireworkEffect.Type.BALL_LARGE;
		if (rand.nextInt(2) == 0)
			type = FireworkEffect.Type.BURST;
		ItemStack i = new ItemStack(Material.FIREWORK, 1);
		FireworkMeta fm = (FireworkMeta) i.getItemMeta();
		ArrayList<Color> c = new ArrayList<Color>();
		c.add(Color.RED);
		c.add(Color.RED);
		c.add(Color.RED);
		c.add(Color.ORANGE);
		c.add(Color.ORANGE);
		c.add(Color.ORANGE);
		c.add(Color.BLACK);
		c.add(Color.GRAY);
		FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(type).trail(true).build();
		fm.addEffect(e);
		fm.setPower(3);
		i.setItemMeta(fm);

		return i;
	}
}

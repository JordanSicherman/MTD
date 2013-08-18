package main.java.net.aemservers.MTD.Commands;

import java.util.Random;

import main.java.net.aemservers.MTD.MTD;
import main.java.net.aemservers.MTD.MobDisguise;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class Classes implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			ChatColor curCol = ChatColor.DARK_GRAY;
			sender.sendMessage(curCol + "You own:");
			long delay = 0L;
			final long step = MTD.instance.getConfig().getLong("time_delay");
			for (DisguiseType t : MobDisguise.getApplicableKits((Player) sender)) {
				curCol = alternate(curCol);
				String msg = curCol
						+ t.toString().replaceAll("PigZombie", "Zombie Pigman").replaceAll("CaveSpider", "Cave Spider")
								.replaceAll("MagmaCube", "Magma Cube").replaceAll("IronGolem", "Iron Golem")
								.replaceAll("FallingBlock", "Horseman");
				MTD.instance.getServer().getScheduler().runTaskLaterAsynchronously(MTD.instance, new MessageSender(sender, msg), delay);
				delay += step;
			}
			delay += 20L;
			MTD.instance
					.getServer()
					.getScheduler()
					.runTaskLaterAsynchronously(
							MTD.instance,
							new MessageSender(sender, ChatColor.YELLOW + getRandomMessage() + ChatColor.ITALIC
									+ MTD.instance.getConfig().getString("buycraft")), delay);
		}
		return true;
	}

	private class MessageSender implements Runnable {
		final String msg;
		final CommandSender sender;

		public MessageSender(CommandSender sender, String msg) {
			this.msg = msg;
			this.sender = sender;
		}

		@Override
		public void run() {
			sender.sendMessage(msg);
		}
	}

	private String getRandomMessage() {
		Random r = new Random();
		switch (r.nextInt(7)) {
		case 0:
			return "Get access to more classes at ";
		case 1:
			return "You could be playing as the " + ChatColor.GOLD + "WITHER" + ChatColor.YELLOW + ". ";
		case 2:
			return "More mobs means more fun! ";
		case 3:
			return "Tired of being a zombie? Donate to unlock more classes! ";
		case 4:
			return "Being a zombie is so last week. Discover more classes at ";
		case 5:
			return "Did you know there are GIANTS in Minecraft? Get access to the 12 block tall wonder at ";
		case 6:
			return "Get the bleeding edge by donating. ";
		default:
			return "Get access to more classes at ";
		}
	}

	private ChatColor alternate(ChatColor cur) {
		if (cur == ChatColor.DARK_GRAY)
			return ChatColor.GRAY;
		return ChatColor.DARK_GRAY;
	}
}

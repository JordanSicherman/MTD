package main.java.orange451.pvpgunplus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper {
	public static int amtItem(Inventory inventory, int id, byte data) {
		int amount = 0;
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for (ItemStack item : items)
				if (item != null) {
					int tid = item.getTypeId();
					int iData = item.getData().getData();
					int amt = item.getAmount();
					if (id == tid && (data == iData || data == -1))
						amount += amt;
				}
		}
		return amount;
	}

	public static void removeItem(Inventory inventory, int tid, byte data, int amt) {
		int start = amt;
		if (inventory != null) {
			ItemStack[] items = inventory.getContents();
			for (int slot = 0; slot < items.length; slot++)
				if (items[slot] != null) {
					int id = items[slot].getTypeId();
					int itmDat = items[slot].getData().getData();
					int itmAmt = items[slot].getAmount();
					if (id == tid && (data == itmDat || data == -1)) {
						if (amt > 0) {
							if (itmAmt >= amt) {
								itmAmt -= amt;
								amt = 0;
							} else {
								amt = start - itmAmt;
								itmAmt = 0;
							}
							if (itmAmt > 0)
								inventory.getItem(slot).setAmount(itmAmt);
							else
								inventory.setItem(slot, null);
						}
						if (amt <= 0)
							return;
					}
				}
		}
	}
}

package net.Indyuce.suddendeath.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.Indyuce.suddendeath.gui.PluginInventory;

public class GuiListener implements Listener {
	@EventHandler
	public void a(InventoryClickEvent event) {
		if (event.getInventory().getHolder() instanceof PluginInventory)
			((PluginInventory) event.getInventory().getHolder()).whenClicked(event);
	}
}

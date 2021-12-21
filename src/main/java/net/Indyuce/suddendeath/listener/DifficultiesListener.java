package net.Indyuce.suddendeath.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import net.Indyuce.suddendeath.player.PlayerData;

public class DifficultiesListener implements Listener {
	@EventHandler
	public void a(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && !event.getEntity().hasMetadata("NPC") && !event.isCancelled())
			event.setDamage(event.getDamage() * PlayerData.get((Player) event.getEntity()).getDifficulty().getDamageMultiplier());
	}
}
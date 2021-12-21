package net.Indyuce.suddendeath.comp;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.suddendeath.player.PlayerData;
import net.Indyuce.suddendeath.util.Utils;

public class SuddenDeathPlaceholders extends PlaceholderExpansion {
	@Override
	public String getAuthor() {
		return "Indyuce";
	}

	@Override
	public String getIdentifier() {
		return "suddendeath";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	public String onPlaceholderRequest(Player player, String identifier) {
		if (identifier.equalsIgnoreCase("bleeding"))
			return Utils.msg(PlayerData.get(player).isBleeding() ? "papi-bleeding" : "papi-not-bleeding");
		if (identifier.equalsIgnoreCase("infection"))
			return Utils.msg(PlayerData.get(player).isInfected() ? "papi-infected" : "papi-not-infected");
		return null;
	}
}

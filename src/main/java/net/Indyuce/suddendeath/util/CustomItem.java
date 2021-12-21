package net.Indyuce.suddendeath.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CustomItem {
	BANDAGE(Material.PAPER, "Bandage", new String[] { "Stops &nBleeding&r." }, new String[] { "AIR,AIR,AIR", "PAPER,STICK,PAPER", "AIR,AIR,AIR" }),
	STRANGE_BREW(Material.MUSHROOM_STEW, "Strange Brew", new String[] { "Stops &nInfection&r." }, new String[] { "AIR,AIR,AIR", "INK_SAC,BOWL,CLAY_BALL", "AIR,AIR,AIR" }),
	RAW_HUMAN_FLESH(Material.BEEF, "Human Flesh", new String[] { "Some fresh human meet.", "I wonder if I can cook it?" }),
	HUMAN_BONE(Material.BONE, "Human Bone"),
	COOKED_HUMAN_FLESH(Material.COOKED_BEEF, "Cooked Human Flesh", new String[] { "Looks tasty!" }),
	SHARP_KNIFE(Material.IRON_SWORD, "Sharp Knife", new String[] { "A super sharp knife.", "Hit someone to make him bleed." }),;

	public Material material;
	private String name;
	public String[] lore;
	public String[] craft;

	private CustomItem(Material material, String name) {
		this(material, name, new String[0], null);
	}

	private CustomItem(Material material, String name, String[] lore) {
		this(material, name, lore, null);
	}

	private CustomItem(Material material, String name, String[] lore, String[] craft) {
		this.material = material;
		this.name = name;
		this.lore = lore;
		this.craft = craft;
	}

	public void update(ConfigurationSection config) {
		this.name = config.getString("name");
		this.lore = config.getStringList("lore").toArray(new String[0]);
		this.craft = config.getStringList("craft").toArray(new String[0]);
	}

	public String getDefaultName() {
		return name;
	}

	public String getName() {
		return "" + ChatColor.RESET + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', name);
	}

	public ItemStack a() {
		ItemStack i = new ItemStack(material);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(getName());
		meta.addItemFlags(ItemFlag.values());
		if (lore != null) {
			ArrayList<String> lore = new ArrayList<String>();
			for (String s : this.lore)
				lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', s));
			meta.setLore(lore);
		}
		i.setItemMeta(meta);
		return i;
	}
}
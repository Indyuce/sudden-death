package net.Indyuce.suddendeath.world;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.suddendeath.SuddenDeath;
import net.Indyuce.suddendeath.manager.EventManager.WorldStatus;

public abstract class WorldEventHandler extends BukkitRunnable implements Listener, StatusRetriever {
	private final World world;
	private final WorldStatus status;

	protected static final Random random = new Random();

	public WorldEventHandler(World world, int tick, WorldStatus status) {
		this.world = world;
		this.status = status;

		runTaskTimer(SuddenDeath.plugin, 20, tick);
		Bukkit.getPluginManager().registerEvents(this, SuddenDeath.plugin);
	}

	public World getWorld() {
		return world;
	}
	
	@Override
	public WorldStatus getStatus() {
		return status;
	}

	public void close() {
		cancel();
		HandlerList.unregisterAll(this);
	}
}

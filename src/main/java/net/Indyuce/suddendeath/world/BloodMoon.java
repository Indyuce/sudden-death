package net.Indyuce.suddendeath.world;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.suddendeath.SuddenDeath;
import net.Indyuce.suddendeath.Feature;
import net.Indyuce.suddendeath.manager.EventManager.WorldStatus;

public class BloodMoon extends WorldEventHandler {
	public BloodMoon(World world) {
		super(world, 3 * 20, WorldStatus.BLOOD_MOON);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void a(EntityDamageEvent event) {
		if (!event.getEntity().getWorld().equals(getWorld()))
			return;

		if (event.getEntity() instanceof Player) {
			event.setDamage(event.getDamage() * (1 + Feature.BLOOD_MOON.getDouble("damage-percent") / 100));
			Player player = (Player) event.getEntity();
			if (event.getDamage() < player.getHealth()) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) Feature.BLOOD_MOON.getDouble("slow-duration") * 20, 2));
				player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0, 1, 0), 64, .1, .3, .1, .2,
						Material.REDSTONE_BLOCK.createBlockData());
			}
		}
	}

	public boolean isFriendly(Entity entity) {
		return entity instanceof Creature && !(entity instanceof Monster) && !(entity instanceof Mob);
	}

	public boolean isNeutral(Entity entity) {
		return entity instanceof Creature && !(entity instanceof Monster) && entity instanceof Mob;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void b(CreatureSpawnEvent event) {
		LivingEntity entity = event.getEntity();
		if (!entity.getWorld().equals(getWorld()) || !(entity instanceof Creature))
			return;

		/*
		 * apply potion effects
		 */
		for (PotionEffectType type : new PotionEffectType[] { PotionEffectType.SPEED, PotionEffectType.INCREASE_DAMAGE,
				PotionEffectType.DAMAGE_RESISTANCE })
			entity.addPotionEffect(
					new PotionEffect(type, 1000000, (int) Feature.BLOOD_MOON.getDouble(type.getName().toLowerCase().replace("_", "-"))));

		entity.setMetadata("BloodmoonMob", new FixedMetadataValue(SuddenDeath.plugin, true));

		// effect
		new BukkitRunnable() {
			double ti = 0;
			Location loc = entity.getLocation().clone();
			Random r = new Random();

			public void run() {
				if (ti > 2) {
					cancel();
					return;
				}
				for (int j = 0; j < 2; j++) {
					ti += .12;
					for (double i = 0; i < Math.PI * 2; i += Math.PI / 8) {
						if (r.nextDouble() < .4)
							continue;
						Location loc1 = loc.clone().add(Math.cos(i) * .8, ti, Math.sin(i) * .8);
						loc1.getWorld().spawnParticle(Particle.REDSTONE, loc1, 0, new Particle.DustOptions(Color.BLACK, 1));
					}
				}
			}
		}.runTaskTimer(SuddenDeath.plugin, 0, 1);
	}

	/*
	 * creepers must lose their effects when exploding, otherwise they spread
	 * their speed or strength as lingering clouds
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void c(EntityExplodeEvent event) {
		if (event instanceof Creeper && event.getEntity().hasMetadata("BloodmoonMob"))
			for (PotionEffectType type : new PotionEffectType[] { PotionEffectType.SPEED, PotionEffectType.INCREASE_DAMAGE,
					PotionEffectType.DAMAGE_RESISTANCE })
				((Creeper) event.getEntity()).removePotionEffect(type);
	}

	@Override
	public void run() {
	}

	@Override
	public void close() {
		super.close();

		// clear potion effects from creepers
		for (Entity entity : getWorld().getEntities())
			if (entity instanceof Creeper && entity.hasMetadata("BloodmoonMob")) {
				Creeper creeper = (Creeper) entity;
				for (PotionEffectType type : new PotionEffectType[] { PotionEffectType.SPEED, PotionEffectType.INCREASE_DAMAGE,
						PotionEffectType.DAMAGE_RESISTANCE })
					creeper.removePotionEffect(type);
			}
	}
}

/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.eventhandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;

public class ProjectileEventHandler implements Listener {

	private MRC plugin;

	public ProjectileEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (plugin.gameState != GameState.INGAME)
			return;

		Location loc = event.getEntity().getLocation();

		if (event.getHitBlock() != null) {

			// Check if scored for red alliance
			if (event.getHitBlock().getType() == Material.RED_TERRACOTTA) {
				// OUTER PORT

				if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player
						&& plugin.redPlayers.contains(event.getEntity().getShooter()))
					plugin.playerData.get(event.getEntity().getShooter()).addOuter(plugin.countdown > 135);

				plugin.redScore += (plugin.countdown > 135) ? 4 : 2;
				plugin.world.playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, 100, 1);
				plugin.redPC++;

				if (plugin.blueBay < 15) {
					plugin.blueBay++;
				} else {
					plugin.arena.spawnRandomPC();
				}
				event.getEntity().remove();
				return;
			}
			if (event.getHitBlock().getType() == Material.RED_CONCRETE_POWDER) {
				// INNER PORT

				if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player
						&& plugin.redPlayers.contains(event.getEntity().getShooter()))
					plugin.playerData.get(event.getEntity().getShooter()).addInner(plugin.countdown > 135);

				plugin.redScore += (plugin.countdown > 135) ? 6 : 3;
				plugin.world.playSound(loc, Sound.BLOCK_ANVIL_LAND, 100, 1);
				plugin.redPC++;

				if (plugin.blueBay < 15) {
					plugin.blueBay++;
				} else {
					plugin.arena.spawnRandomPC();
				}
				event.getEntity().remove();
				return;
			}

			// Check if scored for blue alliance
			if (event.getHitBlock().getType() == Material.BLUE_TERRACOTTA) {
				// OUTER PORT

				if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player
						&& plugin.bluePlayers.contains(event.getEntity().getShooter()))
					plugin.playerData.get(event.getEntity().getShooter()).addOuter(plugin.countdown > 135);

				plugin.blueScore += (plugin.countdown > 135) ? 4 : 2;
				plugin.world.playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, 100, 1);
				plugin.bluePC++;

				if (plugin.redBay < 15) {
					plugin.redBay++;
				} else {
					plugin.arena.spawnRandomPC();
				}
				event.getEntity().remove();
				return;
			}
			if (event.getHitBlock().getType() == Material.BLUE_CONCRETE_POWDER) {
				// INNER PORT

				if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player
						&& plugin.bluePlayers.contains(event.getEntity().getShooter()))
					plugin.playerData.get(event.getEntity().getShooter()).addInner(plugin.countdown > 135);

				plugin.blueScore += (plugin.countdown > 135) ? 6 : 3;
				plugin.world.playSound(loc, Sound.BLOCK_ANVIL_LAND, 100, 1);
				plugin.bluePC++;

				if (plugin.redBay < 15) {
					plugin.redBay++;
				} else {
					plugin.arena.spawnRandomPC();
				}
				event.getEntity().remove();
				return;
			}

		}

		// MISSED SHOT
		plugin.arena.spawnRandomPC();
		event.getEntity().remove();
		if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)
			plugin.playerData.get(event.getEntity().getShooter()).addMiss();

	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		ProjectileSource shooter = event.getEntity().getShooter();
		if (shooter != null && shooter instanceof Player) {

			if (plugin.playerClasses.get(shooter) == PlayerClass.BOW
					|| plugin.playerClasses.get(shooter) == PlayerClass.INSTACLIMB) {
				Vector v = event.getEntity().getVelocity();
				if (plugin.redPlayers.contains(shooter)) {
					if (((Player) shooter).getLocation().getZ() < -12) {
						v.setY(0.25);
					}
				} else {
					if (((Player) shooter).getLocation().getZ() > 15) {
						v.setY(0.25);
					}
				}
				event.getEntity().setVelocity(v);
			}

		}
	}

}

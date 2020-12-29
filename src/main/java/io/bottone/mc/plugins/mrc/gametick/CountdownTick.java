/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.gametick;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;
import io.bottone.mc.plugins.mrc.managers.MRCPlayerData;
import io.bottone.mc.plugins.mrc.managers.MRCTitleManager;

public class CountdownTick {

	public static void doTick(MRC plugin) {
		// Pre-game state, starting soon.

		if (plugin.players.size() < 1) {
			// We don't have enough players ... abort the countdown!
			plugin.gameState = GameState.LOBBY;
			plugin.countdown = 0;
			plugin.joinable = true;
			plugin.arena.clearEntities();
			return;
		}
		if (plugin.countdown <= 0 && plugin.joinable) {
			// Game no longer joinable, moving to the arena.
			
			plugin.arena.clearEntities();

			for (Player player : plugin.playerPositions.keySet()) {
				Location position = plugin.playerPositions.get(player);

				// Clear inventories
				player.getInventory().remove(Material.IRON_DOOR);

				// Give players their power cell shooters
				switch (plugin.playerClasses.get(player)) {
				case INSTACLIMB:
				case BOW:
					ItemStack bow = new ItemStack(Material.BOW, 1);
					ItemMeta bowMeta = bow.getItemMeta();
					bowMeta.setUnbreakable(true);
					bowMeta.setDisplayName("Power Cell Shooter");
					bow.setItemMeta(bowMeta);
					player.getInventory().addItem(bow);
					break;
				case CROSSBOW:
					ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
					ItemMeta crossbowMeta = crossbow.getItemMeta();
					crossbowMeta.setUnbreakable(true);
					crossbowMeta.setDisplayName("Power Cell Shooter");
					crossbow.setItemMeta(crossbowMeta);
					player.getInventory().addItem(crossbow);
					break;
				case SNOWBALL: // intentionally blank
					break;
				}

				// Teleport players to their positions
				player.teleport(position);
				plugin.world.spawnEntity(position, EntityType.BOAT).addPassenger(player);

				if (plugin.redPlayers.contains(player)) {
					player.sendMessage(
							MRC.PREFIX + "You are competing on the " + ChatColor.RED + ChatColor.BOLD + "RED ALLIANCE");
				} else {
					player.sendMessage(MRC.PREFIX + "You are competing on the " + ChatColor.BLUE + ChatColor.BOLD
							+ "BLUE ALLIANCE");
				}

			}

			// Play sound
			plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BASS, 100, 1);

			plugin.joinable = false;
			plugin.countdown = 10;

		}
		if (plugin.countdown > 0 && !plugin.joinable) {
			// Final countdown.

			// Show title
			MRCTitleManager.showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + plugin.countdown,
					" ");

			// Play sound
			plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BASS, 100, 1);
		}
		if (plugin.countdown <= 0 && !plugin.joinable) {
			// Match starts.

			plugin.arena.clearEntities();
			
			// Show title
			MRCTitleManager.showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "GO",
					ChatColor.LIGHT_PURPLE + "Good luck!");

			// Play sound
			plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);

			// Time to start the match!
			plugin.gameState = GameState.INGAME;
			plugin.countdown = 150;

			for (Player player : plugin.players) {
				Entity e = player.getVehicle();
				if (e != null) {
					plugin.killedBoats.add(e.getUniqueId());
					e.remove();
				}
			}

			for (Player player : plugin.playerPositions.keySet()) {
				Location position = plugin.playerPositions.get(player);

				// Init player data
				plugin.playerData.put(player, new MRCPlayerData(player.getName()));

				// Give players their 3 starting power cells
				if (plugin.playerClasses.get(player) == PlayerClass.SNOWBALL)
					plugin.arena.givePowerCells(player, 3, Material.SNOWBALL);
				else
					plugin.arena.givePowerCells(player, 3, Material.ARROW);

				// Teleport players to their positions
				player.teleport(position);
				plugin.world.spawnEntity(position, EntityType.BOAT).addPassenger(player);
			}

			// Spawn initial power cells on the field
			ItemStack arrowStack = new ItemStack(Material.ARROW);
			ItemMeta meta = arrowStack.getItemMeta();
			meta.setDisplayName("Power Cell");
			arrowStack.setItemMeta(meta);
			for (Location loc : plugin.powerCellSpots) {
				plugin.world.dropItem(loc, arrowStack).setVelocity(new Vector(0, 0, 0));
			}

			// Setup the loading bay chest holograms
			plugin.redBayHolo = HologramsAPI.createHologram(plugin, plugin.redBayLoc);
			plugin.blueBayHolo = HologramsAPI.createHologram(plugin, plugin.blueBayLoc);

			plugin.redBayLine = plugin.redBayHolo.appendTextLine(plugin.redBay + " Power Cells");
			plugin.blueBayLine = plugin.blueBayHolo.appendTextLine(plugin.blueBay + " Power Cells");

			plugin.redBayHolo.appendItemLine(new ItemStack(Material.ARROW));
			plugin.blueBayHolo.appendItemLine(new ItemStack(Material.ARROW));

			return;
		}

		plugin.countdown--;

	}

}

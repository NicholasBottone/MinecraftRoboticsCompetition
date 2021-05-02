/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.gametick;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.managers.MRCPlayerData;
import io.bottone.mc.plugins.mrc.managers.MRCTitleManager;
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

public class CountdownTick {

	public static void doTick(MRC plugin) {
		// Pre-game state, starting soon.

		if (plugin.players.size() < 1) {
			// We don't have enough players ... abort the countdown!
			abortCountdown(plugin);
			return;
		}

		if (plugin.countdown <= 0 && plugin.joinable) {
			// Game no longer joinable, moving to the arena.
			startFinalCountdown(plugin);
		}
		if (plugin.countdown > 0 && !plugin.joinable) {
			// Final countdown.
			doFinalCountdownSequence(plugin);
		}
		if (plugin.countdown <= 0 && !plugin.joinable) {
			// Match starts.
			startMatch(plugin);
			return;
		}

		plugin.countdown--;

	}

	private static void abortCountdown(MRC plugin) {
		plugin.gameState = GameState.LOBBY;
		plugin.countdown = 0;
		plugin.joinable = true;
		plugin.arena.clearEntities();

		plugin.getServer().broadcastMessage(MRC.PREFIX + "Match countdown aborted due to lack of players.");
	}

	private static void doFinalCountdownSequence(MRC plugin) {
		// Show title
		MRCTitleManager.showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + plugin.countdown,
				" ");

		// Play sound
		plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BASS, 100, 1);
	}

	private static void startMatch(MRC plugin) {
		plugin.arena.clearEntities();

		// Show title
		MRCTitleManager.showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "GO",
				ChatColor.LIGHT_PURPLE + "Good luck!");

		// Play sound
		plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);

		// Time to start the match!
		plugin.gameState = GameState.INGAME;
		plugin.countdown = 150;
		plugin.getServer().broadcastMessage(MRC.PREFIX + "Let the match begin!");

		// Prepare all players for the match
		preparePlayersForMatch(plugin);

		// Spawn initial power cells on the field
		spawnFieldPowerCells(plugin);

		// Setup the loading bay chest holograms
		setupLoadingBayHolograms(plugin);
	}

	private static void preparePlayersForMatch(MRC plugin) {
		for (Player player : plugin.playerPositions.keySet()) {
			Location position = plugin.playerPositions.get(player);

			// Kill player's old boat
			Entity e = player.getVehicle();
			if (e != null) {
				plugin.killedBoats.add(e.getUniqueId());
				e.remove();
			}

			// Init player data
			plugin.playerData.put(player, new MRCPlayerData(player.getName()));

			// Give players their 3 starting power cells
			plugin.arena.givePowerCells(player, 3);

			// Teleport players to their positions and spawn new boat
			player.teleport(position);
			plugin.world.spawnEntity(position, EntityType.BOAT).addPassenger(player);
		}
	}

	private static void setupLoadingBayHolograms(MRC plugin) {
		plugin.redBayHolo = HologramsAPI.createHologram(plugin, plugin.redBayLoc);
		plugin.blueBayHolo = HologramsAPI.createHologram(plugin, plugin.blueBayLoc);

		plugin.redBayLine = plugin.redBayHolo.appendTextLine(plugin.redBay + " Power Cells");
		plugin.blueBayLine = plugin.blueBayHolo.appendTextLine(plugin.blueBay + " Power Cells");

		plugin.redBayHolo.appendItemLine(new ItemStack(Material.ARROW));
		plugin.blueBayHolo.appendItemLine(new ItemStack(Material.ARROW));
	}

	private static void spawnFieldPowerCells(MRC plugin) {
		ItemStack arrowStack = new ItemStack(Material.ARROW);
		ItemMeta meta = arrowStack.getItemMeta();
		if (meta != null) {
			meta.setDisplayName("Power Cell");
		}
		arrowStack.setItemMeta(meta);
		for (Location loc : plugin.powerCellSpots) {
			plugin.world.dropItem(loc, arrowStack).setVelocity(new Vector(0, 0, 0));
		}
	}

	private static void startFinalCountdown(MRC plugin) {
		plugin.arena.clearEntities();

		// Prepare players for moving to the field
		preparePlayersForField(plugin);

		// Play sound
		plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BASS, 100, 1);

		// Announce teams
		StringBuilder redString = new StringBuilder();
		for (Player player : plugin.redPlayers) {
			redString.append(player.getName()).append(" ");
		}
		StringBuilder blueString = new StringBuilder();
		for (Player player : plugin.bluePlayers) {
			blueString.append(player.getName()).append(" ");
		}
		plugin.getServer().broadcastMessage(String.format("%s%s%s%sVS %s%s",
						MRC.PREFIX, ChatColor.RED, redString, ChatColor.WHITE, ChatColor.BLUE, blueString));

		plugin.joinable = false;
		plugin.countdown = 10;
	}

	private static void preparePlayersForField(MRC plugin) {
		for (Player player : plugin.playerPositions.keySet()) {
			Location position = plugin.playerPositions.get(player);

			// Clear inventories
			player.getInventory().remove(Material.IRON_DOOR);

			// Give players their power cell shooters
			givePlayerShooters(plugin, player);

			// Teleport players to their positions
			player.teleport(position);
			plugin.world.spawnEntity(position, EntityType.BOAT).addPassenger(player);

			if (plugin.redPlayers.contains(player)) {
				player.sendMessage(String.format("%sYou are competing on the %s%sRED ALLIANCE",
								MRC.PREFIX, ChatColor.RED, ChatColor.BOLD));
			} else {
				player.sendMessage(String.format("%sYou are competing on the %s%sBLUE ALLIANCE",
						MRC.PREFIX, ChatColor.BLUE, ChatColor.BOLD));
			}

		}
	}

	private static void givePlayerShooters(MRC plugin, Player player) {
		switch (plugin.playerClasses.get(player)) {
		case INSTACLIMB:
		case BOW:
			ItemStack bow = new ItemStack(Material.BOW, 1);
			ItemMeta bowMeta = bow.getItemMeta();
			if (bowMeta != null) {
				bowMeta.setUnbreakable(true);
				bowMeta.setDisplayName("Power Cell Shooter");
			}
			bow.setItemMeta(bowMeta);
			player.getInventory().addItem(bow);
			break;
		case CROSSBOW:
			ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
			ItemMeta crossbowMeta = crossbow.getItemMeta();
			if (crossbowMeta != null) {
				crossbowMeta.setUnbreakable(true);
				crossbowMeta.setDisplayName("Power Cell Shooter");
			}
			crossbow.setItemMeta(crossbowMeta);
			player.getInventory().addItem(crossbow);
			break;
		} // SNOWBALL and TRIDENT classes will be given their items when the game starts
	}

}

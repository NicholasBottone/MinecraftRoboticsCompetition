/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.gametick;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.managers.MRCPlayerData;
import io.bottone.mc.plugins.mrc.managers.MRCTitleManager;

public class IngameTick {

	public static void doTick(MRC plugin) {

		// The game is in progress.

		plugin.joinable = false;

		// Set power cell count holograms
		plugin.redBayLine.setText(plugin.redBay + " Power Cells");
		plugin.blueBayLine.setText(plugin.blueBay + " Power Cells");

		if (plugin.countdown == 135) {
			// Auto period is over
			endAutoPeriod(plugin);
		}

		if (plugin.countdown <= 0) {
			// Match is over
			finishMatch(plugin);
			return;
		}

		if (plugin.countdown == 30) {
			// Endgame period starts
			startEndgamePeriod(plugin);
		}

		plugin.countdown--;

	}

	private static void endAutoPeriod(MRC plugin) {
		// Play sound
		plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BELL, 100, 1);
	}

	private static void startEndgamePeriod(MRC plugin) {
		// Give acacia door for dismounting and climbing
		provideEndgameItems(plugin);

		// Paste vines for climbing
		plugin.arena.pasteVines();

		// Play sound
		plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BIT, 100, 1);

		// Send message
		plugin.getServer().broadcastMessage(MRC.PREFIX + "We're in the endgame now. 30 seconds left in the match!");
	}

	private static void provideEndgameItems(MRC plugin) {
		for (Player player : plugin.players) {
			ItemStack item = new ItemStack(Material.ACACIA_DOOR, 1);
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Begin Climbing");
			}
			item.setItemMeta(meta);
			player.getInventory().setItem(8, item);
		}
	}

	private static void finishMatch(MRC plugin) {
		// Calculate parks in the rendezvous zone.
		calculateParks(plugin);

		// Match is over.
		plugin.gameState = GameState.FINISHED;
		plugin.countdown = 10;

		// Remove all boats and arrows
		plugin.arena.clearEntities();

		// Clear all game items from inventory (other than armor)
		clearInventories(plugin);

		// Announce the winner and provide economy reward based on final score
		determineMatchWinner(plugin);

		// Publish final score to chat
		plugin.getServer().broadcastMessage(MRC.PREFIX + "Final Score: " + ChatColor.RED + ChatColor.BOLD
				+ plugin.redScore + ChatColor.AQUA + " to " + ChatColor.BLUE + ChatColor.BOLD + plugin.blueScore);

		// Play sound
		plugin.world.playSound(plugin.redRight, Sound.ENTITY_PLAYER_LEVELUP, 100, 1);

		// Announce player contributions in 3 seconds
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> announcePlayerContributions(plugin), 60);
	}

	private static void clearInventories(MRC plugin) {
		for (Player player : plugin.players) {
			player.getInventory().remove(Material.BOW);
			player.getInventory().remove(Material.CROSSBOW);
			player.getInventory().remove(Material.SNOWBALL);
			player.getInventory().remove(Material.ARROW);
			player.getInventory().remove(Material.ACACIA_DOOR);
		}
	}

	private static void announcePlayerContributions(MRC plugin) {
		plugin.getServer().broadcastMessage(MRC.PREFIX + ChatColor.BOLD + "Player Contributions");
		for (Player p : plugin.playerData.keySet()) {
			MRCPlayerData pd = plugin.playerData.get(p);
			plugin.getServer().broadcastMessage(MRC.PREFIX + pd);
			plugin.records.submitScore(p, pd.getPointsContributed());
		}
	}

	private static void determineMatchWinner(MRC plugin) {
		if (plugin.redScore > plugin.blueScore) {
			plugin.getServer().broadcastMessage(
					MRC.PREFIX + ChatColor.RED.toString() + ChatColor.BOLD + "RED ALLIANCE WINS!");
			MRCTitleManager.showTitle(ChatColor.RED.toString() + ChatColor.BOLD + "RED ALLIANCE WINS!", " ");

			if (plugin.econ != null && plugin.bluePlayers.size() >= plugin.redPlayers.size()
					&& (plugin.redScore + plugin.blueScore) >= 100) {
				// Give economy reward
				for (Player player : plugin.redPlayers) {
					plugin.econ.depositPlayer(player, MRC.WIN_REWARD);
				}
			}

		} else if (plugin.blueScore > plugin.redScore) {
			plugin.getServer().broadcastMessage(
					MRC.PREFIX + ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE ALLIANCE WINS!");
			MRCTitleManager.showTitle(ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE ALLIANCE WINS!", " ");

			if (plugin.econ != null && plugin.bluePlayers.size() <= plugin.redPlayers.size()
					&& (plugin.redScore + plugin.blueScore) >= 100) {
				// Give economy reward
				for (Player player : plugin.bluePlayers) {
					plugin.econ.depositPlayer(player, MRC.WIN_REWARD);
				}
			}
		} else {
			plugin.getServer()
					.broadcastMessage(MRC.PREFIX + ChatColor.WHITE.toString() + ChatColor.BOLD + "IT'S A TIE!");
			MRCTitleManager.showTitle(ChatColor.WHITE.toString() + ChatColor.BOLD + "IT'S A TIE!", " ");

			if (plugin.econ != null && plugin.players.size() >= 2 && (plugin.redScore + plugin.blueScore) >= 100) {
				// Give economy reward
				for (Player player : plugin.players) {
					plugin.econ.depositPlayer(player, MRC.TIE_REWARD);
				}
			}
		}
	}

	private static void calculateParks(MRC plugin) {
		for (Player player : plugin.redPlayers) {
			Location loc = player.getLocation();
			loc.setY(71.5);
			if (loc.getBlock().getType() == Material.SMOOTH_RED_SANDSTONE
					|| loc.getBlock().getType() == Material.OAK_WOOD) {
				// Parked!
				plugin.redScore += 5;
				plugin.redEndgame += 5;
				plugin.playerData.get(player).addPoints(5);
			}
		}
		for (Player player : plugin.bluePlayers) {
			Location loc = player.getLocation();
			loc.setY(71.5);
			if (loc.getBlock().getType() == Material.SMOOTH_SANDSTONE
					|| loc.getBlock().getType() == Material.OAK_WOOD) {
				// Parked!
				plugin.blueScore += 5;
				plugin.blueEndgame += 5;
				plugin.playerData.get(player).addPoints(5);
			}
		}
	}

}

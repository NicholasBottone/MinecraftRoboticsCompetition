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

		plugin.redBayLine.setText(plugin.redBay + " Power Cells");
		plugin.blueBayLine.setText(plugin.blueBay + " Power Cells");

		if (plugin.countdown == 135) {
			// Auto period is over
			plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BELL, 100, 1);
		}

		if (plugin.countdown <= 0) {

			// Calculate parks in the rendezvous zone.
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

			// Match is over.
			plugin.gameState = GameState.FINISHED;
			plugin.countdown = 10;

			// Remove all boats and arrows
			plugin.arena.clearEntities();

			// Clear all game items from inventory (other than armor)
			for (Player player : plugin.players) {
				player.getInventory().remove(Material.BOW);
				player.getInventory().remove(Material.CROSSBOW);
				player.getInventory().remove(Material.SNOWBALL);
				player.getInventory().remove(Material.ARROW);
				player.getInventory().remove(Material.ACACIA_DOOR);
			}

			// Calculate ranking points per alliance
			int redRP = 0;
			redRP += (plugin.redPC >= MRC.PC_RANKING_POINT) ? 1 : 0;
			redRP += (plugin.redEndgame >= 65) ? 1 : 0;
			int blueRP = 0;
			blueRP += (plugin.bluePC >= MRC.PC_RANKING_POINT) ? 1 : 0;
			blueRP += (plugin.blueEndgame >= 65) ? 1 : 0;
			
			// Announce the winner based on final score
			if (plugin.redScore > plugin.blueScore) {
				redRP += 2;
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
				blueRP += 2;
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
				redRP += 1;
				blueRP += 1;
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

			// Publish final score to chat
			plugin.getServer().broadcastMessage(MRC.PREFIX + "Final Score: " + ChatColor.RED + ChatColor.BOLD
					+ plugin.redScore + ChatColor.AQUA + " to " + ChatColor.BLUE + ChatColor.BOLD + plugin.blueScore);
			
			// Publish ranking points to chat
			plugin.getServer().broadcastMessage(MRC.PREFIX + ChatColor.RED + ChatColor.BOLD + "RED: + " + redRP + " RPs"
					+ ChatColor.AQUA + " -- " + ChatColor.BLUE + ChatColor.BOLD + "BLUE: + " + blueRP + " RPs");
			
			// Play sound
			plugin.world.playSound(plugin.redRight, Sound.ENTITY_PLAYER_LEVELUP, 100, 1);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					plugin.getServer().broadcastMessage(MRC.PREFIX + ChatColor.BOLD + "Player Contributions");
					for (Player p : plugin.playerData.keySet()) {
						MRCPlayerData pd = plugin.playerData.get(p);
						plugin.getServer().broadcastMessage(MRC.PREFIX + pd);
					}
				}
			}, 60);

			return;
		}

		if (plugin.countdown == 30) { // Endgame period starts
			// Give acacia door for dismounting and climbing
			for (Player player : plugin.players) {
				ItemStack item = new ItemStack(Material.ACACIA_DOOR, 1);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Begin Climbing");
				item.setItemMeta(meta);
				player.getInventory().setItem(8, item);
			}

			// Paste vines for climbing
			plugin.arena.pasteVines();

			// Play sound
			plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BIT, 100, 1);

			// Send message
			plugin.getServer().broadcastMessage(MRC.PREFIX + "We're in the endgame now. 30 seconds left in the match!");
		}

		plugin.countdown--;

	}

}

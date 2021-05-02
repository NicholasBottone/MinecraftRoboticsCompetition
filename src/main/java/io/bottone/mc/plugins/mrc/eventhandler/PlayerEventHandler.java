/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.eventhandler;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;

public class PlayerEventHandler implements Listener {

	private final MRC plugin;

	public PlayerEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerJoinEvent event) {

		onPlayerLogin(plugin, event.getPlayer());

	}

	public static void onPlayerLogin(MRC plugin, Player player) {

		if (!plugin.playerClasses.containsKey(player))
			plugin.playerClasses.put(player, PlayerClass.BOW);

		player.setGameMode(GameMode.ADVENTURE);
		player.getInventory().clear();

		plugin.spectators.add(player);
		player.setAllowFlight(false);

		if (plugin.joinable) {
			if (plugin.matches.containsKey(plugin.matchNumber)) {
				if (plugin.matches.get(plugin.matchNumber).teleportPlayer(player, plugin.redPositionSelect,
						plugin.bluePositionSelect, plugin.stadiumStands)) {
					player.sendMessage(MRC.PREFIX + "Welcome to MRC Event match #" + plugin.matchNumber);
					player.sendMessage(MRC.PREFIX + "Choose a position and class to ready-up.");
				} else {
					if (!player.hasPermission("mrc.fta")) {
						plugin.sendToBungeeServer(player, "MRC");
						return;
					} else {
						player.teleport(plugin.stadiumStands);
						return;
					}
				}
			}

		} else {
			if (plugin.matches.containsKey(plugin.matchNumber + 1)) {
				if (!plugin.matches.get(plugin.matchNumber + 1).hasPlayer(player)) {
					if (!player.hasPermission("mrc.fta")) {
						plugin.sendToBungeeServer(player, "MRC");
						return;
					}
				}
			}
			player.teleport(plugin.stadiumStands);
			player.sendMessage(MRC.PREFIX + "You are queued and will be up soon.");
		}

	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {

		Entity vehicle = event.getPlayer().getVehicle();
		if (vehicle != null) {
			vehicle.remove();
		}

		if (plugin.players.contains(event.getPlayer())) {
			// Player was in game
			plugin.players.remove(event.getPlayer());
			plugin.redPlayers.remove(event.getPlayer());
			plugin.bluePlayers.remove(event.getPlayer());

			Location l = plugin.playerPositions.remove(event.getPlayer());
			if (l != null) {
				if (l.equals(plugin.redLeft)) {
					plugin.redLeftSign.setLine(3, "Click to claim");
					plugin.redLeftSign.update();
				}
				if (l.equals(plugin.redCenter)) {
					plugin.redCenterSign.setLine(3, "Click to claim");
					plugin.redCenterSign.update();
				}
				if (l.equals(plugin.redRight)) {
					plugin.redRightSign.setLine(3, "Click to claim");
					plugin.redRightSign.update();
				}
				if (l.equals(plugin.blueLeft)) {
					plugin.blueLeftSign.setLine(3, "Click to claim");
					plugin.blueLeftSign.update();
				}
				if (l.equals(plugin.blueCenter)) {
					plugin.blueCenterSign.setLine(3, "Click to claim");
					plugin.blueCenterSign.update();
				}
				if (l.equals(plugin.blueRight)) {
					plugin.blueRightSign.setLine(3, "Click to claim");
					plugin.blueCenterSign.update();
				}
			}

			if (plugin.gameState == GameState.INGAME || (plugin.gameState == GameState.COUNTDOWN && !plugin.joinable))
				plugin.getServer().broadcastMessage(MRC.PREFIX + event.getPlayer().getName() + " has left the game.");

			return;
		}

		plugin.spectators.remove(event.getPlayer());

	}

}

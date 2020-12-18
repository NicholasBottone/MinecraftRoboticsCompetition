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

	private MRC plugin;

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
		plugin.tempSpectators.add(player);
		player.setAllowFlight(true);

		player.teleport(plugin.stadiumStands);

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

			if (plugin.players.size() < 1) {
				plugin.getServer().broadcastMessage(MRC.PREFIX + "Match aborted due to lack of players.");
				plugin.gameState = GameState.LOBBY;
				plugin.countdown = 20;
				plugin.arena.resetArena();
			}

			return;
		}

		plugin.spectators.remove(event.getPlayer());
		plugin.tempSpectators.remove(event.getPlayer());

	}

}

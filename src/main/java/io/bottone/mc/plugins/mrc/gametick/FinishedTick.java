package io.bottone.mc.plugins.mrc.gametick;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.eventhandler.PlayerEventHandler;

public class FinishedTick {

	public static void doTick(MRC plugin) {

		// The game is over, the arena will be reset soon.

		plugin.joinable = false;

		if (plugin.countdown == 0) {

			List<Player> playersToProcess = new ArrayList<>();
			List<Player> spectatorsToProcess = new ArrayList<>();

			for (Player player : plugin.players) {
				player.getInventory().clear();
				player.teleport(plugin.stadiumStands);
				playersToProcess.add(player);
			}
			for (Player player : plugin.tempSpectators) {
				player.getInventory().clear();
				spectatorsToProcess.add(player);
			}

			plugin.arena.resetArena();
			plugin.gameState = GameState.LOBBY;
			plugin.joinable = true;

			for (Player player : playersToProcess) {
				PlayerEventHandler.onPlayerLogin(plugin, player);
			}
			
			for (Player player : spectatorsToProcess) {
				plugin.spectators.add(player);
				plugin.tempSpectators.add(player);
				player.setAllowFlight(true);
			}

			return;

		}

		plugin.countdown--;

	}

}

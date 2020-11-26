package io.bottone.mc.plugins.mrc.gametick;

import org.bukkit.entity.Player;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;

public class FinishedTick {

	public static void doTick(MRC plugin) {

		// The game is over, the arena will be reset soon.

		plugin.joinable = false;

		if (plugin.countdown == 0) {

			for (Player player : plugin.players) {
				player.getInventory().clear();
				player.teleport(plugin.stadiumStands);
				plugin.sendToBungeeServer(player, "MRC");
			}

			plugin.arena.resetArena();
			plugin.gameState = GameState.LOBBY;
			plugin.joinable = true;

			plugin.matchNumber++;
			plugin.getServer().broadcastMessage(MRC.PREFIX + "Welcome to MRC Event match #" + plugin.matchNumber);
			if (plugin.matches.containsKey(plugin.matchNumber))
				plugin.matches.get(plugin.matchNumber).teleportPlayers(plugin.redPositionSelect,
						plugin.bluePositionSelect, MRC.PREFIX);

			return;

		}

		plugin.countdown--;

	}

}

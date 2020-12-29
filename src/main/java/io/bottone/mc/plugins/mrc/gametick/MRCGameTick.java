/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.gametick;

import org.bukkit.Bukkit;

import io.bottone.mc.plugins.mrc.MRC;

public class MRCGameTick {

	public MRCGameTick(MRC plugin) {

		// GAME TICK (every second)
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {

				switch (plugin.gameState) {

				case LOBBY:
					// Lobby normally means no players.
					LobbyTick.doTick(plugin);
					break;

				case COUNTDOWN:
					// Pre-game state, starting soon.
					CountdownTick.doTick(plugin);
					break;

				case INGAME:
					// The game is in progress.
					IngameTick.doTick(plugin);
					break;

				case FINISHED:
					// The game is over, the arena will be reset soon.
					FinishedTick.doTick(plugin);
					break;

				}

				// Update scoreboards for all players and spectators
				plugin.scoreboard.updateScoreboards();

			}
		}, 20, 20);

	}

}

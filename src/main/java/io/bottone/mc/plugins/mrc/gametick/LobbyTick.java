/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.gametick;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;

public class LobbyTick {

	public static void doTick(MRC plugin) {
		// Lobby normally means no players.

		plugin.joinable = true;
		plugin.countdown = 0;

		if (plugin.players.size() >= 1) {
			// We have player(s) ... start the countdown!
			plugin.gameState = GameState.COUNTDOWN;
			plugin.countdown = 20;

			plugin.redScore = 0;
			plugin.redPC = 0;
			plugin.redEndgame = 0;
			plugin.redBay = 5;
			plugin.blueScore = 0;
			plugin.bluePC = 0;
			plugin.blueEndgame = 0;
			plugin.blueBay = 5;

			plugin.getServer().broadcastMessage(MRC.PREFIX + "Match initiating in 20 seconds!");
		}

	}

}

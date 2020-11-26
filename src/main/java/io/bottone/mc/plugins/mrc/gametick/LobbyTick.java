package io.bottone.mc.plugins.mrc.gametick;

import io.bottone.mc.plugins.mrc.MRC;

public class LobbyTick {

	public static void doTick(MRC plugin) {
		// Lobby means no active match.

		plugin.joinable = true;
		plugin.countdown = 0;

		plugin.redScore = 0;
		plugin.redPC = 0;
		plugin.redEndgame = 0;
		plugin.redBay = 5;
		plugin.blueScore = 0;
		plugin.bluePC = 0;
		plugin.blueEndgame = 0;
		plugin.blueBay = 5;

	}

}

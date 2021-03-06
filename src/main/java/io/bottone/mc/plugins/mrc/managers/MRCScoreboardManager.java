/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;

public class MRCScoreboardManager {

	private final MRC plugin;

	public MRCScoreboardManager(MRC plugin) {
		this.plugin = plugin;
	}

	public void updateScoreboards() {

		MRCScoreboard sb = new MRCScoreboard(MRC.PREFIX + plugin.gameState.toString());

		switch (plugin.gameState) {

		case LOBBY:
			sb.put(3, "Waiting to start the game!");
			sb.put(2, plugin.players.size() + " players");
			sb.put(1, " ");
			sb.put(0, ChatColor.GREEN + "mc.bottone.io");
			break;

		case COUNTDOWN:
			if (plugin.joinable) {
				sb.put(3, "Match initiating in " + plugin.countdown);
			} else {
				sb.put(3, ChatColor.BOLD + "Here we go in " + (plugin.countdown + 1));
			}
			sb.put(2, plugin.players.size() + " players");
			sb.put(1, " ");
			sb.put(0, ChatColor.GREEN + "mc.bottone.io");
			break;

		case INGAME:
			sb.put(9, ChatColor.BOLD + "Timer: " + plugin.countdown);
			sb.put(8, " ");
			sb.put(7, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
			sb.put(6, ChatColor.RED + "Score: " + ChatColor.BOLD + plugin.redScore);
			sb.put(5, ChatColor.RED + "Power Cells: " + plugin.redPC);
			sb.put(4, "  ");
			sb.put(3, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
			sb.put(2, ChatColor.BLUE + "Score: " + ChatColor.BOLD + plugin.blueScore);
			sb.put(1, ChatColor.BLUE + "Power Cells: " + plugin.bluePC);
			break;

		case FINISHED:
			if (plugin.redScore > plugin.blueScore) {
				sb.put(12, ChatColor.RED.toString() + ChatColor.BOLD + "RED WINS!");
			} else if (plugin.blueScore > plugin.redScore) {
				sb.put(12, ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE WINS!");
			} else {
				sb.put(12, ChatColor.BOLD + "TIE!");
			}
			sb.put(11, " ");
			sb.put(10, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
			sb.put(9, ChatColor.RED + "Score: " + ChatColor.BOLD + plugin.redScore);
			sb.put(8, ChatColor.RED + "Power Cells: " + plugin.redPC);
			sb.put(7, ChatColor.RED + "Endgame: " + plugin.redEndgame);
			sb.put(6, "  ");
			sb.put(5, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
			sb.put(4, ChatColor.BLUE + "Score: " + ChatColor.BOLD + plugin.blueScore);
			sb.put(3, ChatColor.BLUE + "Power Cells: " + plugin.bluePC);
			sb.put(2, ChatColor.BLUE + "Endgame: " + plugin.blueEndgame);
			sb.put(1, "   ");
			sb.put(0, ChatColor.GREEN + "mc.bottone.io");
			break;

		}

		for (Player player : plugin.spectators) {
			sb.setScoreboard(player);

			player.setExp(0);
			player.setLevel(plugin.countdown);
			if (plugin.gameState == GameState.COUNTDOWN && !plugin.joinable)
				player.setLevel(plugin.countdown + 1);
		}

		for (Player player : plugin.players) {
			sb = new MRCScoreboard(MRC.PREFIX + plugin.gameState.toString());

			switch (plugin.gameState) {

			case LOBBY:
				sb.put(3, "Waiting to start the game!");
				sb.put(2, plugin.players.size() + " players");
				sb.put(1, " ");
				sb.put(0, ChatColor.GREEN + "mc.bottone.io");
				break;

			case COUNTDOWN:
				if (plugin.joinable) {
					sb.put(3, "Match initiating in " + plugin.countdown);
				} else {
					sb.put(3, ChatColor.BOLD + "Here we go in " + (plugin.countdown + 1));
				}
				sb.put(2, plugin.players.size() + " players");
				sb.put(1, " ");
				sb.put(0, ChatColor.GREEN + "mc.bottone.io");
				break;

			case INGAME:
				MRCPlayerData pd = plugin.playerData.get(player);
				sb.put(12, ChatColor.BOLD + "Timer: " + plugin.countdown);
				sb.put(11, " ");
				sb.put(10, ChatColor.AQUA.toString() + pd.getPointsContributed() + " pts contributed");
				sb.put(9, ChatColor.AQUA.toString() + pd.getAccuracyPercent() + "% acc, " + pd.getInnersPercent()
						+ "% inners");
				sb.put(8, " ");
				sb.put(7, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
				sb.put(6, ChatColor.RED + "Score: " + ChatColor.BOLD + plugin.redScore);
				sb.put(5, ChatColor.RED + "Power Cells: " + plugin.redPC);
				sb.put(4, "  ");
				sb.put(3, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
				sb.put(2, ChatColor.BLUE + "Score: " + ChatColor.BOLD + plugin.blueScore);
				sb.put(1, ChatColor.BLUE + "Power Cells: " + plugin.bluePC);
				break;

			case FINISHED:
				MRCPlayerData pdata = plugin.playerData.get(player);
				if (plugin.redScore > plugin.blueScore) {
					sb.put(15, ChatColor.RED.toString() + ChatColor.BOLD + "RED WINS!");
				} else if (plugin.blueScore > plugin.redScore) {
					sb.put(15, ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE WINS!");
				} else {
					sb.put(15, ChatColor.BOLD + "TIE!");
				}
				sb.put(14, " ");
				sb.put(13, ChatColor.AQUA.toString() + pdata.getPointsContributed() + " pts contributed");
				sb.put(12, ChatColor.AQUA.toString() + pdata.getAccuracyPercent() + "% acc, " + pdata.getInnersPercent()
						+ "% inners");
				sb.put(11, " ");
				sb.put(10, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
				sb.put(9, ChatColor.RED + "Score: " + ChatColor.BOLD + plugin.redScore);
				sb.put(8, ChatColor.RED + "Power Cells: " + plugin.redPC);
				sb.put(7, ChatColor.RED + "Endgame: " + plugin.redEndgame);
				sb.put(6, "  ");
				sb.put(5, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
				sb.put(4, ChatColor.BLUE + "Score: " + ChatColor.BOLD + plugin.blueScore);
				sb.put(3, ChatColor.BLUE + "Power Cells: " + plugin.bluePC);
				sb.put(2, ChatColor.BLUE + "Endgame: " + plugin.blueEndgame);
				sb.put(1, "   ");
				sb.put(0, ChatColor.GREEN + "mc.bottone.io");
				break;

			}

			sb.setScoreboard(player);

			player.setExp(0);
			player.setLevel(plugin.countdown);
			if (plugin.gameState == GameState.COUNTDOWN && !plugin.joinable)
				player.setLevel(plugin.countdown + 1);
		}

	}

}

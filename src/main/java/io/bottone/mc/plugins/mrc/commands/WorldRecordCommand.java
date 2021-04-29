/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.bottone.mc.plugins.mrc.MRC;
import org.jetbrains.annotations.NotNull;

public class WorldRecordCommand implements CommandExecutor {

	private final MRC plugin;

	public WorldRecordCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		for (int i = 1; i <= 4; i++) {
			sender.sendMessage(
					MRC.PREFIX + i + ") " + plugin.worldRecordHolders[i - 1] + ": " + plugin.worldRecordScores[i - 1]);
		}
		return true;
	}

}

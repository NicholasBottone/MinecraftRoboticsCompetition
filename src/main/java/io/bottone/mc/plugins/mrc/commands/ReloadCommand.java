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

public class ReloadCommand implements CommandExecutor {

	private final MRC plugin;

	public ReloadCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!sender.hasPermission("mrc.fta")) {
			sender.sendMessage(MRC.PREFIX + "No permission!");
			return true;
		}

		if (plugin.schedule.importMatchSchedule()) {
			sender.sendMessage(MRC.PREFIX + "Reloaded successfully.");
			plugin.l.info("Successfully imported match schedule");
		} else {
			sender.sendMessage(MRC.PREFIX + "Failed to reload.");
			plugin.l.info("Failed to import match schedule");
		}
		return true;
	}

}

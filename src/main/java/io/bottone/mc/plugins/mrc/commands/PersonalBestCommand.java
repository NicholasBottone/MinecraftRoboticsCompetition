/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.bottone.mc.plugins.mrc.MRC;
import org.jetbrains.annotations.NotNull;

public class PersonalBestCommand implements CommandExecutor {

	private final MRC plugin;

	public PersonalBestCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

		if (!(sender instanceof Player)) {

			sender.sendMessage(MRC.PREFIX + "Must be a player to do that!");
			return true;

		}

		Player player = (Player) sender;

		if (plugin.personalBests.containsKey(player.getName())) {
			player.sendMessage(MRC.PREFIX + "Your personal best is " + plugin.personalBests.get(player.getName()));
		} else {
			player.sendMessage(MRC.PREFIX + "You do not have a personal best saved!");
		}
		return true;
	}

}

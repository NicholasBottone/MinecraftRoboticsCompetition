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

public class SpectateCommand implements CommandExecutor {

	private final MRC plugin;

	public SpectateCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {

			sender.sendMessage(MRC.PREFIX + "Must be a player to do that!");
			return true;

		}

		Player player = (Player) sender;

		if (!plugin.players.contains(player)) {

			if (!plugin.spectators.contains(player))
				plugin.spectators.add(player);
			plugin.tempSpectators.remove(player);

			player.setAllowFlight(true);

			player.sendMessage(MRC.PREFIX + "You are now in spectator mode!");

		} else {

			player.sendMessage(MRC.PREFIX + "You can't do that right now!");

		}
		return true;

	}

}

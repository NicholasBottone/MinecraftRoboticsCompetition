/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.commands;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class ClassCommand implements CommandExecutor {

	private final MRC plugin;

	public ClassCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {

			sender.sendMessage(MRC.PREFIX + "Must be a player to do that!");
			return true;

		}

		Player player = (Player) sender;

		if (plugin.joinable) {

			return grantPlayerClass(args, player);

		} else {

			player.sendMessage(MRC.PREFIX + "You can't do that right now!");

		}

		return true;

	}

	private boolean grantPlayerClass(String[] args, Player player) {
		if (args.length == 0) {
			return false;
		}

		switch (args[0].toLowerCase()) {
		case "instaclimb":
			plugin.playerClasses.put(player, PlayerClass.INSTACLIMB);
			break;
		case "bow":
			plugin.playerClasses.put(player, PlayerClass.BOW);
			break;
		case "crossbow":
			plugin.playerClasses.put(player, PlayerClass.CROSSBOW);
			break;
		case "snowball":
			plugin.playerClasses.put(player, PlayerClass.SNOWBALL);
			break;
		case "trident":
			plugin.playerClasses.put(player, PlayerClass.TRIDENT);
				break;
		default:
			return false;
		}

		player.sendMessage(MRC.PREFIX + "You have been given the " + args[0] + " class.");
		return true;
	}

}

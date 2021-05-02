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
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class TeamChatCommand implements CommandExecutor {
	
	private final MRC plugin;

	public TeamChatCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {

			sender.sendMessage(MRC.PREFIX + "Must be a player to do that!");
			return true;

		}

		if (args.length < 1)
			return false;
		
		Player player = (Player) sender;
		
		String message = ChatColor.GREEN + "[Team Chat] " + player.getDisplayName() + " " + ChatColor.RESET + String.join(" ", args);
		
		if (plugin.redPlayers.contains(player)) {
			for (Player p : plugin.redPlayers) {
				p.sendMessage(message);
			}
		} else if (plugin.bluePlayers.contains(player)) {
			for (Player p : plugin.bluePlayers) {
				p.sendMessage(message);
			}
		} else {
			player.sendMessage(ChatColor.RED + "Could not send your message as you have not yet joined a team.");
		}
		
		return true;
	}
	
}

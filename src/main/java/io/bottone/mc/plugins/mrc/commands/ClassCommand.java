package io.bottone.mc.plugins.mrc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;

public class ClassCommand implements CommandExecutor {

	private MRC plugin;

	public ClassCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {

			sender.sendMessage(MRC.PREFIX + "Must be a player to do that!");
			return true;

		}

		Player player = (Player) sender;

		if (plugin.joinable) {

			if (!plugin.players.contains(player)) {
				player.sendMessage(MRC.PREFIX + "You must claim a position before selecting a class.");
				return true;
			}

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
			default:
				return false;
			}

			player.sendMessage(MRC.PREFIX + "You have been given the " + args[0] + " class.");
			return true;

		} else {

			player.sendMessage(MRC.PREFIX + "You can't do that right now!");
			return true;

		}

	}

}

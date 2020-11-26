package io.bottone.mc.plugins.mrc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.bottone.mc.plugins.mrc.MRC;

public class SetMatchCommand implements CommandExecutor {

	private MRC plugin;

	public SetMatchCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("mrc.fta")) {
			sender.sendMessage(MRC.PREFIX + "No permission!");
			return true;
		}

		try {
			plugin.matchNumber = Integer.parseInt(args[0]);
			sender.sendMessage(MRC.PREFIX + "Set match to " + plugin.matchNumber);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}

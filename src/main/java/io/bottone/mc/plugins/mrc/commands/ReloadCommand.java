package io.bottone.mc.plugins.mrc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.bottone.mc.plugins.mrc.MRC;

public class ReloadCommand implements CommandExecutor {

	private MRC plugin;

	public ReloadCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

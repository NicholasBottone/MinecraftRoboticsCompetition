package io.bottone.mc.plugins.mrc.managers;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.commands.ClassCommand;
import io.bottone.mc.plugins.mrc.commands.PersonalBestCommand;
import io.bottone.mc.plugins.mrc.commands.PositionCommand;
import io.bottone.mc.plugins.mrc.commands.ReloadCommand;
import io.bottone.mc.plugins.mrc.commands.SpectateCommand;
import io.bottone.mc.plugins.mrc.commands.WorldRecordCommand;
import io.bottone.mc.plugins.mrc.commands.TeamChatCommand;

public class MRCCommands {

	public static void registerCommands(MRC plugin) {

		plugin.getCommand("class").setExecutor(new ClassCommand(plugin));
		plugin.getCommand("pb").setExecutor(new PersonalBestCommand(plugin));
		plugin.getCommand("pos").setExecutor(new PositionCommand(plugin));
		plugin.getCommand("reloadrecords").setExecutor(new ReloadCommand(plugin));
		plugin.getCommand("spectate").setExecutor(new SpectateCommand(plugin));
		plugin.getCommand("wr").setExecutor(new WorldRecordCommand(plugin));
		plugin.getCommand("teamchat").setExecutor(new TeamChatCommand(plugin));

	}

}

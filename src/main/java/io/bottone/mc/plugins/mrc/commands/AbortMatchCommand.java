package io.bottone.mc.plugins.mrc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;

public class AbortMatchCommand implements CommandExecutor {

	private MRC plugin;

	public AbortMatchCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("mrc.fta")) {
			sender.sendMessage(MRC.PREFIX + "No permission!");
			return true;
		}

		if (plugin.gameState == GameState.LOBBY) {
			sender.sendMessage(MRC.PREFIX + "Already in the lobby, no match to abort.");
			return true;
		}

		plugin.arena.resetArena();
		plugin.gameState = GameState.LOBBY;
		plugin.joinable = true;

		plugin.getServer().broadcastMessage(MRC.PREFIX + "Match aborted. Let's try that again.");
		plugin.getServer().broadcastMessage(MRC.PREFIX + "Welcome to MRC Event match #" + plugin.matchNumber);
		if (plugin.matches.containsKey(plugin.matchNumber))
			plugin.matches.get(plugin.matchNumber).teleportPlayers(plugin.redPositionSelect, plugin.bluePositionSelect,
					MRC.PREFIX);

		return true;
	}

}

package io.bottone.mc.plugins.mrc.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;

public class StartMatchCommand implements CommandExecutor {

	private MRC plugin;

	public StartMatchCommand(MRC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("mrc.fta")) {
			sender.sendMessage(MRC.PREFIX + "No permission!");
			return true;
		}

		// Moving to the arena.
		plugin.getServer()
				.broadcastMessage(MRC.PREFIX + "Official event match #" + plugin.matchNumber + " is starting!");

		for (Player player : plugin.playerPositions.keySet()) {
			Location position = plugin.playerPositions.get(player);

			// Clear inventories
			player.getInventory().remove(Material.IRON_DOOR);

			// Give players their power cell shooters
			switch (plugin.playerClasses.get(player)) {
			case INSTACLIMB:
			case BOW:
				ItemStack bow = new ItemStack(Material.BOW, 1);
				ItemMeta bowMeta = bow.getItemMeta();
				bowMeta.setUnbreakable(true);
				bowMeta.setDisplayName("Power Cell Shooter");
				bow.setItemMeta(bowMeta);
				player.getInventory().addItem(bow);
				break;
			case CROSSBOW:
				ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
				ItemMeta crossbowMeta = crossbow.getItemMeta();
				crossbowMeta.setUnbreakable(true);
				crossbowMeta.setDisplayName("Power Cell Shooter");
				crossbow.setItemMeta(crossbowMeta);
				player.getInventory().addItem(crossbow);
				break;
			case SNOWBALL: // intentionally blank
				break;
			}

			// Teleport players to their positions
			player.teleport(position);
			plugin.world.spawnEntity(position, EntityType.BOAT).addPassenger(player);

			if (plugin.redPlayers.contains(player)) {
				player.sendMessage(
						MRC.PREFIX + "You are competing on the " + ChatColor.RED + ChatColor.BOLD + "RED ALLIANCE");
			} else {
				player.sendMessage(
						MRC.PREFIX + "You are competing on the " + ChatColor.BLUE + ChatColor.BOLD + "BLUE ALLIANCE");
			}

		}

		// Play sound
		plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BASS, 100, 1);

		// Announce teams
		String redString = "";
		for (Player player : plugin.redPlayers) {
			redString += player.getName() + " ";
		}
		String blueString = "";
		for (Player player : plugin.bluePlayers) {
			blueString += player.getName() + " ";
		}
		plugin.getServer().broadcastMessage(
				MRC.PREFIX + ChatColor.RED + redString + ChatColor.WHITE + "VS " + ChatColor.BLUE + blueString);

		plugin.joinable = false;
		plugin.countdown = 10;
		plugin.gameState = GameState.COUNTDOWN;

		return true;
	}

}

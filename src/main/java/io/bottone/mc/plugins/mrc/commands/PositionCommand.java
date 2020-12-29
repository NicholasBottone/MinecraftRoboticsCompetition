/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.commands;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import io.bottone.mc.plugins.mrc.MRC;

public class PositionCommand implements CommandExecutor {

	private MRC plugin;

	public PositionCommand(MRC plugin) {
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

			if (args.length == 0)
				return false;

			Location pos;
			Sign sign;

			switch (args[0].toLowerCase()) {
			case "redleft":
				pos = plugin.redLeft;
				sign = plugin.redLeftSign;
				break;
			case "redcenter":
				pos = plugin.redCenter;
				sign = plugin.redCenterSign;
				break;
			case "redright":
				pos = plugin.redRight;
				sign = plugin.redRightSign;
				break;
			case "blueleft":
				pos = plugin.blueLeft;
				sign = plugin.blueLeftSign;
				break;
			case "bluecenter":
				pos = plugin.blueCenter;
				sign = plugin.blueCenterSign;
				break;
			case "blueright":
				pos = plugin.blueRight;
				sign = plugin.blueRightSign;
				break;
			default:
				return false;
			}

			if (plugin.playerPositions.containsValue(pos)) {
				if (plugin.playerPositions.get(player).equals(pos)) {
					player.sendMessage(MRC.PREFIX + "Unclaimed your position.");
					removeOldPosSel(player);
					plugin.spectators.add(player);
					plugin.tempSpectators.add(player);
					player.setAllowFlight(true);
					player.getInventory().setArmorContents(null);
					return true;
				}

				player.sendMessage(MRC.PREFIX + "That spot is already taken!");
				return true;
			}
			removeOldPosSel(player);
			plugin.playerPositions.put(player, pos);
			Color team;
			if (args[0].toLowerCase().startsWith("red")) {
				plugin.redPlayers.add(player);
				team = Color.RED;
			} else {
				plugin.bluePlayers.add(player);
				team = Color.BLUE;
			}
			sign.setLine(3, player.getName());
			sign.update();

			plugin.players.add(player);
			plugin.spectators.remove(player);
			plugin.tempSpectators.remove(player);

			player.setAllowFlight(false);

			// Give players their colored armor
			ItemStack armor = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta lameta = (LeatherArmorMeta) armor.getItemMeta();
			lameta.setColor(team);
			armor.setItemMeta(lameta);
			player.getInventory().setBoots(armor);

			armor = new ItemStack(Material.LEATHER_LEGGINGS);
			lameta = (LeatherArmorMeta) armor.getItemMeta();
			lameta.setColor(team);
			armor.setItemMeta(lameta);
			player.getInventory().setLeggings(armor);

			armor = new ItemStack(Material.LEATHER_HELMET);
			lameta = (LeatherArmorMeta) armor.getItemMeta();
			lameta.setColor(team);
			armor.setItemMeta(lameta);
			player.getInventory().setHelmet(armor);

			armor = new ItemStack(Material.LEATHER_CHESTPLATE);
			lameta = (LeatherArmorMeta) armor.getItemMeta();
			lameta.setColor(team);
			armor.setItemMeta(lameta);
			player.getInventory().setChestplate(armor);

			player.sendMessage(MRC.PREFIX + "Claimed a position in the upcoming match.");
			return true;

		} else {

			player.sendMessage(MRC.PREFIX + "You can't do that right now!");
			return true;

		}

	}

	private void removeOldPosSel(Player player) {
		plugin.redPlayers.remove(player);
		plugin.bluePlayers.remove(player);
		plugin.players.remove(player);
		if (plugin.playerPositions.containsKey(player)) {
			Location loc = plugin.playerPositions.get(player);
			if (loc.equals(plugin.redLeft)) {
				plugin.playerPositions.remove(player);
				plugin.redLeftSign.setLine(3, "Click to claim");
				plugin.redLeftSign.update();
			} else if (loc.equals(plugin.redCenter)) {
				plugin.playerPositions.remove(player);
				plugin.redCenterSign.setLine(3, "Click to claim");
				plugin.redCenterSign.update();
			} else if (loc.equals(plugin.redRight)) {
				plugin.playerPositions.remove(player);
				plugin.redRightSign.setLine(3, "Click to claim");
				plugin.redRightSign.update();
			} else if (loc.equals(plugin.blueLeft)) {
				plugin.playerPositions.remove(player);
				plugin.blueLeftSign.setLine(3, "Click to claim");
				plugin.blueLeftSign.update();
			} else if (loc.equals(plugin.blueCenter)) {
				plugin.playerPositions.remove(player);
				plugin.blueCenterSign.setLine(3, "Click to claim");
				plugin.blueCenterSign.update();
			} else if (loc.equals(plugin.blueRight)) {
				plugin.playerPositions.remove(player);
				plugin.blueRightSign.setLine(3, "Click to claim");
				plugin.blueRightSign.update();
			}
		}
	}

}

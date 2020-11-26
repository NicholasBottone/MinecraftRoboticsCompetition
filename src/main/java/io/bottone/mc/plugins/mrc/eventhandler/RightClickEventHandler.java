package io.bottone.mc.plugins.mrc.eventhandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;

public class RightClickEventHandler implements Listener {

	private MRC plugin;

	public RightClickEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {

		if (event.getMaterial() == Material.SNOWBALL && !event.getPlayer().isInsideVehicle()) {
			event.setCancelled(true);
			return;
		}

		if (event.getMaterial() == Material.IRON_DOOR) {
			// Send back to hub bungee server
			plugin.sendToBungeeServer(event.getPlayer(), "Hub");
			return;
		}

		if (event.getMaterial() == Material.ACACIA_DOOR) {
			if (plugin.gameState != GameState.INGAME)
				return;
			// Dismount from boat to begin climb
			Entity vehicle = event.getPlayer().getVehicle();
			if (vehicle != null) {
				vehicle.remove();
			}
			event.getPlayer().getInventory().remove(Material.ACACIA_DOOR);
			event.getPlayer().getInventory().remove(Material.BOW);
			event.getPlayer().getInventory().remove(Material.CROSSBOW);
			if (plugin.playerClasses.get(event.getPlayer()) == PlayerClass.INSTACLIMB) {
				// Instant hang, award points for hang
				if (!plugin.hungPlayers.contains(event.getPlayer())) {
					plugin.playerData.get(event.getPlayer()).addPoints(20);
					plugin.hungPlayers.add(event.getPlayer());
					if (plugin.redPlayers.contains(event.getPlayer())) {
						plugin.redScore += 20;
						plugin.redEndgame += 20;
					} else if (plugin.bluePlayers.contains(event.getPlayer())) {
						plugin.blueScore += 20;
						plugin.blueEndgame += 20;
					}
					event.getPlayer().sendMessage(MRC.PREFIX + "You have hung.");
				}
			}
			return;
		}

		if (event.getClickedBlock() != null) {

			if (event.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
				if (event.getClickedBlock().equals(plugin.redLeftSign.getBlock())) {
					event.getPlayer().performCommand("pos redleft");
				} else if (event.getClickedBlock().equals(plugin.redCenterSign.getBlock())) {
					event.getPlayer().performCommand("pos redcenter");
				} else if (event.getClickedBlock().equals(plugin.redRightSign.getBlock())) {
					event.getPlayer().performCommand("pos redright");
				} else if (event.getClickedBlock().equals(plugin.blueLeftSign.getBlock())) {
					event.getPlayer().performCommand("pos blueleft");
				} else if (event.getClickedBlock().equals(plugin.blueCenterSign.getBlock())) {
					event.getPlayer().performCommand("pos bluecenter");
				} else if (event.getClickedBlock().equals(plugin.blueRightSign.getBlock())) {
					event.getPlayer().performCommand("pos blueright");
				}
				return;
			}

			if (event.getClickedBlock().getType() == Material.BELL) {
				if (plugin.gameState != GameState.INGAME)
					return;

				if (plugin.redPlayers.contains(event.getPlayer())) {
					// if they are on the red alliance
					Location loc = event.getClickedBlock().getLocation();
					loc.setY(loc.getY() + 1);
					if (loc.getBlock().getType() != Material.RED_CONCRETE)
						return;
				} else if (plugin.bluePlayers.contains(event.getPlayer())) {
					// if they are on the blue alliance
					Location loc = event.getClickedBlock().getLocation();
					loc.setY(loc.getY() + 1);
					if (loc.getBlock().getType() != Material.BLUE_CONCRETE)
						return;
				} else {
					return;
				}

				// Fully hung, award points for hang
				if (!plugin.hungPlayers.contains(event.getPlayer())) {
					plugin.playerData.get(event.getPlayer()).addPoints(20);
					plugin.hungPlayers.add(event.getPlayer());
					if (plugin.redPlayers.contains(event.getPlayer())) {
						plugin.redScore += 20;
						plugin.redEndgame += 20;
					} else if (plugin.bluePlayers.contains(event.getPlayer())) {
						plugin.blueScore += 20;
						plugin.blueEndgame += 20;
					}
					event.getPlayer().sendMessage(MRC.PREFIX + "You have hung.");
				}
			}

		}
	}

}

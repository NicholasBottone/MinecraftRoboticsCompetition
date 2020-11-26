package io.bottone.mc.plugins.mrc.gametick;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;
import io.bottone.mc.plugins.mrc.managers.MRCPlayerData;
import io.bottone.mc.plugins.mrc.managers.MRCTitleManager;

public class CountdownTick {

	public static void doTick(MRC plugin) {
		// Pre-game state, starting soon.

		if (plugin.countdown > 0 && !plugin.joinable) {
			// Final countdown.

			// Show title
			MRCTitleManager.showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + plugin.countdown,
					" ");

			// Play sound
			plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_BASS, 100, 1);
		}
		if (plugin.countdown <= 0 && !plugin.joinable) {
			// Match starts.

			// Show title
			MRCTitleManager.showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "GO",
					ChatColor.LIGHT_PURPLE + "Good luck!");

			// Play sound
			plugin.world.playSound(plugin.redRight, Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);

			// Time to start the match!
			plugin.gameState = GameState.INGAME;
			plugin.countdown = 150;
			plugin.getServer().broadcastMessage(MRC.PREFIX + "Let the match begin!");

			for (Player player : plugin.players) {
				Entity e = player.getVehicle();
				if (e != null) {
					plugin.killedBoats.add(e.getUniqueId());
					e.remove();
				}
			}

			plugin.arena.clearEntities();

			for (Player player : plugin.playerPositions.keySet()) {
				Location position = plugin.playerPositions.get(player);

				// Init player data
				plugin.playerData.put(player, new MRCPlayerData(player.getName()));

				// Give players their 3 starting power cells
				if (plugin.playerClasses.get(player) == PlayerClass.SNOWBALL)
					plugin.arena.givePowerCells(player, 3, Material.SNOWBALL);
				else
					plugin.arena.givePowerCells(player, 3, Material.ARROW);

				// Teleport players to their positions
				player.teleport(position);
				plugin.world.spawnEntity(position, EntityType.BOAT).addPassenger(player);
			}

			// Spawn initial power cells on the field
			ItemStack arrowStack = new ItemStack(Material.ARROW);
			ItemMeta meta = arrowStack.getItemMeta();
			meta.setDisplayName("Power Cell");
			arrowStack.setItemMeta(meta);
			for (Location loc : plugin.powerCellSpots) {
				plugin.world.dropItem(loc, arrowStack).setVelocity(new Vector(0, 0, 0));
			}

			// Setup the loading bay chest holograms
			plugin.redBayHolo = HologramsAPI.createHologram(plugin, plugin.redBayLoc);
			plugin.blueBayHolo = HologramsAPI.createHologram(plugin, plugin.blueBayLoc);

			plugin.redBayLine = plugin.redBayHolo.appendTextLine(plugin.redBay + " Power Cells");
			plugin.blueBayLine = plugin.blueBayHolo.appendTextLine(plugin.blueBay + " Power Cells");

			plugin.redBayHolo.appendItemLine(new ItemStack(Material.ARROW));
			plugin.blueBayHolo.appendItemLine(new ItemStack(Material.ARROW));

			return;
		}

		plugin.countdown--;

	}

}

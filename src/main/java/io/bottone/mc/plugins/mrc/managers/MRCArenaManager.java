/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.managers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MRCArenaManager {

	private final MRC plugin;

	public MRCArenaManager(MRC plugin) {
		this.plugin = plugin;
	}

	public void resetArena() {
		// Clear all lists
		clearAllLists();

		// Get rid of the holograms
		if (plugin.redBayHolo != null) {
			plugin.redBayHolo.delete();
			plugin.blueBayHolo.delete();
		}

		// Delete the vines
		pasteBlank();

		// Set the ingame time to a random time
		plugin.world.setTime(MRC.rand.nextInt(24000));

		// Clear entities
		clearEntities();

		// Reset position signs
		resetPositionSigns();
	}

	private void clearAllLists() {
		plugin.players.clear();
		plugin.tempSpectators.clear();
		plugin.redPlayers.clear();
		plugin.bluePlayers.clear();
		plugin.hungPlayers.clear();
		plugin.playerData.clear();
		plugin.playerPositions.clear();
	}

	private void resetPositionSigns() {
		plugin.redLeftSign.setLine(3, "Click to claim");
		plugin.redCenterSign.setLine(3, "Click to claim");
		plugin.redRightSign.setLine(3, "Click to claim");
		plugin.blueLeftSign.setLine(3, "Click to claim");
		plugin.blueCenterSign.setLine(3, "Click to claim");
		plugin.blueRightSign.setLine(3, "Click to claim");
		plugin.redLeftSign.update();
		plugin.redCenterSign.update();
		plugin.redRightSign.update();
		plugin.blueLeftSign.update();
		plugin.blueCenterSign.update();
		plugin.blueRightSign.update();
	}

	public void clearEntities() {
		for (Entity e : plugin.world.getEntities()) {
			if (e instanceof Player)
				continue;
			e.remove();
		}
	}

	private void pasteBlank() {

		CuboidRegion region = new CuboidRegion(BlockVector3.at(-78, 91, 50), BlockVector3.at(-85, 75, 57));
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
				.getEditSession(BukkitAdapter.adapt(plugin.world), -1)) {
			ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
					region.getMinimumPoint());
			Operations.complete(forwardExtentCopy);
		} catch (WorldEditException ex) {
			plugin.l.severe("Could not WorldEdit copy blank!");
			ex.printStackTrace();
			return;
		}

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
				.getEditSession(BukkitAdapter.adapt(plugin.world), -1)) {
			Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
					.to(BlockVector3.at(-36, 74, -3)).ignoreAirBlocks(false).copyEntities(false).copyBiomes(false)
					.build();
			Operations.complete(operation);
		} catch (WorldEditException ex) {
			plugin.l.severe("Could not WorldEdit paste blank!");
			ex.printStackTrace();
		}

	}

	public void pasteVines() {

		CuboidRegion region = new CuboidRegion(BlockVector3.at(18, 91, 58), BlockVector3.at(11, 75, 65));
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
				.getEditSession(BukkitAdapter.adapt(plugin.world), -1)) {
			ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
					region.getMinimumPoint());
			Operations.complete(forwardExtentCopy);
		} catch (WorldEditException ex) {
			plugin.l.severe("Could not WorldEdit copy vines!");
			ex.printStackTrace();
			return;
		}

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
				.getEditSession(BukkitAdapter.adapt(plugin.world), -1)) {
			Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
					.to(BlockVector3.at(-36, 74, -3)).ignoreAirBlocks(false).copyEntities(false).copyBiomes(false)
					.build();
			Operations.complete(operation);
		} catch (WorldEditException ex) {
			plugin.l.severe("Could not WorldEdit paste vines!");
			ex.printStackTrace();
		}

	}

	public void spawnRandomPC() {
		// Respawn the power cell in a random spot
		ItemStack arrowStack = new ItemStack(Material.ARROW);

		ItemMeta meta = arrowStack.getItemMeta();
		if (meta != null) {
			meta.setDisplayName("Power Cell");
		}
		arrowStack.setItemMeta(meta);

		plugin.world.dropItemNaturally(new Location(plugin.world, MRC.random(-40, -24), 76, MRC.random(-19, 22)),
				arrowStack);
	}

	public void givePowerCells(Player player, int number) {
		Material material;
		switch (plugin.playerClasses.get(player)) {
			case SNOWBALL:
				material = Material.SNOWBALL;
				break;
			case TRIDENT:
				material = Material.TRIDENT;
				break;
			default:
				material = Material.ARROW;
				break;
		}

		ItemStack powerCellStack;

		if (plugin.playerClasses.get(player) == PlayerClass.TRIDENT) {
			// If trident class, add new tridents to existing item stack
			powerCellStack = getTridentItemStack(player);
			if (powerCellStack != null) {
				powerCellStack.setAmount(powerCellStack.getAmount() + number);
				return;
			} else {
				powerCellStack = new ItemStack(material, number);
			}
		} else {
			powerCellStack = new ItemStack(material, number);
		}

		ItemMeta meta = powerCellStack.getItemMeta();
		if (meta != null) {
			meta.setDisplayName("Power Cell");
			meta.setUnbreakable(true);
		}
		powerCellStack.setItemMeta(meta);

		if (plugin.playerClasses.get(player) == PlayerClass.TRIDENT && !player.isInsideVehicle()) {
			// tridents should have riptide during endgame
			powerCellStack.addEnchantment(Enchantment.RIPTIDE, 1);
		}

		player.getInventory().addItem(powerCellStack);
	}

	public int getPowerCellCount(Player player) {
		int powerCells = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			//noinspection ConstantConditions
			if (item != null && (item.getType() == Material.ARROW || item.getType() == Material.SNOWBALL ||
					item.getType() == Material.TRIDENT))
				powerCells += item.getAmount();
		}
		return powerCells;
	}

	public boolean canPickupPowerCell(Player player) {
		int powerCells = getPowerCellCount(player);

		int maxPowerCells = 5;
		switch (plugin.playerClasses.get(player)) {
			case BOW:
			case CROSSBOW:
				maxPowerCells = 5;
				break;
			case TRIDENT:
			case SNOWBALL:
				maxPowerCells = 3;
				break;
			case INSTACLIMB:
				maxPowerCells = 4;
				break;
		}

		return powerCells < maxPowerCells;
	}

	public ItemStack getTridentItemStack(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			//noinspection ConstantConditions
			if (item != null && item.getType() == Material.TRIDENT) {
				return item;
			}
		}
		return null;
	}

	public void clearPowerCellsFromPlayer(Player player) {
		player.getInventory().remove(Material.ARROW);
		player.getInventory().remove(Material.SNOWBALL);
		player.getInventory().remove(Material.TRIDENT);
	}

}

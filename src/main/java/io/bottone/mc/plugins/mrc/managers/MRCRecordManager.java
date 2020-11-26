package io.bottone.mc.plugins.mrc.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import io.bottone.mc.plugins.mrc.MRC;

public class MRCRecordManager {

	private MRC plugin;

	public MRCRecordManager(MRC plugin) {
		this.plugin = plugin;
		loadWorldRecords();
	}

	public void loadWorldRecords() {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();

		plugin.personalBests = plugin.getConfig().getConfigurationSection("records.personalbests").getValues(false);
		setRecordSkulls();
	}

	public void submitScore(Player p, int score) {

		if (plugin.personalBests.containsKey(p.getName())) {
			if ((Integer) plugin.personalBests.get(p.getName()) < score) { // new PB
				plugin.personalBests.put(p.getName(), score);
				p.sendMessage(MRC.PREFIX + "New personal best!");

				plugin.getConfig().createSection("records.personalbests", plugin.personalBests);
				plugin.saveConfig();
				setRecordSkulls();
			}
		} else { // no PB saved
			plugin.personalBests.put(p.getName(), score);
			p.sendMessage(MRC.PREFIX + "New personal best!");

			plugin.getConfig().createSection("records.personalbests", plugin.personalBests);
			plugin.saveConfig();
			setRecordSkulls();
		}

	}

	@SuppressWarnings("deprecation")
	private void setRecordSkulls() {
		plugin.worldRecordHolders = new String[4];
		plugin.worldRecordScores = new int[] { 0, 0, 0, 0 };

		for (String playerName : plugin.personalBests.keySet()) {
			int score = (int) plugin.personalBests.get(playerName);
			if (score > plugin.worldRecordScores[0]) {
				plugin.worldRecordScores[3] = plugin.worldRecordScores[2];
				plugin.worldRecordScores[2] = plugin.worldRecordScores[1];
				plugin.worldRecordScores[1] = plugin.worldRecordScores[0];

				plugin.worldRecordHolders[3] = plugin.worldRecordHolders[2];
				plugin.worldRecordHolders[2] = plugin.worldRecordHolders[1];
				plugin.worldRecordHolders[1] = plugin.worldRecordHolders[0];

				plugin.worldRecordHolders[0] = playerName;
				plugin.worldRecordScores[0] = score;
				continue;
			}
			if (score > plugin.worldRecordScores[1]) {
				plugin.worldRecordScores[3] = plugin.worldRecordScores[2];
				plugin.worldRecordScores[2] = plugin.worldRecordScores[1];

				plugin.worldRecordHolders[3] = plugin.worldRecordHolders[2];
				plugin.worldRecordHolders[2] = plugin.worldRecordHolders[1];

				plugin.worldRecordHolders[1] = playerName;
				plugin.worldRecordScores[1] = score;
				continue;
			}
			if (score > plugin.worldRecordScores[2]) {
				plugin.worldRecordScores[3] = plugin.worldRecordScores[2];

				plugin.worldRecordHolders[3] = plugin.worldRecordHolders[2];

				plugin.worldRecordHolders[2] = playerName;
				plugin.worldRecordScores[2] = score;
				continue;
			}
			if (score > plugin.worldRecordScores[3]) {
				plugin.worldRecordHolders[3] = playerName;
				plugin.worldRecordScores[3] = score;
				continue;
			}
		}

		plugin.record1.getBlock().setType(Material.PLAYER_WALL_HEAD);
		Skull skull = (Skull) plugin.record1.getBlock().getState();
		skull.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(plugin.worldRecordHolders[0]));
		skull.setRotation(BlockFace.WEST);
		skull.update();

		plugin.record2.getBlock().setType(Material.PLAYER_WALL_HEAD);
		skull = (Skull) plugin.record2.getBlock().getState();
		skull.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(plugin.worldRecordHolders[1]));
		skull.setRotation(BlockFace.WEST);
		skull.update();

		plugin.record3.getBlock().setType(Material.PLAYER_WALL_HEAD);
		skull = (Skull) plugin.record3.getBlock().getState();
		skull.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(plugin.worldRecordHolders[2]));
		skull.setRotation(BlockFace.WEST);
		skull.update();

		plugin.record4.getBlock().setType(Material.PLAYER_WALL_HEAD);
		skull = (Skull) plugin.record4.getBlock().getState();
		skull.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(plugin.worldRecordHolders[3]));
		skull.setRotation(BlockFace.WEST);
		skull.update();

		// TODO: some sort of feature that shows player names/scores on the heads (low
		// priority)

	}

}

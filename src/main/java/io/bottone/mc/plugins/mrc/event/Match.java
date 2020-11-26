package io.bottone.mc.plugins.mrc.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Match {

	private int matchNum;

	private String[] redPlayers;
	private String[] bluePlayers;

	public Match(String line) {
		String[] split = line.split(",");

		if (split.length >= 7) {

			matchNum = Integer.parseInt(split[0]);
			redPlayers = new String[] { split[1], split[2], split[3] };
			bluePlayers = new String[] { split[4], split[5], split[6] };

		} else if (split.length >= 5) {

			matchNum = Integer.parseInt(split[0]);
			redPlayers = new String[] { split[1], split[2] };
			bluePlayers = new String[] { split[3], split[4] };

		} else if (split.length >= 3) {

			matchNum = Integer.parseInt(split[0]);
			redPlayers = new String[] { split[1] };
			bluePlayers = new String[] { split[2] };

		} else {

			throw new ArrayIndexOutOfBoundsException("Not enough players in match schedule line");

		}
	}

	public int getMatchNum() {
		return matchNum;
	}

	public String[] getRedPlayers() {
		return redPlayers;
	}

	public String[] getBluePlayers() {
		return bluePlayers;
	}

	public boolean hasPlayer(Player player) {
		for (String s : redPlayers) {
			if (player.getName().equals(s)) {
				return true;
			}
		}
		for (String s : bluePlayers) {
			if (player.getName().equals(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean teleportPlayer(Player player, Location red, Location blue, Location defaultLoc) {
		for (String s : redPlayers) {
			if (player.getName().equals(s)) {
				player.teleport(red);
				return true;
			}
		}
		for (String s : bluePlayers) {
			if (player.getName().equals(s)) {
				player.teleport(blue);
				return true;
			}
		}
		player.teleport(defaultLoc);
		return false;
	}

	public void teleportPlayers(Location red, Location blue, String PREFIX) {
		for (String s : redPlayers) {
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
				p.teleport(red);
				p.sendMessage(PREFIX + "Choose a position and class to ready-up.");
			}
		}
		for (String s : bluePlayers) {
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
				p.teleport(blue);
				p.sendMessage(PREFIX + "Choose a position and class to ready-up.");
			}
		}
	}

}
package io.bottone.mc.plugins.mrc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class MRC extends JavaPlugin implements Listener {

	public static enum GameState {
		LOBBY, COUNTDOWN, INGAME, FINISHED
	}

	public static final String PREFIX = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "MRC"
			+ ChatColor.DARK_PURPLE + "] " + ChatColor.AQUA;

	private Logger l;

	private Location stadium;
	private Location red1;
	private Location red2;
	private Location red3;
	private Location blue1;
	private Location blue2;
	private Location blue3;

	private List<Player> players = new ArrayList<>();
	private List<Player> spectators = new ArrayList<>();

	private GameState gameState = GameState.LOBBY;
	private int countdown = 30;
	private boolean joinable = true;

	private int redScore = 0;
	private int redPC = 0;
	private int redEndgame = 0;

	private int blueScore = 0;
	private int bluePC = 0;
	private int blueEndgame = 0;

//	private Economy econ;

	private MRC plugin;

	@Override
	public void onEnable() {
		l = getLogger();
		plugin = this;

		/*
		 * // Check for Vault
		 *
		 * if (getServer().getPluginManager().getPlugin("Vault") == null) {
		 * l.log(Level.SEVERE, "Unable to hook with Vault, disabling MRC!");
		 * getServer().getPluginManager().disablePlugin(this); return; }
		 *
		 * // Hook Vault economy
		 *
		 * RegisteredServiceProvider<Economy> rspe =
		 * getServer().getServicesManager().getRegistration(Economy.class); econ =
		 * rspe.getProvider(); if (econ == null) { l.log(Level.SEVERE,
		 * "Unable to hook with Vault economy, disabling MRC!");
		 * getServer().getPluginManager().disablePlugin(this); return; } else
		 * l.log(Level.INFO, "MRC successfully hooked to Vault economy " +
		 * econ.getName());
		 */

		// GAME TICK (every second)
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {

				switch (gameState) {

				case LOBBY:
					joinable = true;

					if (players.size() >= 1) {
						// We have player(s) ... start the countdown!
						gameState = GameState.COUNTDOWN;
						countdown = 30;

						redScore = 0;
						redPC = 0;
						redEndgame = 0;
						blueScore = 0;
						bluePC = 0;
						blueEndgame = 0;

						getServer().broadcastMessage(PREFIX + ChatColor.BOLD + "Match starting in 30 seconds!");
						break;
					}
					break;

				case COUNTDOWN:
					if (players.size() < 1) {
						// We don't have enough players ... abort the countdown!
						gameState = GameState.LOBBY;
						countdown = 30;
						joinable = true;

						getServer().broadcastMessage(PREFIX + "Match countdown aborted due to lack of players.");
						break;
					}
					if (countdown <= 0 && joinable) {
						joinable = false;
						countdown = 10;

						// Teleport players to their positions
						// TODO

						// Give players their bows
						for (Player player : players) {
							player.getInventory().addItem(new ItemStack(Material.BOW, 1));
						}
					}
					if (countdown > 0 && !joinable) {
						// Show title
						showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + countdown, "");
					}
					if (countdown <= 0 && !joinable) {
						// Show title
						showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "GO",
								ChatColor.LIGHT_PURPLE + "Good luck!");

						// Time to start the match!
						gameState = GameState.INGAME;
						countdown = 150;
						getServer().broadcastMessage(PREFIX + "Let the match begin!");
						// TODO: Start the match
						break;
					}

					countdown--;

					break;

				case INGAME:
					joinable = false;
					// TODO
					break;

				case FINISHED:
					joinable = false;
					// TODO
					break;

				}

				// Update scoreboards for all players and spectators
				updateScoreboards();

			}
		}, 100, 20);

		// waits for worlds to load before setting locations
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {
				stadium = new Location(getServer().getWorld("MRC"), -0.5, 35.5, -1.5, 0, 0); // TODO get coords
				red1 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				red2 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				red3 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				blue1 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				blue2 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				blue3 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);

				getServer().getPluginManager().registerEvents(plugin, plugin);
				l.log(Level.INFO, "Locations loaded and MRC activated.");
			}

		}, 200);

	}

	private void showInstantTitle(String title, String subtitle) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle, 0, 20, 0);
		}
	}

	private void showTitle(String title, String subtitle) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle, 20, 100, 20);
		}
	}

	private void updateScoreboards() {

		BottScoreboard sb = new BottScoreboard(ChatColor.GREEN.toString() + ChatColor.BOLD + "MRC");

		switch (gameState) {

		case LOBBY:
			sb.put(2, "Waiting to start the game!");
			sb.put(1, players.size() + " players");
			sb.put(0, "");
			sb.put(-1, ChatColor.GREEN + "mc.bottone.io");
			break;

		case COUNTDOWN:
			if (joinable) {
				sb.put(2, "Match starting in " + countdown);
			} else {
				sb.put(2, ChatColor.BOLD + "Here we go in " + countdown);
			}
			sb.put(1, players.size() + " players");
			sb.put(0, "");
			sb.put(-1, ChatColor.GREEN + "mc.bottone.io");
			break;

		case INGAME:
			sb.put(9, ChatColor.BOLD + "Timer: " + countdown);
			sb.put(8, "");
			sb.put(7, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
			sb.put(6, ChatColor.RED.toString() + "Score: " + redScore);
			sb.put(5, ChatColor.RED.toString() + "Power Cells: " + redPC);
			sb.put(4, "");
			sb.put(3, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
			sb.put(2, ChatColor.BLUE.toString() + "Score: " + blueScore);
			sb.put(1, ChatColor.BLUE.toString() + "Power Cells: " + bluePC);
			break;

		case FINISHED:
			if (redScore > blueScore) {
				sb.put(9, ChatColor.RED.toString() + ChatColor.BOLD + "RED WINS!");
			} else if (blueScore > redScore) {
				sb.put(9, ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE WINS!");
			} else {
				sb.put(9, ChatColor.BOLD + "TIE!");
			}
			sb.put(8, "");
			sb.put(7, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
			sb.put(6, ChatColor.RED.toString() + "Score: " + redScore);
			sb.put(5, ChatColor.RED.toString() + "Power Cells: " + redPC);
			sb.put(4, "");
			sb.put(3, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
			sb.put(2, ChatColor.BLUE.toString() + "Score: " + blueScore);
			sb.put(1, ChatColor.BLUE.toString() + "Power Cells: " + bluePC);
			sb.put(0, "");
			sb.put(-1, ChatColor.GREEN + "mc.bottone.io");
			break;

		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			sb.setScoreboard(player);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerJoinEvent event) {

		event.getPlayer().teleport(stadium);
		event.getPlayer().setGameMode(GameMode.ADVENTURE);
		event.getPlayer().getInventory().clear();

		if (players.size() < 6 && joinable) {
			players.add(event.getPlayer());
		} else {
			spectators.add(event.getPlayer());
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
		}

	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		// TODO
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(PREFIX + "Must be a player to do that!");
			return true;
		}

		if (label.toLowerCase().startsWith("spectate")) {
			Player player = (Player) sender;

			if (joinable) {

				players.remove(player);
				spectators.add(player);

				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(stadium);

				player.sendMessage(PREFIX + "You are now spectating the upcoming match!");

			} else {

				player.sendMessage(PREFIX + "You can't do that right now!");

			}
		}

		return true;

	}

}

package io.bottone.mc.plugins.mrc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;
import io.bottone.mc.plugins.mrc.event.Match;
import io.bottone.mc.plugins.mrc.event.MatchScheduleManager;
import io.bottone.mc.plugins.mrc.gametick.MRCInit;
import io.bottone.mc.plugins.mrc.managers.MRCArenaManager;
import io.bottone.mc.plugins.mrc.managers.MRCPlayerData;
import io.bottone.mc.plugins.mrc.managers.MRCScoreboardManager;
import net.milkbowl.vault.economy.Economy;

public final class MRC extends JavaPlugin {

	public static final String PREFIX = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "MRC"
			+ ChatColor.DARK_PURPLE + "] " + ChatColor.AQUA;

	public static final int WIN_REWARD = 15;
	public static final int TIE_REWARD = 5;

	public Logger l;
	public static Random rand = new Random();

	public Location redPositionSelect;
	public Location bluePositionSelect;

	public Location stadiumStands;
	public Location redRight;
	public Location redCenter;
	public Location redLeft;
	public Location blueRight;
	public Location blueCenter;
	public Location blueLeft;
	public Location record1;
	public Location record2;
	public Location record3;
	public Location record4;

	public Sign redRightSign;
	public Sign redCenterSign;
	public Sign redLeftSign;
	public Sign blueRightSign;
	public Sign blueCenterSign;
	public Sign blueLeftSign;

	public List<Location> powerCellSpots = new ArrayList<>();

	public World world;

	public Location redBayLoc;
	public Location blueBayLoc;
	public Hologram redBayHolo;
	public Hologram blueBayHolo;
	public TextLine redBayLine;
	public TextLine blueBayLine;

	public List<Player> players = new ArrayList<>();
	public List<Player> spectators = new ArrayList<>();

	public List<Player> redPlayers = new ArrayList<>();
	public List<Player> bluePlayers = new ArrayList<>();

	public HashMap<Player, MRCPlayerData> playerData = new HashMap<>();
	public HashMap<Player, Location> playerPositions = new HashMap<>();
	public HashMap<Player, PlayerClass> playerClasses = new HashMap<>();

	public Map<String, Object> personalBests = new HashMap<>();
	public String[] worldRecordHolders;
	public int[] worldRecordScores;

	public List<Player> hungPlayers = new ArrayList<>();

	public List<UUID> killedBoats = new ArrayList<>();

	public GameState gameState = GameState.LOBBY;
	public int countdown = 0;
	public boolean joinable = true;

	public int redScore = 0;
	public int redPC = 0;
	public int redEndgame = 0;
	public int redBay = 5;

	public int blueScore = 0;
	public int bluePC = 0;
	public int blueEndgame = 0;
	public int blueBay = 5;

	public Economy econ;

	public MRCArenaManager arena;
	public MRCScoreboardManager scoreboard;
	public MatchScheduleManager schedule;

	public HashMap<Integer, Match> matches = new HashMap<>();
	public int matchNumber = 1;
	public boolean matchReady = false;

	@Override
	public void onEnable() {

		l = getLogger();
		new MRCInit(this);

	}

	public void sendToBungeeServer(Player player, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}

	public static int random(int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}

}

package io.bottone.mc.plugins.mrc.event;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.Bukkit;

import io.bottone.mc.plugins.mrc.MRC;

public class MatchScheduleManager {

	private MRC plugin;

	public MatchScheduleManager(MRC plugin) {
		this.plugin = plugin;
		importMatchSchedule();
	}

	public boolean importMatchSchedule() {

		try {
			plugin.matches = new HashMap<>();
			Scanner schedule = new Scanner(new File("F:\\MRC-Schedules\\" + Bukkit.getServer().getMotd() + ".csv"));
			if (schedule.hasNextLine())
				schedule.nextLine(); // skip over headers
			while (schedule.hasNextLine()) { // for each match
				String s = schedule.nextLine();
				if (s.length() < 4)
					continue;
				try {
					Match m = new Match(s);
					plugin.matches.put(m.getMatchNum(), m);
				} catch (Exception e) {
					plugin.l.warning("schedule csv line: " + s);
					plugin.l.warning(e.getMessage());
				}
			}
			return true;
		} catch (Exception e) {
			plugin.l.severe("Unable to import match schedule (schedule csv)");
			plugin.l.severe(e.getMessage());
			return false;
		}

	}

}

/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

public class MRCScoreboard {

	private final Scoreboard s;
	private final Objective o1;

	public MRCScoreboard(String title) {
		s = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();

		o1 = s.registerNewObjective("dashboard1", "dummy", title);
		o1.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void put(int line, String string) {
		o1.getScore(string).setScore(line);
	}

	public void setScoreboard(Player p) {
		p.setScoreboard(s);
	}

}

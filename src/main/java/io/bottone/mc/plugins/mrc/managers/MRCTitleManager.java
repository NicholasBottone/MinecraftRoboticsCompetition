/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MRCTitleManager {

	public static void showInstantTitle(String title, String subtitle) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle, 0, 20, 0);
		}
	}

	public static void showTitle(String title, String subtitle) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle, 20, 100, 20);
		}
	}

}

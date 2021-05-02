/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.managers;

public class MRCPlayerData {

	private final String playerName;
	private int shots = 0;
	private int inners = 0;
	private int outers = 0;
	private int lowers = 0;
	private int pointsContributed = 0;

	public MRCPlayerData(String playerName) {
		this.playerName = playerName;
	}

	public void addMiss() {
		shots++;
	}

	public void addLower(boolean auto) {
		shots++;
		lowers++;
		pointsContributed += auto ? 2 : 1;
	}

	public void addInner(boolean auto) {
		shots++;
		inners++;
		pointsContributed += auto ? 6 : 3;
	}

	public void addOuter(boolean auto) {
		shots++;
		outers++;
		pointsContributed += auto ? 4 : 2;
	}

	public void addPoints(int points) {
		pointsContributed += points;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getShots() {
		return shots;
	}

	public int getInners() {
		return inners;
	}

	public int getOuters() {
		return outers;
	}

	public int getLowers() {
		return lowers;
	}

	public int getPointsContributed() {
		return pointsContributed;
	}

	@Override
	public String toString() {
		return String.format("%s: %d pts, %d%% acc (%dI/%dO/%dL)", playerName, pointsContributed, getAccuracyPercent(),
				getInners(), getOuters(), getLowers());
	}

	public int getAccuracyPercent() {
		// 100 represents 100%
		return (int) (((inners + outers) / (double) shots) * 100);
	}

	public int getInnersPercent() {
		// 100 represents 100%
		return (int) ((inners / (double) shots) * 100);
	}

}

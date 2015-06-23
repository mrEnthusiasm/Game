package game.logic;

import game.battles.Map;
import game.logic.Attack.AttackType;
import game.player.Blake;
import game.player.Player;
import game.player.Ruby;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Main class for keeping track of the game state.
 * 
 * @author Daniel Schmidt
 *
 */
public class Battle {
	private Terrain[][] terrain;
	private int numCols;
	private int numRows;

	private List<Player> allies;
	private List<Player> enemies;
	private List<Player> playerOrder;
	private int round;
	private int totalRounds;
	private Player curPlayer;
	private Vertex[][] curPlayerMoves;
	private boolean alliesTurn = true;

	Random rand = new Random();

	public Battle(Map map, List<Player> allyList, List<Player> enemyList) {
		this.numCols = map.getNumberColumns();
		this.numRows = map.getNumberRows();

		curPlayerMoves = new Vertex[numCols][numRows];
		terrain = map.getTerrain();

		this.allies = allyList;
		this.enemies = enemyList;
		this.playerOrder = new ArrayList<Player>();

		for (Player p : allies) {
			terrain[p.getXValue()][p.getYValue()].occupy(p);
		}

		for (Player p : enemies) {
			terrain[p.getXValue()][p.getYValue()].occupy(p);
		}

		if (allies.size() > enemies.size()) {
			totalRounds = allies.size();
		} else {
			totalRounds = enemies.size();
		}

		for (int i = 0; i < totalRounds; i++) {
			if (i < allies.size()) {
				playerOrder.add(allies.get(i));
			}
			if (i < enemies.size()) {
				playerOrder.add(enemies.get(i));
			}
		}

		// Start Battle
		round = 0;
		curPlayer = playerOrder.get(0);
		updateMoves(curPlayer, curPlayer.getXValue(), curPlayer.getYValue());
	}

	public void nextPlayer() {
		curPlayer.resetActionPoints();
		round++;
		if (round >= playerOrder.size()) {
			round = 0;
		}
		curPlayer = playerOrder.get(round);
		if (allies.contains(curPlayer)) {
			alliesTurn = true;
		} else {
			alliesTurn = false;
		}
		updateMoves(curPlayer, curPlayer.getXValue(), curPlayer.getYValue());
	}

	/**
	 * variation on Dijkstra's algorithm, used to find all squares with path
	 * lengths less than player's max move distance and update curPlayerMoves
	 * array
	 * 
	 * @param player
	 */
	public void updateMoves(Player player, int startingCol, int startingRow) {
		Vertex[][] graph = new Vertex[numCols][numRows];
		PriorityQueue<Vertex> unvisited = new PriorityQueue<Vertex>();
		Vertex origin = new Vertex(startingCol, startingRow);
		origin.setDist(0);
		graph[origin.getX()][origin.getY()] = origin;
		unvisited.add(origin);
		Vertex cur;
		while (!unvisited.isEmpty() && player.canMove()) {
			cur = unvisited.poll();
			if (cur.getDist() > player.getMaxMoveDistance()) {
				// found all possible moves
				break;
			}
			boolean stop = false;
			// go through 3x3 square to get neighbors of cur
			for (int i = cur.getX() - 1; i < cur.getX() + 2; i++) {
				for (int j = cur.getY() - 1; j < cur.getY() + 2; j++) {
					if (i < 0 || j < 0)
						continue; // beyond the map
					if (i >= numCols || j >= numRows)
						continue; // beyond the map
					if (i == cur.getX() && j == cur.getY())
						continue; // center of 3x3 ie: cur
					Terrain t = terrain[i][j];
					if (t.isOccupied()) {
						if (alliesTurn) {// good guys turn
							if (enemies.contains(t.getResident())
									&& (cur != origin)
									&& player.getClass() != Ruby.class) {
								// square is in 'area of control' of enemy
								// player thus is a dead end unless origin
								// square
								// but Ruby is special so she get's to ignore it
								cur.getNeighbors().clear();
								stop = true; // to break out of outer for loop
								break;
							} else {
								// square occupied by ally
								continue;
							}
						} else { // bad guys turn
							if (allies.contains(t.getResident())
									&& (cur != origin)) {
								// square is in 'area of control' of enemy
								// player
								// thus is a dead end unless origin square
								cur.getNeighbors().clear();
								stop = true; // to break out of outer for loop
								break;
							} else {
								// square occupied by ally
								continue;
							}
						}
					}
					if (!t.isTraversable()) {
						continue;
					}
					Vertex tmp = graph[i][j];
					if (tmp == null) {
						tmp = new Vertex(i, j);
						cur.getNeighbors().add(tmp);
						// may add some vertices we later remove because of area
						// of control but their minDist from cur will be
						// positive infinity so it doesn't matter
						unvisited.add(tmp);
						graph[i][j] = tmp;
					} else {
						cur.getNeighbors().add(tmp);
					}
				}
				if (stop) {
					stop = false;
					break;
				}
			}
			Vertex neighbor;
			Iterator<Vertex> itr = cur.getNeighbors().iterator();
			float cost;
			while (itr.hasNext()) {
				neighbor = itr.next();
				if (neighbor.getX() != cur.getX()
						&& neighbor.getY() != cur.getY()) {
					cost = (float) Math.sqrt(2.0);
				} else {
					cost = 1;
				}
				if (cur.getDist() + cost < neighbor.getDist()) {
					neighbor.setDist(cur.getDist() + cost);
					neighbor.setPrevious(cur);
					// re-add vertex because priorityqueue does not have a
					// decrease priority function
					unvisited.add(neighbor);
				}
			}
		}

		// update curplayerMoves array
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				if (graph[i][j] != null) {
					curPlayerMoves[i][j] = graph[i][j];
				} else {
					curPlayerMoves[i][j] = null;
				}
			}
		}
	}

	/**
	 * Checks if player has enough action points to move as well as if the
	 * distance in curPlayerMoves array is less than or equal to the current
	 * player's max move distance. A null value in the array indicates
	 * updateMoves never got to that square and its distance is greater than
	 * current player's max move distance.
	 * 
	 * @param col
	 * @param row
	 * @return true if the player can move to square (col, row), otherwise false
	 */
	public boolean curPlayerCanMoveTo(int col, int row) {
		if (curPlayer.getActionPoints() > 0) {
			if (curPlayerMoves[col][row] != null
					&& curPlayerMoves[col][row].getDist() <= curPlayer
							.getMaxMoveDistance()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Helper function for has cover. finds GCD of num1 and num2
	 * 
	 * @param num1
	 * @param num2
	 * @return greatest common denominator
	 */
	private static int findGCD(int num1, int num2) {
		if (num2 == 0) {
			return num1;
		}
		return findGCD(num2, num1 % num2);
	}

	/**
	 * A sqaure provides cover if it either is of a terrain type that provides
	 * cover (ie TreeTerrain) or is occupied by a Player
	 */
	private boolean providesCover(int col, int row) {
		Terrain t = terrain[col][row];
		if (t.isOccupied()) {
			return true;
		} else if (t.providesCover()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Imagine a straight line between square[col, row] and sqaure[col+xDist,
	 * row+yDist]. This function checks all squares along that line if they
	 * provide cover.
	 * 
	 * @param col
	 *            starting x location
	 * @param row
	 *            startying y location
	 * @param xDist
	 *            distance to target along x axis
	 * @param yDist
	 *            distance to target along y axis
	 * @return true if a sqaure along the line of sight between the two squares
	 *         provides cover, otherwise false
	 */
	private boolean hasCover(int col, int row, int xDist, int yDist) {
		/*
		 * TODO Have a cascade problem of being under cover in a line. For
		 * example R can not shoot any G. R TGGGGGGGGGGGGGGGGG This occurs
		 * because you can only shoot a player if you can draw a straight line
		 * to the center of their square, but a player can provide cover if line
		 * of sight passes through ANY part of their square
		 */
		int gcd = Math.abs(findGCD(xDist, yDist));
		int rise = yDist / gcd;
		int run = xDist / gcd;
		int curX = col - xDist;
		int curY = row - yDist;
		double m = 0;
		if (run != 0) {
			m = (double) rise / (double) run;
		} else {
			rise = rise / Math.abs(rise); // negative or positive one
			// close enough to vertical for even a MASSIVE grid
			m = Double.MAX_VALUE;
		}
		boolean intersection = false;
		double intersect = 0;
		int count = 0;
		double extra = 0.5;
		int step;
		if (m < 1 && m > -1) { // horizontal
			if (Math.abs(run) % 2 == 1 && Math.abs(rise) % 2 == 1) {
				intersection = true;
				intersect = Math.abs(run);
				count = (int) (Math.abs(run) * 0.5);
			}
			extra += (Math.abs(m) / 2); // moving to edge of square
			step = run / Math.abs(run); // normalizes the vector
			while (true) {
				curX += step;
				count++;
				if (intersection && (count == intersect)) {
					curY += Math.signum(rise);
					extra = Math.abs(m);
					count = 0;
					if (providesCover(curX, curY)) {
						return false;
					}
					continue;
				}
				if (curX == col && curY == row) {
					return true;
				} else {
					if (providesCover(curX, curY)) {
						return false;
					}
				}
				extra += Math.abs(m);
				if (extra > 1 && (count + 1 != intersect)) {
					curY += Math.signum(rise);
					// will never equal destination from extra move
					if (providesCover(curX, curY)) {
						return false;
					} else {
						extra -= 1;
					}
				}
			}
		} else if (m > 1 || m < -1) { // vertical
			if (Math.abs(run) % 2 == 1 && Math.abs(rise) % 2 == 1) {
				intersection = true;
				intersect = Math.abs(rise);
				count = (int) (Math.abs(rise) * 0.5);
			}
			extra += (1 / (Math.abs(m) * 2)); // moving to edge of square
			step = rise / Math.abs(rise); // normalizes the vector
			while (true) {
				curY += step;
				count++;
				if (intersection && (count == intersect)) {
					curX += Math.signum(run);
					extra = 1 / Math.abs(m);
					count = 0;
					if (providesCover(curX, curY)) {
						return false;
					}
					continue;
				}
				if (curX == col && curY == row) {
					return true;
				} else {
					if (providesCover(curX, curY)) {
						return false;
					}
				}
				extra += (1 / Math.abs(m));
				if (extra > 1 && (count + 1 != intersect)) {
					curX += Math.signum(run);
					// will never equal destination from extra move
					if (providesCover(curX, curY)) {
						return false;
					} else {
						extra -= 1;
					}
				}
			}
		} else { // diagonal
			while (true) {
				curX += Math.signum(run);
				curY += Math.signum(rise);
				if (curX == col && curY == row) {
					return true;
				}
				if (providesCover(curX, curY)) {
					return false;
				}
			}
		}
	}

	/**
	 * Determines if square[col, row] contains a player, if the current player
	 * has any action points, and if the square is within melee distance.
	 * Currently set up to allow for melee ranges greater than 1, however does
	 * not check if squares in between attacker and defender provide cover.
	 * 
	 * @param col
	 * @param row
	 * @return true if square[col, row] contains a player that can be meleed,
	 *         otherwise false
	 */
	public boolean curPlayerCanMelee(int col, int row) {
		if (curPlayer.getActionPoints() == 0) {
			return false;
		}
		if (terrain[col][row].isOccupied()) {
			int xDist = col - curPlayer.getXValue();
			int yDist = row - curPlayer.getYValue();
			double totalDist = Math.sqrt(xDist * xDist + yDist * yDist);
			double range = curPlayer.getMelee().getRange();
			if (totalDist <= range) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Determines if square[col, row] contains a player, if the current player
	 * has any action points, if the square is within ranged distance, and if
	 * the defender is under cover.
	 * 
	 * @param col
	 * @param row
	 * @return true if square[col, row] contains a player that can be range
	 *         attacked, otherwise false
	 */
	public boolean curPlayerCanRanged(int col, int row) {
		if (curPlayer.getActionPoints() == 0) {
			return false;
		}
		if (terrain[col][row].isOccupied()) {
			int xDist = col - curPlayer.getXValue();
			int yDist = row - curPlayer.getYValue();
			double totalDist = Math.sqrt(xDist * xDist + yDist * yDist);
			if (totalDist <= curPlayer.getRanged().getRange()) {
				return hasCover(col, row, xDist, yDist);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Player getCurPlayer() {
		return curPlayer;
	}

	/**
	 * 
	 * @param col
	 * @param row
	 * @return player at sqaure[col, row], if unoccupied returns null
	 */
	public Player getPlayerAt(int col, int row) {
		if (terrain[col][row].isOccupied()) {
			return terrain[col][row].getResident();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param col
	 * @param row
	 * @return terrain at location sqaure[col, row]
	 */
	public Terrain getSquare(int col, int row) {
		return terrain[col][row];
	}

	/**
	 * Current player attacks specified defender
	 * 
	 * @param defender
	 *            currently selected player
	 * @param type
	 *            attack type as specified in Attack class
	 * @return returns info on if attack succeeded. 0 = death, 1 = damaged, 2 =
	 *         blocked, 3 = dodged, 4 = Blake dodge
	 */

	public int curPlayerAttack(Player defender, AttackType type) {
		Attack attack = null;
		switch (type) {
		case MELEE:
			attack = curPlayer.getMelee();
			break;
		case RANGED:
			attack = curPlayer.getRanged();
			break;
		case SPECIAL:
			// TODO come up with good framework for special attacks
			break;
		}
		curPlayer.useActionPoints(1);
		// check speed vs agility
		int speed = attack.getSpeedValue() + addDTwenty();
		int agility = defender.getAgilityRating().get() + addDTwenty();
		if (agility > speed) {
			return 3;
		}
		// check power vs armor
		int power = attack.getPowerValue() + addDTwenty();
		int armor = defender.getArmorRating().get() + addDTwenty();
		int armorDamage = (int) (power * 0.2);
		if (armor > power) {
			// subtract a bit of armor
			if (armorDamage < defender.getArmorRating().get()) {
				defender.takeArmorDamage(armorDamage);
			} else {
				defender.getArmorRating().set(0);
			}
			return 2;
		}
		// Extra bit of code for Blake
		if (defender.getClass().equals(Blake.class)) {
			int x;
			int y;
			Random rand = new Random();
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					x = defender.getXValue() - 1 + i;
					y = defender.getYValue() - 1 + j;
					if (x < 0 || y < 0)
						continue;
					if (x >= terrain.length || y >= terrain[0].length)
						continue;
					if (x == defender.getXValue() && y == defender.getYValue())
						continue;
					Terrain t = terrain[x][y];
					if (!t.isOccupied()) {
						if (t.isTraversable) {
							if (rand.nextInt(5) == 0) {
								defender.getXProperty().set(x);
								defender.getYProperty().set(y);
								return 4;
							}
						}
					}
				}
			}
		}
		// attack succeeded
		// subtract a bit of armor
		if (armorDamage < defender.getArmorRating().get()) {
			defender.takeArmorDamage(armorDamage);

		} else {
			defender.getArmorRating().set(0);
		}
		// provide damage
		int damage = attack.getDamageProperty().get();
		if (damage < defender.getHealth().get()) {
			defender.takeDamage(damage);
			return 1;
		} else {
			damage = defender.getHealth().get();
			defender.takeDamage(damage);
			if (!enemies.remove(defender)) {
				if (!allies.remove(defender)) {
					System.err
							.println("COULD NOT FIND DEFENDER IN LISTS OF PLAYERS");
				}
			}
			if (!playerOrder.remove(defender)) {
				System.err.println("COULD NOT FIND DEFENDER IN PLAYER ORDER");
			}
			terrain[defender.getXValue()][defender.getYValue()].leave();
			return 0;
		}
	}

	/**
	 * provides psuedorandom d20 dice roll
	 * 
	 * @return value between 1-20 inclusive
	 */
	private int addDTwenty() {
		return rand.nextInt(21) + 1;
	}

	public boolean isBadGuysTurn() {
		return !alliesTurn;
	}

	public int getNumCols() {
		return numCols;
	}

	public int getNumRows() {
		return numRows;
	}

	/**
	 * Creates and returns a timeline animation of the player moving through
	 * every square along the path between its current location and sqaure[col, row]
	 * @param player player to move
	 * @param col destination column
	 * @param row destination row
	 * @return A Timeline animation
	 */
	public Timeline getMovementAnimation(Player player, int col, int row) {
		Vertex curV = curPlayerMoves[col][row];
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		while (curV != null) {
			path.add(0, curV);
			curV = curV.getPrevious();
		}
		int index = 1;
		Timeline moveTimeline = new Timeline();
		moveTimeline.setCycleCount(1);
		while (index < path.size()) {
			KeyValue xValue = new KeyValue(curPlayer.getXProperty(), path.get(
					index).getX());
			KeyValue yValue = new KeyValue(curPlayer.getYProperty(), path.get(
					index).getY());
			KeyFrame kf = new KeyFrame(Duration.millis(250 * index), xValue,
					yValue);
			moveTimeline.getKeyFrames().add(kf);
			index++;
		}
		return moveTimeline;
	}

	public Terrain[][] getTerrain() {
		return terrain;
	}

	public boolean isGoodGuy(Player p) {
		return allies.contains(p);
	}

	/**
	 * Checks for end game.
	 * @return int value indicating if the game is over and who won. 2 = bad guys won,
	 * 1 = good guys won, 0 = game is not over.
	 */
	public int checkForEndGame() {
		if (allies.size() == 0) {
			return 2;
		} else if (enemies.size() == 0) {
			return 1;
		} else {
			return 0;
		}

	}

	public int getNumberOfPlayers() {
		return playerOrder.size();
	}

	/**
	 * Player class has a method, onTurnOver(), that needs to be called at the end of
	 * every turn. This is a convenience method to call that method for every player.
	 */
	public void updatePlayers() {
		for (Player p : playerOrder) {
			p.onTurnOver();
		}
	}
}

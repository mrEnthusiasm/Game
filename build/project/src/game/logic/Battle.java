package game.logic;

import game.battles.Map;
import game.logic.Attack.AttackType;
import game.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import javafx.beans.property.IntegerProperty;

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
	private Boolean[][] curPlayerMoves;
	private boolean alliesTurn = true;
	
	Random rand = new Random();

	public Battle(Map map, List<Player> allyList, List<Player> enemyList) {
		this.numCols = map.getNumberColumns();
		this.numRows = map.getNumberRows();

		curPlayerMoves = new Boolean[numCols][numRows];
		terrain = map.getTerrain();

		this.allies = allyList;
		this.enemies = enemyList;
		this.playerOrder = new ArrayList<Player>();

		for (Player p : allies) {
			terrain[p.getX()][p.getY()].occupy(p);
		}

		for (Player p : enemies) {
			terrain[p.getX()][p.getY()].occupy(p);
		}

		if (allies.size() > enemies.size()) {
			totalRounds = allies.size();
		} else {
			totalRounds = enemies.size();
		}
		
		for(int i=0; i<totalRounds; i++){
			if(i < allies.size()){
				playerOrder.add(allies.get(i));
			}
			if(i < enemies.size()){
				playerOrder.add(enemies.get(i));
			}
		}

		// Start Battle
		round = 0;
		curPlayer = playerOrder.get(0);
		updateMoves(curPlayer);
		//alliesTurnNext = false;
	}

	public void nextPlayer() {
		curPlayer.resetActionPoints();
		round++;
		if(round >= playerOrder.size()){
			round = 0;
		}
		curPlayer = playerOrder.get(round);
		if(allies.contains(curPlayer)){
			alliesTurn = true;
		}else{
			alliesTurn = false;
		}
		updateMoves(curPlayer);
	}

	private void updateMoves(Player player) {
		Vertex[][] graph = new Vertex[numCols][numRows];
		PriorityQueue<Vertex> unvisited = new PriorityQueue<Vertex>();
		Vertex origin = new Vertex(player.getX(), player.getY());
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
					// re-add vertex because priorityqueue does not have a
					// decrease priority function
					unvisited.add(neighbor);
				}
			}
		}

		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				if (graph[i][j] != null) {
					if (graph[i][j].getDist() <= player.getMaxMoveDistance()
							&& graph[i][j].getDist() > 0) {
						curPlayerMoves[i][j] = true;
					} else {
						curPlayerMoves[i][j] = false;
					}
				} else {
					curPlayerMoves[i][j] = false;
				}
			}
		}
	}

	public boolean curPlayerCanMoveTo(int col, int row) {
		if(curPlayer.getActionPoints() >  0){
			return curPlayerMoves[col][row];
		}else{
			return false;
		}
	}

	// private helper functions for canMelee and canRanged to find gcd
	// can't believe this isn't in Math
	private static int findGCD(int num1, int num2) {
		if (num2 == 0) {
			return num1;
		}
		return findGCD(num2, num1 % num2);
	}

	/**
	 * determines if a square at (col, row) provides cover or not
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
	 * checks all squares in a straight line between cur player and col, row for
	 * squares that provide cover.
	 */
	private boolean hasCover(int col, int row, int xDist, int yDist) {
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
			if (run % 2 == 1 && rise % 2 == 1) {
				intersection = true;
				intersect = 0.5 * run;
			}
			extra += (Math.abs(m) / 2); // moving to edge of square
			step = run / Math.abs(run); // normalizes the vector
			while (true) {
				curX += step;
				count++;
				if (intersection && count > intersect) {
					curY += Math.signum(rise);
					extra = (extra + m) - 1;
					count = 0;
				}
				if (curX == col && curY == row) {
					return false;
				} else {
					if (providesCover(curX, curY)) {
						return true;
					}
				}
				extra += Math.abs(m);
				if (extra > 1) {
					curY += Math.signum(rise);
					// will never equal destination from extra move
					if (providesCover(curX, curY)) {
						return true;
					}
					extra -= 1;
				}
			}
		} else if (m > 1 || m < -1) { // vertical
			if (run % 2 == 1 && rise % 2 == 1) {
				intersection = true;
				intersect = 0.5 * rise;
			}
			extra += (1 / (Math.abs(m) * 2)); // moving to edge of square
			step = rise / Math.abs(rise); // normalizes the vector
			while (true) {
				curY += step;
				count++;
				if (intersection && (count>intersect)) {
					curX += Math.signum(run);
					extra = (extra + (1 / Math.abs(m))) - 1;
					count = 0;
				}
				if (curX == col && curY == row) {
					return false;
				} else {
					if (providesCover(curX, curY)) {
						return true;
					}
				}
				extra += (1 / Math.abs(m));
				if (extra > 1) {
					curX += Math.signum(run);
					// will never equal destination from extra move
					if (providesCover(curX, curY)) {
						return true;
					}
					extra -= 1;
				}
			}
		} else { // diagonal
			while (true) {
				curX += Math.signum(run);
				curY += Math.signum(rise);
				if (curX == col && curY == row) {
					return false;
				}
				if (providesCover(curX, curY)) {
					return true;
				}
			}
		}
	}

	public boolean curPlayerCanMelee(int col, int row) {
		if (curPlayer.getActionPoints() == 0) {
			return false;
		}
		if (terrain[col][row].isOccupied()) {
			int xDist = col - curPlayer.getX();
			int yDist = row - curPlayer.getY();
			double totalDist = Math.sqrt(xDist * xDist + yDist * yDist);
			// may have a monster with melee reach of greater than 1 at some
			// point
			double range = curPlayer.getMelee().getRange();
			if (totalDist <= range) {
				if (hasCover(col, row, xDist, yDist)) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean curPlayerCanRanged(int col, int row) {
		if (curPlayer.getActionPoints() == 0) {
			return false;
		}
		if (terrain[col][row].isOccupied()) {
			int xDist = col - curPlayer.getX();
			int yDist = row - curPlayer.getY();
			double totalDist = Math.sqrt(xDist * xDist + yDist * yDist);
			if (totalDist <= curPlayer.getRanged().getRange()) {
				if (hasCover(col, row, xDist, yDist)) {
					return false;
				} else {
					return true;
				}
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

	public Player getPlayerAt(int col, int row) {
		if (terrain[col][row].isOccupied()) {
			return terrain[col][row].getResident();
		} else {
			return null;
		}
	}

	public Terrain getSquare(int col, int row) {
		return terrain[col][row];
	}

	public void move(Player movingPlayer, int col, int row) {
		movingPlayer.move(col, row);
		movingPlayer.useActionPoints(1);
		updateMoves(movingPlayer);
	}

	/**
	 * has current player attack specified defender
	 * @param defender currently selected player
	 * @param type attack type as specified in Attack class
	 * @return returns info on if attack succeeded. 0 = death, 1 = damaged, 2 =
	 *         blocked, 3 = dodgeed
	 */
	
	public int curPlayerAttack(Player defender, AttackType type) {
		Attack attack = null;
		switch(type) {
		case MELEE:
			attack = curPlayer.getMelee();
			break;
		case RANGED:
			attack = curPlayer.getRanged();
			break;
		case SPECIAL:
			//TODO come up with good framework for special attacks
			break;
		}
		curPlayer.useActionPoints(1);
		//check speed vs agility
		int speed = attack.getSpeedValue() + addDTwenty();
		int agility = defender.getAgilityRating().get() + addDTwenty();
		if(agility > speed){
			return 3;
		}
		//check power vs armor
		int power = attack.getPowerValue() + addDTwenty();
		int armor = defender.getArmorRating().get() + addDTwenty();
		int armorDamage = (int)(power*0.2);
		if(armor > power){
			//subtract a bit of armor
			if(armorDamage < defender.getArmorRating().get()){
				defender.takeArmorDamage(armorDamage);
			} else {
				defender.getArmorRating().set(0);
			}
			return 2;
		}
		//attack succeeded
		//subtract a bit of armor
		if(armorDamage < defender.getArmorRating().get()){
			defender.takeArmorDamage(armorDamage);
		} else {
			defender.getArmorRating().set(0);
		}
		//provide damage
		int damage = attack.getDamageProperty().get();
		if(damage < defender.getHealth().get()){
			defender.takeDamage(damage);
			return 1;
		}else{
			damage = defender.getHealth().get();
			defender.takeDamage(damage);
			if(!enemies.remove(defender)){
				if(!allies.remove(defender)){
					System.err.println("COULD NOT FIND DEFENDER IN LISTS OF PLAYERS");
				}
			}
			if(!playerOrder.remove(defender)){
				System.err.println("COULD NOT FIND DEFENDER IN PLAYER ORDER");
			}
			terrain[defender.getX()][defender.getY()].leave();
			updateMoves(curPlayer);
			return 0;
		}
	}
	
	/**
	 * provides psuedorandom d20 dice roll
	 * @return value between 1-20 inclusive
	 */
	private int addDTwenty(){
		return rand.nextInt(21)+1;
	}
	
	public boolean isBadGuysTurn(){
		return !alliesTurn;
	}

	public int getNumCols() {
		return numCols;
	}
	
	public int getNumRows() {
		return numRows;
	}
	
	public Terrain[][] getTerrain() {
		return terrain;
	}

	public boolean isGoodGuy(Player p) {
		return allies.contains(p);
	}

	public int checkForEndGame() {
		if(allies.size() == 0){
			return 2;
		}else if(enemies.size() == 0){
			return 1;
		}else{
			return 0;
		}
		
	}
}
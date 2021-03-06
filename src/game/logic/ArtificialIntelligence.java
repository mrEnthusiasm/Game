package game.logic;

import game.player.Player;
import game.ui.Battleground;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Class describing computer controlled players. Should interact with game state
 * through battleground, not by directly affecting the game logic of the battle
 * object. UI will behave more naturally rather than having to manually force
 * updates. Currently a very rudimentary AI system with no ranged action
 * implemented.
 * 
 * @author Daniel Schmidt
 *
 */
public class ArtificialIntelligence {
	Battle battle;
	Battleground battleground;
	Player curPlayer;
	Terrain[][] terrain;

	public ArtificialIntelligence(Battle battle, Battleground battleground) {
		this.battle = battle;
		this.terrain = battle.getTerrain();
		this.battleground = battleground;
	}

	/**
	 * called by battleground class if it is a bad guys turn. Step 1: If next to
	 * a good guy, attack them. Step 2: If not next to a good guy, move towards
	 * closest good guy.
	 */
	public void performAction() {
		curPlayer = battle.getCurPlayer();
		if (attackNearby())
			;
		else if (moveTowardsClosestEnemy())
			;
		else
			System.err.println("NO AI ACTION PERFORMED");
	}

	/**
	 * Uses Dijkstra's algorithm to find path to closest good guy. Than works
	 * backwards from good guy until distance to sqaure is less than or equal to
	 * bad guy's maximum move distance.
	 * 
	 * @return true if found an enemy to move towards, otherwise false
	 */
	private boolean moveTowardsClosestEnemy() {
		// find closest enemy - move towards them
		Vertex[][] graph = new Vertex[battle.getNumCols()][battle.getNumRows()];
		PriorityQueue<Vertex> unvisited = new PriorityQueue<Vertex>();
		Vertex origin = new Vertex(curPlayer.getXValue(), curPlayer.getYValue());
		origin.setDist(0);
		graph[origin.getX()][origin.getY()] = origin;
		unvisited.add(origin);
		Vertex cur;
		// need to break when find an enemy
		while (!unvisited.isEmpty()) {
			cur = unvisited.poll();
			for (int i = cur.getX() - 1; i < cur.getX() + 2; i++) {
				for (int j = cur.getY() - 1; j < cur.getY() + 2; j++) {
					if (i < 0 || j < 0)
						continue; // beyond the map
					if (i >= battle.getNumCols() || j >= battle.getNumRows())
						continue; // beyond the map
					if (i == cur.getX() && j == cur.getY())
						continue; // center of 3x3 ie: cur
					Terrain t = terrain[i][j];
					if (t.isOccupied()) {
						if (battle.isGoodGuy(t.getResident())
								&& (cur != origin)) {
							// follow path back to max move distance
							Vertex v = moveOnPath(cur);
							battleground.AISelectPane(v.getX(), v.getY());
							battleground.AIMove();
							return true;
						} else {
							// square occupied by ally
							continue;
						}
					}
					if (!t.isTraversable()) {
						continue;
					}
					Vertex tmp = graph[i][j];
					if (tmp == null) {
						tmp = new Vertex(i, j);
						cur.getNeighbors().add(tmp);
						// may add some vertices we later remove because of
						// area
						// of control but their minDist from cur will be
						// positive infinity so it doesn't matter
						unvisited.add(tmp);
						graph[i][j] = tmp;
					} else {
						cur.getNeighbors().add(tmp);
					}
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
		return false;
	}

	/**
	 * helper function for moveTowardsClosestEnemy to find vertex with move distance
	 * less than maxMoveDistance
	 * @param v starting vertex
	 * @return vertex with a move distance less than or equal to maxMoveDistance
	 *         of bad guy
	 */
	private Vertex moveOnPath(Vertex v) {
		Vertex cur = v;
		while (cur.getDist() >= curPlayer.getMaxMoveDistance()) {
			cur = cur.getPrevious();
		}
		return cur;
	}

	/**
	 * Checks squares around bad guy for a good guy. Attacks first good guy found.
	 * @return true if good guy attacked, otherwise false
	 */
	private boolean attackNearby() {
		for (int i = curPlayer.getXValue() - 1; i < curPlayer.getXValue() + 2; i++) {
			for (int j = curPlayer.getYValue() - 1; j < curPlayer.getYValue() + 2; j++) {
				if (i < 0 || j < 0)
					continue; // beyond the map
				if (i >= battle.getNumCols() || j >= battle.getNumRows())
					continue; // beyond the map
				if (i == curPlayer.getXValue() && j == curPlayer.getYValue())
					continue; // center of 3x3 ie: curPlayer location
				Player p = battle.getPlayerAt(i, j);
				if (p == null) {
					continue;
				} else {
					if (battle.isGoodGuy(p)) {
						battleground.AISelectPane(p.getXValue(), p.getYValue());
						battleground.AIMelee(p);
						return true;
					}
				}
			}
		}
		return false;
	}

}
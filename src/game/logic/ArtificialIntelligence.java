package game.logic;

import game.player.Player;
import game.view.Battleground;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Class describing computer controlled players. Should interact with game state
 * through battleground, not by directly affecting the game logic of battle
 * object. UI will behave more naturally rather than having to manually force
 * updates.
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

	public int run() {
		curPlayer = battle.getCurPlayer();
		while (curPlayer.getActionPoints() != 0) {
			if (attackNearby()) {
				continue;
			} else {
				if (moveTowardsClosestEnemy() == 0) {
					return 0;
				}
			}
		}
		return 1;
	}

	private int moveTowardsClosestEnemy() {
		// find closest enemy - move towards them
		Vertex[][] graph = new Vertex[battle.getNumCols()][battle.getNumRows()];
		PriorityQueue<Vertex> unvisited = new PriorityQueue<Vertex>();
		Vertex origin = new Vertex(curPlayer.getX(), curPlayer.getY());
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
							battleground.moveAction();
							return 1;
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
		return 0;
	}

	private Vertex moveOnPath(Vertex v) {
		Vertex cur = v;
		while (cur.getDist() > curPlayer.getMaxMoveDistance()) {
			cur = cur.getPrevious();
		}
		return cur;
	}

	private boolean attackNearby() {
		for (int i = curPlayer.getX() - 1; i < curPlayer.getX() + 2; i++) {
			for (int j = curPlayer.getY() - 1; j < curPlayer.getY() + 2; j++) {
				if (i < 0 || j < 0)
					continue; // beyond the map
				if (i >= battle.getNumCols() || j >= battle.getNumRows())
					continue; // beyond the map
				if (i == curPlayer.getX() && j == curPlayer.getY())
					continue; // center of 3x3 ie: cur
				Player p = battle.getPlayerAt(i, j);
				if (p == null) {
					continue;
				} else {
					if (battle.isGoodGuy(p)) {
						battleground.meleeAction(p);
						return true;
					}
				}
			}
		}
		return false;
	}

}
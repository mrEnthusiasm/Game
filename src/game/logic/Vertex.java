package game.logic;

import java.util.ArrayList;

/**
 * Used by any method using Dijkstra's algorithm as the nodes of the graph it creates
 * @author Daniel Schmidt
 *
 */
public class Vertex implements Comparable<Vertex> {
	private float minDist;
	private int x;
	private int y;
	private ArrayList<Vertex> neighbors;
	private Vertex previous;

	public Vertex(int x, int y) {
		minDist = Float.MAX_VALUE;
		this.setX(x);
		this.setY(y);
		this.neighbors = new ArrayList<Vertex>();
	}

	public Float getDist() {
		return minDist;
	}

	public void setDist(float dist) {
		this.minDist = dist;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setPrevious(Vertex v) {
		previous = v;
	}

	public Vertex getPrevious() {
		return previous;
	}

	@Override
	public int compareTo(Vertex otherVertex) {
		return this.getDist().compareTo(otherVertex.getDist());
	}

	public ArrayList<Vertex> getNeighbors() {
		return neighbors;
	}
}
package game.battles;

import game.logic.GrassTerrain;
import game.logic.Terrain;

/**
 * Defines terrain layout for a level
 * @author Daniel Schmidt
 *
 */
public class Map {
	private Terrain[][] terrain;
	private int columns;
	private int rows;
	public Map(int c, int r){
		this.columns = c;
		this.rows = r;
		terrain = new Terrain[columns][rows];
		Terrain temp;
		//defaults to an empty GrassTerrain field
		addTerrain(0, rows, 0, columns, new GrassTerrain());
	}
	public int getNumberColumns(){
		return columns;
	}
	public int getNumberRows(){
		return rows;
	}
	public Terrain[][] getTerrain(){
		return terrain;
	}
	public Terrain getTerrainAt(int col, int row){
		return terrain[col][row];
	}
	/**
	 * Convenience method for setting the terrain for square sections of a map
	 * @param rowStart top of square, inclusive
	 * @param rowEnd bottom of square, exclusive
	 * @param colStart left side of square, inclusive
	 * @param colEnd  right side of square, exclusive
	 * @param t terrain type to add to map
	 */
	public void addTerrain(int rowStart, int rowEnd, int colStart, int colEnd, Terrain t){
		for(int i=colStart; i<colEnd; i++){
			for(int j=rowStart; j<rowEnd; j++){
				terrain[i][j] = t;
			}
		}
	}
}

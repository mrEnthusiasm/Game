package game.battles;

import game.logic.GrassTerrain;
import game.logic.Terrain;

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
		for(int i=0; i<columns; i++){
			for(int j=0; j<rows; j++){
				temp = new GrassTerrain();
				terrain[i][j] = temp;
			}
		}
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
	public void addTerrain(int rowStart, int rowEnd, int colStart, int colEnd, Terrain t){
		for(int i=colStart; i<colEnd; i++){
			for(int j=rowStart; j<rowEnd; j++){
				terrain[i][j] = t;
			}
		}
	}
}

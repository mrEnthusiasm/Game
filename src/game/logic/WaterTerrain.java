package game.logic;


public class WaterTerrain extends Terrain{
	public WaterTerrain(){
		css = "water";
		isTraversable = false;
		providesCover = false;
	}
	
	@Override
	public String toString(){
		return "Water";
	}

}

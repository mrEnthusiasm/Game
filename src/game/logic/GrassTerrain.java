package game.logic;


public class GrassTerrain extends Terrain{
	public GrassTerrain(){
		css = "grass";
		
		isTraversable = true;
		providesCover = false;
	}
	
	@Override
	public String toString(){
		return "Grass";
	}
}

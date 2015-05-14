package game.logic;

public class TreeTerrain extends Terrain{
	public TreeTerrain(){
		css = "tree";
		
		isTraversable = false;
		providesCover = true;
	}
	
	@Override
	public String toString(){
		return "Tree";
	}
}

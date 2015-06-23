package game.logic;

import game.player.Player;

/**
 * Base class for all other Terrain classes.
 * @author Daniel Schmidt
 *
 */
public abstract class Terrain {
	private boolean occupied = false;
	private Player resident;
	protected boolean isTraversable;
	protected boolean providesCover;
	protected String css;
	
	public boolean isOccupied(){
		return occupied;
	}
	
	public void occupy(Player p){
		if(isTraversable()){
			this.occupied = true;
			resident = p;
		}else{
			System.err.println("TRIED TO OCCUPY NON TRAVERSABLE TERRAIN");
		}
	}
	
	public void leave(){
		if(isOccupied()){
			resident = null;
			this.occupied = false;
		}else if(!isTraversable()){
			System.err.println("TRIED TO LEAVE NON TRAVERSABLE");
		}else{
			System.err.println("TRIED TO LEAVE NON OCCUPIED TERRAIN");
		}
	}
	
	public Player getResident(){
		return resident;
	}
	
	public boolean isTraversable(){
		return isTraversable;
	}
	
	public boolean providesCover(){
		return providesCover;
	}
	
	public String getCss(){
		if(css == null){
			System.err.println("NO CSS SET");
			return "";
		}else{
			return css;
		}
	}
}

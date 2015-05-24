package game.player;

public abstract class PlayerStatus {
	protected double agilityModifier = 1.0;
	protected double movementModifier = 1.0;
	protected int roundsEffective;
	protected int turnsLeft;
	
	public double getAdjustment(StatType st){
		switch(st) {
		case AGILITY:
			return agilityModifier;
		case MOVEMENT:
			return movementModifier;
		default:
			System.err.println("UNKNOWN STAT TYPE");
			return -1.0;
		}
	}
	
	public void setTurnsLeft(int numPlayers){
		turnsLeft = numPlayers * roundsEffective;
	}
	
	public void decrementTurnsLeft(){
		turnsLeft--;
	}
	
	public boolean isExpired() {
		if(turnsLeft > 0){
			return false;
		}else{
			return true;
		}
	}
}

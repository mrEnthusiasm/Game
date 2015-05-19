package game.battles;

import java.util.ArrayList;
import java.util.List;

import game.BattleSelectionPage;
import game.player.Player;
import game.view.Battleground;

public abstract class Level {
	BattleSelectionPage battlefieldPage;
	Battleground battleground;
	
	List<Player> allies = new ArrayList<Player>();
	List<Player> enemies = new ArrayList<Player>();
	
	Map myMap;
	
	public Level(BattleSelectionPage battlefieldPage) {
		this.battlefieldPage = battlefieldPage;
		retry();
		
	}
	
	
	public abstract void addPlayers();
	
	public abstract void createMap();
	
	public void retry() {
		allies = new ArrayList<Player>();
		enemies = new ArrayList<Player>();
		addPlayers();
		createMap();
		battleground = new Battleground(this, myMap, allies, enemies);
		battlefieldPage.primaryStage.getScene().setRoot(battleground);
	}
	
	public Battleground getBattleground(){
		if(battleground == null){
			System.err.println("BATTLEGROUND UNDEFINED");
			return null;
		}else{
			return battleground;
		}
	}
	
	public void done() {
		battlefieldPage.closeLevel();
	}
}

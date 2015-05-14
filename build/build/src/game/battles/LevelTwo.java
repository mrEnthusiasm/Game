package game.battles;

import game.BattleFieldPage;
import game.logic.WaterTerrain;
import game.player.Beowolf;
import game.player.Ruby;

public class LevelTwo extends Level{

	public LevelTwo(BattleFieldPage battlefieldPage) {
		super(battlefieldPage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addPlayers() {
		allies.add(new Ruby(5, 0));
		enemies.add(new Beowolf(5, 14));
		enemies.add(new Beowolf(7, 14));
		enemies.add(new Beowolf(9, 14));
		
		
	}

	@Override
	public void createMap() {
		myMap = new Map(15, 15);
		myMap.addTerrain(6, 10, 3, 7, new WaterTerrain());
		myMap.addTerrain(7, 9, 7, 8, new WaterTerrain());
		myMap.addTerrain(7, 9, 2, 3, new WaterTerrain());
		
	}

}

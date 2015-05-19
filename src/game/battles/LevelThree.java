package game.battles;

import java.util.Random;

import game.BattleSelectionPage;
import game.logic.TreeTerrain;
import game.player.Beowolf;
import game.player.Ruby;

public class LevelThree extends Level{

	public LevelThree(BattleSelectionPage battlefieldPage) {
		super(battlefieldPage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addPlayers() {
		allies.add(new Ruby(2, 0));
		enemies.add(new Beowolf(19, 19));
		enemies.add(new Beowolf(17, 19));
		enemies.add(new Beowolf(19, 17));
		enemies.add(new Beowolf(17, 17));
		
	}

	@Override
	public void createMap() {
		myMap = new Map(20, 20);
		Random rand = new Random();
		int random;
		for(int i=3; i<17; i++){
			for(int j=5; j<14; j++){
				random = rand.nextInt(3);
				if(random == 0){
					myMap.addTerrain(j, j+1, i, i+1, new TreeTerrain());
				}
			}
		}
		
	}

}

package game.battles;

import game.BattleFieldPage;
import game.player.Beowolf;
import game.player.Ruby;

public class LevelOne extends Level{

	public LevelOne(BattleFieldPage battlefieldPage) {
		super(battlefieldPage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addPlayers() {
		allies.add(new Ruby(5, 0));
		enemies.add(new Beowolf(7, 9));
		enemies.add(new Beowolf(3, 9));
	}

	@Override
	public void createMap() {
		myMap = new Map(10, 10);
	}
}

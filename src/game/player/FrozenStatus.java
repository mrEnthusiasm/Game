package game.player;

public class FrozenStatus extends PlayerStatus{
	public FrozenStatus () {
		agilityModifier = 0.33;
		movementModifier = 0.33;
		roundsEffective = 1;
	}
}

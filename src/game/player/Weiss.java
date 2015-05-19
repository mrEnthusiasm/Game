package game.player;

import game.logic.Attack;
import game.logic.Attack.AttackType;

public class Weiss extends Player{
	public Weiss(int x, int y){
		name = "Weiss";
		description = "She may have ice in her veins, but that can come in handy on the battlefield. "
				+ "Long range shots from her weapon Myrtenaster freeze enemies reducing agility and mobility";
		
		maxHealth = 100;
		health.set(maxHealth);
		armorRating.set(10);
		agilityRating.set(9);
		
		this.x.set(x);
		this.y.set(y);
		
		actionPoints.setValue(2);
		
		maxMoveDist = 4;
		
		int speed = 9;
		int power = 10;
		// need a range > sqrt(2) so that diagonal attacks possible but no need
		// to display that to end user (shhh don't tell them)
		double maxRange = 1.5; 
		String name = "Rapier";
		int damage = 15;
		Attack punch = new Attack(AttackType.MELEE, speed, power, maxRange,
				name, damage);
		this.meleeAttack = punch;
		
		speed = 19;
		power = 10;
		maxRange = 15;
		name = "Ice Shot";
		damage = 13;
		Attack iceShot = new Attack(AttackType.RANGED, speed, power, maxRange,
				name, damage);
		this.rangedAttack = iceShot;
	}
}

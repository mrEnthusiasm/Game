package game.player;

import game.logic.Attack;
import game.logic.Attack.AttackType;

public class Blake extends Player{
	public Blake(int x, int y){
		sprite = "resources/images/b-test-sprite.png";
		infoPic = "resources/images/info-pic-blake.jpg";
		name = "Blake";
		description = "A shadow in the night, her semblemce allows her an extra "
				+ "chance to dodge attacks if there are open spaces next to her.";
		
		maxHealth = 100;
		health.set(maxHealth);
		armorRating.set(15);
		agilityRating.set(25);
		
		this.x.set(x);
		this.y.set(y);
		
		actionPoints.setValue(2);
		
		maxMoveDist = 5;
		
		int speed = 12;
		int power = 13;
		// need a range > sqrt(2) so that diagonal attacks possible but no need
		// to display that to end user (shhh don't tell them)
		double maxRange = 1.5; 
		String name = "Sword";
		int damage = 13;
		Attack punch = new Attack(AttackType.MELEE, speed, power, maxRange,
				name, damage);
		this.meleeAttack = punch;
		
		speed = 25;
		power = 8;
		maxRange = 10;
		name = "Pistol";
		damage = 10;
		Attack iceShot = new Attack(AttackType.RANGED, speed, power, maxRange,
				name, damage);
		this.rangedAttack = iceShot;
	}
}

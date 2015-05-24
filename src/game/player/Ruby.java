package game.player;

import game.logic.Attack;
import game.logic.Attack.AttackType;

public class Ruby extends Player {
	public Ruby(int x, int y) {
		sprite = "resources/images/ruby-sprite.png";
		infoPic = "resources/images/info-pic-ruby.png";
		name = "Ruby";
		this.description = "Blink and you'll miss her, Ruby's semblence allows her "
				+ "to move so fast she ignores enemy's area of control";

		maxHealth = 100;
		health.set(maxHealth);
		armorRating.set(20);
		agilityRating.set(10);

		this.x.set(x);
		this.y.set(y);

		actionPoints.setValue(2);

		maxMoveDist.setValue(6);
		int speed = 10;
		int power = 15;
		// need a range > sqrt(2) so that diagonal attacks possible but no need
		// to display that to end user (shhh don't tell them)
		double maxRange = 1.5; 
		String name = "Scythe";
		int damage = 30;
		Attack scythe = new Attack(AttackType.MELEE, speed, power, maxRange,
				name, damage);
		this.meleeAttack = scythe;

		speed = 20;
		power = 8;
		maxRange = 18;
		name = "Sniper Rifle";
		damage = 15;
		Attack sniper = new Attack(AttackType.RANGED, speed, power, maxRange,
				name, damage);
		this.rangedAttack = sniper;
	}
}

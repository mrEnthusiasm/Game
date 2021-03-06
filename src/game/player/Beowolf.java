package game.player;

import game.logic.Attack;
import game.logic.Attack.AttackType;

public class Beowolf extends Player {
	public Beowolf(int x, int y) {
		sprite = "resources/images/grimm-sprite.png";
		infoPic = "resources/images/info-pic-beowolf.png";
		name = "Beowolf";
		description = "simple enough to deal with on there own, Beowolves become a real "
				+ "problem when encountered in large packs";

		this.x.set(x);
		this.y.set(y);

		maxHealth = 50;
		health.set(maxHealth);
		armorRating.set(10);
		agilityRating.set(1);

		actionPoints.setValue(2);
		maxMoveDist.setValue(4);

		int speed = 13;
		int power = 10;
		// need a range > sqrt(2) so that diagonal attacks possible but no need
		// to display that to end user (shhh don't tell them)
		double maxRange = 1.5;
		String name = "Claws";
		int damage = 20;
		Attack claws = new Attack(AttackType.MELEE, speed, power, maxRange,
				name, damage);
		this.meleeAttack = claws;
	}
}

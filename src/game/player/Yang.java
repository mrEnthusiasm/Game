package game.player;

import javafx.beans.property.SimpleIntegerProperty;
import game.logic.Attack;
import game.logic.Attack.AttackType;

public class Yang extends Player{
	SimpleIntegerProperty meleeDamage;
	public Yang(int x, int y){
		sprite = "resources/images/yang-sprite.png";
		//TODO get yang info pic
		infoPic = "";
		name = "Weiss";
		name = "Yang";
		description = "The only thing more painful than her puns are her punches. "
				+ "Yang's melee attack increases in damage for every hit she takes";
		
		maxHealth = 100;
		health.set(maxHealth);
		armorRating.set(25);
		agilityRating.set(8);
		
		this.x.set(x);
		this.y.set(y);
		
		actionPoints.setValue(2);
		
		maxMoveDist.setValue(4);
		
		int speed = 8;
		int power = 17;
		// need a range > sqrt(2) so that diagonal attacks possible but no need
		// to display that to end user (shhh don't tell them)
		double maxRange = 1.5; 
		String name = "Guantlet Punch";
		meleeDamage = new SimpleIntegerProperty();
		meleeDamage.set(25);
		Attack punch = new Attack(AttackType.MELEE, speed, power, maxRange,
				name, meleeDamage);
		this.meleeAttack = punch;
		
		speed = 17;
		power = 12;
		maxRange = 13;
		name = "Exploding Shotgun";
		SimpleIntegerProperty rangedDamage = new SimpleIntegerProperty();
		rangedDamage.set(12);
		Attack shotgun = new Attack(AttackType.RANGED, speed, power, maxRange,
				name, rangedDamage);
		this.rangedAttack = shotgun;
	}
	@Override
	public void takeDamage(int damage) {
		health.set(health.get() - damage);
		
	}
	@Override
	public void takeArmorDamage(int armorDamage) {
		meleeDamage.add(armorDamage);
		armorRating.set(armorRating.get() - armorDamage);
	}
}

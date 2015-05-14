package game.logic;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Attack {
	private AttackType type;
	private SimpleIntegerProperty speed = new SimpleIntegerProperty();
	private SimpleIntegerProperty power = new SimpleIntegerProperty();
	private SimpleIntegerProperty damage = new SimpleIntegerProperty();
	private double maxRange = 0;
	private String name = "";
	private String description = "";

	public enum AttackType {
		MELEE, RANGED, SPECIAL;
		@Override
		public String toString() {
			switch (this) {
			case MELEE:
				return "melee";
			case RANGED:
				return "ranged";
			case SPECIAL:
				return "special";
			default:
				return "";
			}
		}
	}

	public Attack(AttackType type, int speed, int power, double maxRange,
			String name, int damage) {
		this.type = type;
		this.speed.set(speed);
		this.power.set(power);
		this.maxRange = maxRange;
		this.name = name;
		this.damage.set(damage);
	}

	public AttackType getType() {
		return type;
	}

	public SimpleIntegerProperty getSpeedProperty() {
		return speed;
	}

	public SimpleIntegerProperty getPowerPorperty() {
		return power;
	}

	public int getSpeedValue() {
		return speed.get();
	}

	public int getPowerValue() {
		return power.get();
	}

	public double getRange() {
		return maxRange;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public SimpleIntegerProperty getDamageProperty() {
		return damage;
	}
}

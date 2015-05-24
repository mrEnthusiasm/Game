package game.player;

import java.util.ArrayList;

import game.logic.Attack;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.ImageView;

public abstract class Player {
	protected String sprite;
	protected String infoPic;
	protected String name;
	protected String description;
	protected ImageView playerImage;

	protected SimpleDoubleProperty x = new SimpleDoubleProperty();
	protected SimpleDoubleProperty y = new SimpleDoubleProperty();

	protected SimpleIntegerProperty health = new SimpleIntegerProperty();
	protected int maxHealth;
	protected SimpleIntegerProperty armorRating = new SimpleIntegerProperty();
	protected SimpleIntegerProperty agilityRating = new SimpleIntegerProperty();

	public SimpleIntegerProperty actionPoints = new SimpleIntegerProperty();
	protected SimpleIntegerProperty maxMoveDist = new SimpleIntegerProperty();
	protected boolean canMove = true;
	protected ArrayList<Attack> specials = new ArrayList<Attack>();
	protected Attack rangedAttack = null;
	protected Attack meleeAttack = null;

	private double agilityModifier = 1.0;
	private double movementModifier = 1.0;

	protected ArrayList<StatusType> playerStatus = new ArrayList<StatusType>();
	protected StatusType rangedStatusEffect;
	protected StatusType meleeStatusEffect;
	

	public SimpleDoubleProperty getXProperty() {
		return x;
	}

	public SimpleDoubleProperty getYProperty() {
		return y;
	}

	/**
	 * @return int value of x location property
	 */
	public int getXValue() {
		return x.getValue().intValue();
	}

	/**
	 * @return int value of y location property
	 */
	public int getYValue() {
		return y.getValue().intValue();
	}

	public float getMaxMoveDistance() {
		return maxMoveDist.getValue();
	}

	public void setMaxMoveDistance() {

	}

	public String getSpriteLocation() {
		return sprite;
	}

	public String getInfoPicLocation() {
		return infoPic;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<Attack> getSpecials() {
		return specials;
	}

	public Attack getMelee() {
		return meleeAttack;
	}

	public Attack getRanged() {
		return rangedAttack;
	}

	public SimpleIntegerProperty getHealth() {
		return health;
	}

	public SimpleIntegerProperty getArmorRating() {
		return armorRating;
	}

	public SimpleIntegerProperty getAgilityRating() {
		return agilityRating;
	}

	public void move(int curSelectedCol, int curSelectedRow) {
		this.x.set(curSelectedCol);
		this.y.set(curSelectedRow);
	}

	public boolean canMove() {
		if (actionPoints.get() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void useActionPoints(int i) {
		actionPoints.set(actionPoints.get() - i);
	}

	public void resetActionPoints() {
		actionPoints.set(2);
	}

	public int getActionPoints() {
		return actionPoints.get();
	}

	public void takeDamage(int damage) {
		health.set(health.get() - damage);

	}

	public void takeArmorDamage(int armorDamage) {
		armorRating.set(armorRating.get() - armorDamage);
	}

	public void setPlayerImage(ImageView image) {
		this.playerImage = image;
	}

	public ImageView getPlayerImage() {
		return playerImage;
	}

	protected void bindModifiers() {
		agilityRating.multiply(agilityModifier);
		maxMoveDist.multiply(movementModifier);
	}

	private double statusAdjustment(StatType stat) {
		double adjustment = 1.0;
		for (StatusType status : playerStatus) {
			adjustment *= status.getAdjustment(stat);
		}
		return adjustment;
	}
	
	private void updateStatus() {
		double adjustment;
		for (StatType stat : StatType.values()) {
			adjustment = statusAdjustment(stat);
			if(adjustment == -1.0){
				System.err.println("BAD ADJUSTMENT VALUE");
				return;
			}
			switch (stat) {
			case AGILITY:
				agilityModifier = adjustment;
				break;
			case MOVEMENT:
				movementModifier = adjustment;
				break;
			}
		}
	}
	
	public StatusType getRangedStatusEffect() {
		return rangedStatusEffect;
	}
	
	public StatusType getMeleeStatusEffect() {
		return meleeStatusEffect;
	}

	public void setFrozen() {
		playerStatus.add(StatusType.FROZEN);
		System.out.println("freezing");
		updateStatus();
		
	}
	
	public void removeFrozen() {
		if(!playerStatus.remove(StatusType.FROZEN)){
			System.err.println("FROZEN STATUS NOT FOUND, COULD NOT REMOVE");
			return;
		}
		updateStatus();
	}
}

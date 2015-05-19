package game.player;

import java.util.ArrayList;

import game.logic.Attack;
import javafx.beans.property.SimpleIntegerProperty;

public abstract class Player {
	protected String sprite;
	protected String infoPic;
	protected String name;
	protected String description;
	
	protected SimpleIntegerProperty x = new SimpleIntegerProperty();
	protected SimpleIntegerProperty y = new SimpleIntegerProperty();
	
	protected SimpleIntegerProperty health = new SimpleIntegerProperty();
	protected int maxHealth;
	protected SimpleIntegerProperty armorRating = new SimpleIntegerProperty();
	protected SimpleIntegerProperty agilityRating = new SimpleIntegerProperty();
	
	public SimpleIntegerProperty actionPoints = new SimpleIntegerProperty();
	protected float maxMoveDist;
	protected boolean canMove=true;;
	protected ArrayList<Attack> specials = new ArrayList<Attack>();
	protected Attack rangedAttack = null;
	protected Attack meleeAttack = null;
	
	public SimpleIntegerProperty getXProperty() {
		return x;
	}
	public SimpleIntegerProperty getYProperty() {
		return y;
	}
	public int getXValue() {
		return x.get();
	}
	public int getYValue() {
		return y.get();
	}
	public float getMaxMoveDistance() {
		return maxMoveDist;
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
	public ArrayList<Attack> getSpecials(){
		return specials;
	}
	public Attack getMelee(){
		return meleeAttack;
	}
	public Attack getRanged(){
		return rangedAttack;
	}
	public SimpleIntegerProperty getHealth(){
		return health;
	}
	public SimpleIntegerProperty getArmorRating(){
		return armorRating;
	}
	public SimpleIntegerProperty getAgilityRating(){
		return agilityRating;
	}
	public void move(int curSelectedCol, int curSelectedRow) {
		this.x.set(curSelectedCol);
		this.y.set(curSelectedRow);
	}
	public boolean canMove() {
		if(actionPoints.get() > 0){
			return true;
		}else{
			return false;
		}
	}
	public void useActionPoints(int i) {
		actionPoints.set(actionPoints.get()-i);	
	}
	public void resetActionPoints() {
		actionPoints.set(2);
	}
	public int getActionPoints(){
		return actionPoints.get();
	}
	public void takeDamage(int damage) {
		health.set(health.get() - damage);
		
	}
	public void takeArmorDamage(int armorDamage) {
		armorRating.set(armorRating.get() - armorDamage);
	}
}

package game.view;

import game.logic.Attack;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AttackInfoPane extends TextFlow{
	Text name = new Text();
	Text type = new Text();
	Text speed = new Text();
	Text power = new Text();
	Text range = new Text();
	Text damage = new Text();
	
	DoubleProperty paneHeight = new SimpleDoubleProperty();
	public AttackInfoPane(Attack attack){
		//top, right, bottom, left
		setPadding(new Insets(3.0, 3.0, 3.0, 3.0));
		
		name.textProperty().set(attack.getName()+"  ");
		name.setFont(new Font(20.0));
		type.textProperty().set(attack.getType().toString()+"\n");
		type.setFont(new Font(15.0));
		damage.textProperty().bind(Bindings.format("%d dmg\t", attack.getDamageProperty()));
		damage.setFont(new Font(15.0));
		range.textProperty().set("max range "+ (int)attack.getRange()+"\n");
		range.setFont(new Font(15.0));
		speed.textProperty().bind(Bindings.format("speed %d\t", attack.getSpeedProperty()));
		speed.setFont(new Font(15.0));
		power.textProperty().bind(Bindings.format("power %d ", attack.getPowerPorperty()));
		power.setFont(new Font(15.0));
		
		
		this.getChildren().addAll(name, type, damage, range, speed, power);
		
		/*
		name.setTextOrigin(VPos.TOP);
		name.setFont(new Font(20));
		name.wrappingWidthProperty().bind(this.widthProperty().divide(2.0));
		
		type.setTextOrigin(VPos.BASELINE);
		type.setFont(Font.font("Verdana", FontPosture.ITALIC, 15.0));
		type.xProperty().bind(name.wrappingWidthProperty());
		type.yProperty().bind(name.yProperty().add(name.getFont().getSize()));
		
		damage.setTextOrigin(VPos.TOP);
		damage.setFont(new Font(15));
		damage.yProperty().bind(name.yProperty().add(name.getFont().getSize()));
		
		speed.setTextOrigin(VPos.TOP);
		speed.setFont(new Font(14));
		speed.yProperty().bind(damage.yProperty().add(
				damage.getFont().getSize()+padding));
		speed.wrappingWidthProperty().bind(this.widthProperty().divide(3.0));
		
		power.setTextOrigin(VPos.TOP);
		power.setFont(new Font(14));
		power.xProperty().bind(speed.wrappingWidthProperty());
		power.yProperty().bind(speed.yProperty());
		
		range.setTextOrigin(VPos.TOP);
		range.setFont(new Font(14));
		range.yProperty().bind(speed.yProperty().add(speed.getFont().getSize()));
		*/
		
	}

}

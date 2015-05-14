package game.view;

import game.logic.Attack;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class AttackInfoPane extends Pane{
	Text name = new Text();
	Text type = new Text();
	Text speed = new Text();
	Text power = new Text();
	Text range = new Text();
	Text damage = new Text();
	Text description = new Text();
	
	DoubleProperty paneHeight = new SimpleDoubleProperty();
	public AttackInfoPane(Attack attack){
		//top, right, bottom, left
		setPadding(new Insets(3.0, 3.0, 3.0, 3.0));
		double padding = 3.0;
		
		name.textProperty().set(attack.getName());
		type.textProperty().set(attack.getType().toString());
		speed.textProperty().bind(Bindings.format("speed %d", attack.getSpeedProperty()));
		power.textProperty().bind(Bindings.format("power %d", attack.getPowerPorperty()));
		range.textProperty().set("max range "+ (int)attack.getRange());
		damage.textProperty().bind(Bindings.format("damage %d", attack.getDamageProperty()));
		description.setText(attack.getDescription());
		
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
		
		
		getChildren().addAll(name, type, damage, speed, power, range);
	}

}

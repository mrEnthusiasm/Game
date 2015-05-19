package game.view;

import game.logic.Attack;
import game.logic.Terrain;
import game.player.Player;

import java.util.Iterator;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class InfoPane extends Pane {
	private final ImageView infoPic = new ImageView();
	private Image pic;
	private final Text name = new Text();
	private final Text healthText = new Text();
	private final Text armorRatingText = new Text();
	private final Text agilityRatingText = new Text();
	private final VBox attacks = new VBox();

	private final Text terrainName = new Text();
	private final Text traversable = new Text();
	private final Text cover = new Text();

	private final double interNodePadding = 5.0;

	public InfoPane() {
		// top, right, bottom, left
		setPadding(new Insets(3.0));

		// create children
		getChildren().addAll(infoPic, name, healthText, armorRatingText,
				agilityRatingText, attacks, terrainName, traversable, cover);

		// set player children properties
		infoPic.setTranslateX(this.getPadding().getLeft());
		infoPic.setTranslateY(this.getPadding().getTop());
		infoPic.fitWidthProperty().bind(this.widthProperty().multiply(0.4));
		infoPic.fitHeightProperty().bind(infoPic.fitWidthProperty());

		name.setTextOrigin(VPos.TOP);
		name.xProperty().bind(
				infoPic.xProperty().add(
						infoPic.fitWidthProperty().add(interNodePadding)));
		name.yProperty().bind(infoPic.yProperty());
		name.setFont(new Font(30.0));

		healthText.setTextOrigin(VPos.TOP);
		healthText.xProperty().bind(name.xProperty());
		healthText.yProperty().bind(
				name.yProperty().add(
						name.getFont().getSize() + interNodePadding));
		healthText.setFont(new Font(20.0));

		armorRatingText.setTextOrigin(VPos.TOP);
		armorRatingText.xProperty().bind(name.xProperty());
		armorRatingText.yProperty().bind(
				healthText.yProperty().add(
						healthText.getFont().getSize() + interNodePadding));
		armorRatingText.setFont(new Font(18.0));

		agilityRatingText.setTextOrigin(VPos.TOP);
		agilityRatingText.xProperty().bind(name.xProperty());
		agilityRatingText.yProperty()
				.bind(armorRatingText.yProperty().add(
						armorRatingText.getFont().getSize() + interNodePadding));
		agilityRatingText.setFont(new Font(18.0));

		attacks.setSpacing(3.0);
		attacks.layoutXProperty().bind(
				infoPic.xProperty().add(this.getPadding().getLeft()));
		attacks.layoutYProperty().bind(
				infoPic.yProperty().add(
						infoPic.fitHeightProperty().add(interNodePadding)));
		attacks.prefWidthProperty().bind(this.widthProperty());

		// set terrain properties
		terrainName.setTextOrigin(VPos.TOP);
		terrainName.setTranslateX(this.getPadding().getLeft());
		terrainName.setTranslateY(this.getPadding().getTop());
		terrainName.setFont(new Font(30.0));

		traversable.setTextOrigin(VPos.TOP);
		traversable.xProperty().bind(terrainName.xProperty().add(
				this.getPadding().getLeft()));
		traversable.yProperty().bind(
				terrainName.yProperty().add(
						terrainName.getFont().getSize() + interNodePadding));
		traversable.setFont(new Font(20.0));
		
		cover.setTextOrigin(VPos.TOP);
		cover.xProperty().bind(terrainName.xProperty().add(
				this.getPadding().getLeft()));
		cover.yProperty().bind(
				traversable.yProperty().add(
						traversable.getFont().getSize() + interNodePadding));
		cover.setFont(new Font(20.0));

	}

	public void setPlayer(Player curPlayer) {
		pic = new Image(curPlayer.getInfoPicLocation());
		infoPic.setImage(pic);
		name.setText(curPlayer.getName());
		healthText.textProperty().bind(
				Bindings.format("hp %d", curPlayer.getHealth()));
		armorRatingText.textProperty().bind(
				Bindings.format("armor rating %d", curPlayer.getArmorRating()));
		agilityRatingText.textProperty().bind(
				Bindings.format("agility rating %d",
						curPlayer.getAgilityRating()));

		Attack meleeAttack;
		attacks.getChildren().clear();
		meleeAttack = curPlayer.getMelee();
		if(meleeAttack != null){
			AttackInfoPane meleeInfo = new AttackInfoPane(meleeAttack);
			attacks.getChildren().add(meleeInfo);
		}
		Attack rangedAttack = curPlayer.getRanged();
		if(rangedAttack != null){
			AttackInfoPane rangedInfo = new AttackInfoPane(rangedAttack);
			attacks.getChildren().add(rangedInfo);
		}
		Attack attack;
		Iterator<Attack> specialAttackItr = curPlayer.getSpecials().iterator();
		while (specialAttackItr.hasNext()) {
			attack = specialAttackItr.next();
			AttackInfoPane attackInfo = new AttackInfoPane(attack);
			attacks.getChildren().add(attackInfo);
		}

	}

	public void clear() {
		infoPic.setImage(null);
		name.setText("");
		healthText.textProperty().unbind();
		healthText.setText("");
		armorRatingText.textProperty().unbind();
		armorRatingText.setText("");
		agilityRatingText.textProperty().unbind();
		agilityRatingText.setText("");
		attacks.getChildren().clear();
		
		terrainName.setText("");
		traversable.setText("");
		cover.setText("");
	}

	public void setTerrain(Terrain terrain) {
		terrainName.setText(terrain.toString());
		
		if(terrain.isTraversable()){
			traversable.setText("traversable");
		}else{
			traversable.setText("not traversable");
		}
		
		if(terrain.providesCover()){
			cover.setText("provides cover");
		}else{
			cover.setText("does not provide cover");
		}
	}
}

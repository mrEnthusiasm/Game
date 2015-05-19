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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class InfoPane extends Pane {
	private final ImageView infoPic = new ImageView();
	private Image pic;
	private final ImageView heartPic = new ImageView();
	private Image heart;
	private final ImageView armorPic = new ImageView();
	private Image armor;
	private final Text nameText = new Text();
	private final Text healthText = new Text();
	private final Text armorText = new Text();
	private final Text agilityRatingText = new Text();
	private final Text movementText = new Text();
	private final Text descriptionText = new Text();
	private final TextFlow descriptionTextFlow = new TextFlow();

	private final TextFlow defensiveText = new TextFlow();
	private final VBox attacks = new VBox();

	private final Text terrainName = new Text();
	private final Text traversable = new Text();
	private final Text cover = new Text();

	private final double interNodePadding = 5.0;

	public InfoPane() {
		// top, right, bottom, left
		setPadding(new Insets(3.0));

		// create children
		getChildren().addAll(infoPic, defensiveText, attacks, descriptionTextFlow,
				terrainName, traversable, cover);

		// set player children properties
		infoPic.xProperty().set(this.getPadding().getLeft());
		infoPic.yProperty().set(this.getPadding().getTop());
		infoPic.fitWidthProperty().bind(this.widthProperty().multiply(0.35));
		infoPic.fitHeightProperty().bind(infoPic.fitWidthProperty());

		defensiveText.getChildren().addAll(nameText, heartPic, healthText,
				armorPic, armorText, agilityRatingText, movementText);
		defensiveText.layoutXProperty().bind(
				infoPic.xProperty().add(infoPic.fitWidthProperty().add(10.0)));
		defensiveText.layoutYProperty().bind(infoPic.yProperty());
		defensiveText.setLineSpacing(3.0);

		// TODO going to have to bind this to whatever we find font size to
		heartPic.fitHeightProperty().set(20.0);
		heartPic.setPreserveRatio(true);
		armorPic.fitHeightProperty().set(18.0);
		armorPic.setPreserveRatio(true);

		nameText.setFont(new Font(40.0));
		healthText.setFont(new Font(20.0));
		healthText.setFill(Color.RED);
		armorText.setFont(new Font(20.0));
		armorText.setFill(Color.BLUE);
		agilityRatingText.setFont(new Font(18.0));
		movementText.setFont(new Font(18.0));

		attacks.setSpacing(3.0);
		attacks.layoutXProperty().bind(infoPic.xProperty());
		attacks.layoutYProperty().bind(
				infoPic.yProperty().add(
						infoPic.fitHeightProperty().add(interNodePadding)));
		attacks.prefWidthProperty().bind(this.widthProperty());

		descriptionText.setFont(Font.font("", FontPosture.ITALIC, 12.0));
		descriptionTextFlow.prefWidthProperty().bind(this.widthProperty());
		descriptionTextFlow.layoutXProperty().bind(infoPic.xProperty());
		descriptionTextFlow.layoutYProperty().bind(
				Bindings.add(attacks.layoutYProperty(),
						attacks.heightProperty()));
		descriptionTextFlow.getChildren().add(descriptionText);

		// set terrain properties
		terrainName.setTextOrigin(VPos.TOP);
		terrainName.setTranslateX(this.getPadding().getLeft());
		terrainName.setTranslateY(this.getPadding().getTop());
		terrainName.setFont(new Font(30.0));

		traversable.setTextOrigin(VPos.TOP);
		traversable.xProperty().bind(
				terrainName.xProperty().add(this.getPadding().getLeft()));
		traversable.yProperty().bind(
				terrainName.yProperty().add(
						terrainName.getFont().getSize() + interNodePadding));
		traversable.setFont(new Font(20.0));

		cover.setTextOrigin(VPos.TOP);
		cover.xProperty().bind(
				terrainName.xProperty().add(this.getPadding().getLeft()));
		cover.yProperty().bind(
				traversable.yProperty().add(
						traversable.getFont().getSize() + interNodePadding));
		cover.setFont(new Font(20.0));

	}

	public void setPlayer(Player curPlayer) {
		pic = new Image(curPlayer.getInfoPicLocation());
		infoPic.setImage(pic);
		heart = new Image("resources/images/heart8bit.png");
		heartPic.setImage(heart);
		armor = new Image("resources/images/shield.png");
		armorPic.setImage(armor);
		nameText.setText(curPlayer.getName() + "\n");
		healthText.textProperty().bind(
				Bindings.format(" %d  ", curPlayer.getHealth()));
		armorText.textProperty().bind(
				Bindings.format(" %d\n", curPlayer.getArmorRating()));
		agilityRatingText.textProperty().bind(
				Bindings.format("agility rating %d\n",
						curPlayer.getAgilityRating()));
		movementText.textProperty().bind(
				Bindings.format("move distance %d\n",
						(int) curPlayer.getMaxMoveDistance()));
		descriptionText.setText(curPlayer.getDescription());

		Attack meleeAttack;
		attacks.getChildren().clear();
		meleeAttack = curPlayer.getMelee();
		if (meleeAttack != null) {
			AttackInfoPane meleeInfo = new AttackInfoPane(meleeAttack);
			attacks.getChildren().add(meleeInfo);
		}
		Attack rangedAttack = curPlayer.getRanged();
		if (rangedAttack != null) {
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
		heartPic.setImage(null);
		armorPic.setImage(null);
		nameText.setText("");
		healthText.textProperty().unbind();
		healthText.setText("");
		armorText.textProperty().unbind();
		armorText.setText("");
		agilityRatingText.textProperty().unbind();
		agilityRatingText.setText("");
		movementText.textProperty().unbind();
		movementText.setText("");
		attacks.getChildren().clear();
		descriptionText.setText("");

		terrainName.setText("");
		traversable.setText("");
		cover.setText("");
	}

	public void setTerrain(Terrain terrain) {
		terrainName.setText(terrain.toString());

		if (terrain.isTraversable()) {
			traversable.setText("traversable");
		} else {
			traversable.setText("not traversable");
		}

		if (terrain.providesCover()) {
			cover.setText("provides cover");
		} else {
			cover.setText("does not provide cover");
		}
	}
}

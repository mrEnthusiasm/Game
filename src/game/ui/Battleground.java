package game.ui;

import game.battles.Level;
import game.battles.Map;
import game.logic.ArtificialIntelligence;
import game.logic.Terrain;
import game.logic.Attack.AttackType;
import game.logic.Battle;
import game.player.Player;
import game.player.PlayerStatus;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class Battleground extends HBox {
	Level parentLevel;

	int columns;
	int rows;
	private Battle battle;
	ArtificialIntelligence badGuyBrain;

	Pane battlefield;
	BattleGrid battlegrid;

	InfoPane playerPane;
	InfoPane selectionPane;

	Text actionPoints = new Text();
	Button meleeButton;
	Button rangedButton;
	Button moveButton;
	Button specialButton;
	Button recoverButton;
	Button endTurnButton;

	public Battleground(Level level, Map map, List<Player> allies,
			List<Player> enemies) {
		getStylesheets().add("stylesheets/Battleground.css");

		parentLevel = level;

		columns = map.getNumberColumns();
		rows = map.getNumberRows();
		battle = new Battle(map, allies, enemies);
		badGuyBrain = new ArtificialIntelligence(battle, this);

		battlefield = new Pane();
		battlefield.getStyleClass().add("background");
		battlefield.setPadding(new Insets(15.0));
		battlefield.prefHeightProperty().bind(this.heightProperty());
		battlefield.prefWidthProperty()
				.bind(this.widthProperty().multiply(.75));
		battlefield.maxWidthProperty().bind(this.widthProperty().multiply(.75));

		battlegrid = new BattleGrid(map, allies, enemies);
		battlegrid.prefHeightProperty().bind(battlefield.heightProperty());
		battlegrid.maxWidthProperty().bind(battlegrid.prefHeightProperty());
		battlegrid.prefWidthProperty().bind(battlegrid.prefHeightProperty());
		battlegrid.layoutXProperty().bind(
				battlefield.widthProperty().divide(2)
						.subtract(battlegrid.widthProperty().divide(2)));
		battlegrid.updateSelection.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue.booleanValue() == true) {
					int column = battlegrid.getCurSelectedCol();
					int row = battlegrid.getCurSelectedRow();

					updateSelection(column, row);
					updateActions(column, row);

					battlegrid.updateSelection.set(false);
				}
			}
		});
		battlefield.getChildren().add(battlegrid);

		addSprites(allies, map.getNumberRows(), map.getNumberColumns());
		addSprites(enemies, map.getNumberRows(), map.getNumberColumns());

		VBox infoPane = new VBox();
		infoPane.prefWidthProperty().bind(this.widthProperty().multiply(.25));
		infoPane.minWidthProperty().bind(this.widthProperty().multiply(.25));

		// Top of info pane containing info on current player
		playerPane = new InfoPane();
		playerPane.minHeightProperty().bind(
				infoPane.heightProperty().multiply(.40));
		playerPane.maxHeightProperty().bind(
				infoPane.heightProperty().multiply(.40));

		// bottom of info pane containing info on current selection
		// be it player or terrain
		selectionPane = new InfoPane();
		selectionPane.prefHeightProperty().bind(
				infoPane.heightProperty().multiply(.40));

		// center part of info pane containing action buttons
		VBox actionPane = new VBox();
		actionPane.setAlignment(Pos.TOP_CENTER);
		actionPane.minHeightProperty().bind(
				infoPane.heightProperty().multiply(.20));
		actionPane.maxHeightProperty().bind(
				infoPane.heightProperty().multiply(.20));
		actionPane.prefWidthProperty().bind(infoPane.widthProperty());

		// TODO label this so they know what the number means
		actionPoints.setFont(new Font(20.0));

		GridPane actionButtons = new GridPane();
		actionButtons.prefWidthProperty().bind(actionPane.widthProperty());
		actionButtons.prefHeightProperty().bind(
				actionPane.prefHeightProperty().subtract(
						actionPoints.getFont().getSize()));
		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setFillWidth(true);
		columnConstraints.setHgrow(Priority.ALWAYS);
		actionButtons.getColumnConstraints().addAll(columnConstraints,
				columnConstraints, columnConstraints);

		meleeButton = new Button("melee");
		meleeButton.setDisable(true);
		meleeButton.setMaxWidth(Double.MAX_VALUE);
		meleeButton.setOnAction(e -> meleeAction(battlegrid
				.getCurSelectedPane().getTerrain().getResident()));
		actionButtons.add(meleeButton, 0, 0);

		rangedButton = new Button("ranged");
		rangedButton.setDisable(true);
		rangedButton.setMaxWidth(Double.MAX_VALUE);
		rangedButton.setOnAction(e -> rangedAction(battlegrid
				.getCurSelectedPane().getTerrain().getResident()));
		actionButtons.add(rangedButton, 1, 0);

		specialButton = new Button("special");
		// specialButton.setDisable(true);
		specialButton.setMaxWidth(Double.MAX_VALUE);
		// TODO this is just for testing
		specialButton.setOnAction(e -> specialTesting());
		actionButtons.add(specialButton, 2, 0);

		recoverButton = new Button("recover");
		recoverButton.setDisable(true);
		recoverButton.setMaxWidth(Double.MAX_VALUE);
		actionButtons.add(recoverButton, 0, 1);

		moveButton = new Button("move");
		moveButton.setOnAction(e -> moveAction());
		moveButton.setDisable(true);
		moveButton.setMaxWidth(Double.MAX_VALUE);
		actionButtons.add(moveButton, 1, 1);

		endTurnButton = new Button("end turn");
		endTurnButton.setMaxWidth(Double.MAX_VALUE);
		endTurnButton.setOnAction(e -> endTurn());
		actionButtons.add(endTurnButton, 2, 1);

		actionPane.getChildren().addAll(actionPoints, actionButtons);

		infoPane.getChildren().addAll(playerPane, actionPane, selectionPane);

		this.getChildren().addAll(battlefield, infoPane);

		setCurPlayer();

	}

	private void specialTesting() {
		System.out.println(battle.getCurPlayer().getXValue() + " "
				+ battle.getCurPlayer().getYValue());
	}

	private void addSprites(List<Player> players, int rows, int cols) {
		Iterator<Player> itr = players.iterator();
		Player currPlayer;
		ImageView playerImage;
		TerrainPane refPane = battlegrid.getPaneAt(0, 0);
		double padding = 5.0;
		while (itr.hasNext()) {
			currPlayer = itr.next();
			playerImage = new ImageView(currPlayer.getSpriteLocation());
			playerImage.setMouseTransparent(true);
			playerImage.fitHeightProperty().bind(
					refPane.heightProperty().subtract(padding * 2));
			playerImage.setPreserveRatio(true);
			// Attempt to place sprites in center of grid, but all grid panes
			// are not
			// the same size because width and height do not divide evenly so
			// it's a pixel
			// or so off
			playerImage.layoutXProperty().bind(
					battlegrid
							.layoutXProperty()
							.add(padding)
							.add(Bindings.multiply(currPlayer.getXProperty(),
									battlegrid.widthProperty().divide(cols))));
			playerImage.layoutYProperty().bind(
					battlegrid
							.layoutYProperty()
							.add(padding)
							.add(Bindings.multiply(currPlayer.getYProperty(),
									battlegrid.heightProperty().divide(rows))));
			currPlayer.setPlayerImage(playerImage);
			battlefield.getChildren().add(playerImage);
		}
	}

	private void endTurn() {
		if (battlegrid.getCurSelectedPane() != null) {
			battlegrid.clearCurSelection();
			selectionPane.clear();
		}
		battle.updatePlayers();
		TerrainPane tp = battlegrid.getCurPlayerPane();
		tp.getStyleClass().removeAll("current-player");
		actionPoints.textProperty().unbind();
		battle.nextPlayer();
		setCurPlayer();
		clearActions();
		selectionPane.clear();
		if (battle.isBadGuysTurn()) {
			clearActions();
			endTurnButton.setDisable(true);
			badGuyBrain.performAction();
		} else {
			endTurnButton.setDisable(false);
		}
	}

	private void highlightPossibleMoves() {
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				if (battle.curPlayerCanMoveTo(i, j)) {
					if (!battlegrid.getPaneAt(i, j).getStyleClass()
							.contains("possible-move")) {
						battlegrid.getPaneAt(i, j).getStyleClass()
								.add("possible-move");
					}
				} else {
					battlegrid.getPaneAt(i, j).getStyleClass()
							.remove("possible-move");
				}
			}
		}
	}

	public void setCurPlayer() {
		Player p = battle.getCurPlayer();
		battlegrid.setCurPlayerPane(battlegrid.getPaneAt(p.getXValue(),
				p.getYValue()));
		highlightPossibleMoves();

		battlegrid.getCurPlayerPane().getStyleClass().add("current-player");
		playerPane.setPlayer(battle.getCurPlayer());
		actionPoints.textProperty().bind(
				Bindings.convert(battle.getCurPlayer().actionPoints));

	}

	public void updateSelection(int col, int row) {
		Terrain terrain = battle.getSquare(col, row);
		if (terrain.isOccupied()) { // display player info
			selectionPane.clear();
			selectionPane.setPlayer(terrain.getResident());
		} else { // display terrain info
			selectionPane.clear();
			selectionPane.setTerrain(terrain);
		}
	}

	private void updateActions(int col, int row) {
		if (battle.isBadGuysTurn()) {
			return;
		}
		if (battle.curPlayerCanMoveTo(col, row)) {
			moveButton.setDisable(false);
		} else {
			moveButton.setDisable(true);
		}

		if (battle.getCurPlayer().getMelee() != null) {
			if (battle.curPlayerCanMelee(col, row)) {
				meleeButton.setDisable(false);
			} else {
				meleeButton.setDisable(true);
			}
		} else {
			meleeButton.setDisable(true);
		}

		if (battle.getCurPlayer().getRanged() != null) {
			if (battle.curPlayerCanRanged(col, row)) {
				rangedButton.setDisable(false);
			} else {
				rangedButton.setDisable(true);
			}
		} else {
			rangedButton.setDisable(true);
		}
	}

	private void clearActions() {
		moveButton.setDisable(true);
		meleeButton.setDisable(true);
		rangedButton.setDisable(true);
		specialButton.setDisable(true);
		recoverButton.setDisable(true);
	}

	private void moveAction() {
		moveButton.setDisable(true);
		// TODO may eventually want to make entire grid unclickable while
		// performing an action but for now that's a lot of work for a problem
		// that's not a problem yet

		Player movingPlayer = battle.getCurPlayer();
		TerrainPane oldPane = battlegrid.getCurPlayerPane();
		TerrainPane newPane = battlegrid.getCurSelectedPane();

		oldPane.getStyleClass().remove("current-player");
		oldPane.getTerrain().leave();
		newPane.getTerrain().occupy(movingPlayer);
		battlegrid.clearCurSelection();
		selectionPane.clear();

		Timeline moveAnimation = battle.getMovementAnimation(
				battle.getCurPlayer(), battlegrid.getCurSelectedCol(),
				battlegrid.getCurSelectedRow());
		moveAnimation.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onActionComplete();
			}
		});
		moveAnimation.play();
		movingPlayer.useActionPoints(1);
		battle.updateMoves(movingPlayer, battlegrid.getCurSelectedCol(),
				battlegrid.getCurSelectedRow());
	}

	private void meleeAction(Player defender) {
		int result = battle.curPlayerAttack(defender, AttackType.MELEE);
		// TODO adding all the fun animation bits
		if (result == 4) { // defender is blake and she dodged
			TerrainPane oldPane = battlegrid.getCurSelectedPane();
			oldPane.getTerrain().leave();

			TerrainPane newPane = battlegrid.getPaneAt(defender.getXValue(),
					defender.getYValue());
			newPane.getTerrain().occupy(defender);
			battlegrid.clearCurSelection();
			selectionPane.clear();
			onActionComplete();

		} else if (result == 3) { // defender dodged
			onActionComplete();
		} else if (result == 2) { // defender blocked
			onActionComplete();
		} else if (result == 1) { // defender hit
			PlayerStatus effect = battle.getCurPlayer().getMeleeStatusEffect();
			defender.addStatusEffect(effect, battle.getNumberOfPlayers());
			onActionComplete();
		} else if (result == 0) { // defender hit and dies
			battlefield.getChildren().remove(defender.getPlayerImage());
			battlegrid.clearCurSelection();
			selectionPane.clear();

			// TODO perform attack and death animation

			battle.updateMoves(battle.getCurPlayer(), battle.getCurPlayer()
					.getXValue(), battle.getCurPlayer().getYValue());
			// check if game over
			int end = battle.checkForEndGame();
			if (end != 0) {
				displayEndDialog(end);
			}
			onActionComplete();
		} else {
			System.err.println("UNRECOGNIZED MELEE ATTACK RESULT CODE");
		}
	}

	private void rangedAction(Player defender) {
		int result = battle.curPlayerAttack(defender, AttackType.RANGED);
		updateActions(defender.getXValue(), defender.getYValue());

		if (result == 4) { // defender is blake and she dodged
			TerrainPane oldPane = battlegrid.getCurSelectedPane();
			oldPane.getTerrain().leave();

			TerrainPane newPane = battlegrid.getPaneAt(defender.getXValue(),
					defender.getYValue());
			newPane.getTerrain().occupy(defender);
			battlegrid.clearCurSelection();
			selectionPane.clear();
			onActionComplete();

		} else if (result == 3) { // defender dodged
			onActionComplete();
		} else if (result == 2) { // defender blocked
			onActionComplete();
		} else if (result == 1) { // defender hit
			PlayerStatus effect = battle.getCurPlayer().getMeleeStatusEffect();
			defender.addStatusEffect(effect, battle.getNumberOfPlayers());
			onActionComplete();
		} else if (result == 0) { // defender died
			battlefield.getChildren().remove(defender.getPlayerImage());
			selectionPane.clear();

			// TODO perform attack and defend animations

			battle.updateMoves(battle.getCurPlayer(), battle.getCurPlayer()
					.getXValue(), battle.getCurPlayer().getYValue());
			// check if game over
			int end = battle.checkForEndGame();
			if (end != 0) {
				displayEndDialog(end);
			}
			highlightPossibleMoves();

			onActionComplete();
		} else {
			System.err.println("UNRECOGNIZED MELEE ATTACK RESULT CODE");
		}
	}

	private void onActionComplete() {
		Player p = battle.getCurPlayer();
		/*
		 * TODO it would be nice to change the character sprite rather than the
		 * terrain pane to highlight whose turn it is
		 */
		battlegrid.setCurPlayerPane(battlegrid.getPaneAt(p.getXValue(),
				p.getYValue()));
		highlightPossibleMoves();
		battlegrid.getCurPlayerPane().getStyleClass().add("current-player");

		if (battlegrid.getCurSelectedPane() != null) {
			updateActions(battlegrid.getCurSelectedCol(),
					battlegrid.getCurSelectedRow());
		}
		if (battle.isBadGuysTurn()) {
			if (battle.getCurPlayer().getActionPoints() > 0) {
				badGuyBrain.performAction();
			} else {
				endTurn();
			}
		}
	}

	public void displayEndDialog(int endingCode) {
		ButtonType doneButtonType = new ButtonType("Done");
		ButtonType retryButtonType = new ButtonType("Retry");
		Dialog<ButtonType> dlg = new Dialog<ButtonType>();
		dlg.initStyle(StageStyle.UNDECORATED);
		dlg.initModality(Modality.APPLICATION_MODAL);
		dlg.getDialogPane().getButtonTypes()
				.setAll(retryButtonType, doneButtonType);

		Text resultText = new Text();
		resultText.setFont(new Font(50.0));
		dlg.getDialogPane().setContent(resultText);
		dlg.getDialogPane().setPrefSize(500, 300);

		Optional<ButtonType> buttonSelection;
		if (endingCode == 1) { // you won!
			resultText.setText("Victory");
			buttonSelection = dlg.showAndWait();

			if (buttonSelection.get() == doneButtonType) {
				parentLevel.done();
			} else if (buttonSelection.get() == retryButtonType) {
				parentLevel.retry();
			} else {
				System.err.println("UNKNOWN GAME OVER DIALOG OPTION");
			}
		} else { // you lost!
			resultText.setText("Defeat");
			buttonSelection = dlg.showAndWait();
			if (buttonSelection.get() == doneButtonType) {
				parentLevel.done();
			} else if (buttonSelection.get() == retryButtonType) {
				parentLevel.retry();
			} else {
				System.err.println("UNKNOWN GAME OVER DIALOG OPTION");
			}
		}
	}

	// AI INTERACTION TOOLS
	public void AISelectPane(int x, int y) {
		battlegrid.AISelectPane(x, y);
	}

	public void AIMelee(Player defender) {
		meleeAction(defender);
	}

	public void AIRanged(Player defender) {
		rangedAction(defender);
	}

	public void AIMove() {
		moveAction();
	}
}

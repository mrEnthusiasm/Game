package game.view;

import game.battles.Level;
import game.battles.Map;
import game.logic.ArtificialIntelligence;
import game.logic.Battle;
import game.logic.Terrain;
import game.logic.Attack.AttackType;
import game.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class Battleground extends HBox {
	Level parentLevel;

	int columns;
	int rows;
	private Battle battle;
	ArtificialIntelligence badGuyBrain;

	BorderPane background;
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
		this.parentLevel = level;
		this.columns = map.getNumberColumns();
		this.rows = map.getNumberRows();
		battle = new Battle(map, allies, enemies);

		badGuyBrain = new ArtificialIntelligence(battle, this);

		this.getStylesheets().add("stylesheets/Battleground.css");

		background = new BorderPane();
		background.getStyleClass().add("background");
		background.setPadding(new Insets(15.0));
		background.prefHeightProperty().bind(this.heightProperty());
		background.prefWidthProperty().bind(this.widthProperty().multiply(.75));
		background.maxWidthProperty().bind(this.widthProperty().multiply(.75));

		this.battlegrid = new BattleGrid(map, allies, enemies);
		battlegrid.prefHeightProperty().bind(background.heightProperty());
		battlegrid.maxWidthProperty().bind(battlegrid.prefHeightProperty());
		// battlegrid.minWidthProperty().bind(battlegrid.prefHeightProperty());
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
		background.setCenter(battlegrid);

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
		specialButton.setDisable(true);
		specialButton.setMaxWidth(Double.MAX_VALUE);
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

		this.getChildren().addAll(background, infoPane);

		setCurPlayer();
	}

	private void endTurn() {

		if (battlegrid.getCurSelectedPane() != null) {
			battlegrid.clearCurSelection();
			selectionPane.clear();
		}
		TerrainPane tp = battlegrid.getCurPlayerPane();
		tp.getStyleClass().remove("current-player");
		actionPoints.textProperty().unbind();
		battle.nextPlayer();
		setCurPlayer();
		clearActions();
		selectionPane.clear();
		if (battle.isBadGuysTurn()) {
			badGuyBrain.run();
			if (battle.checkForEndGame() == 0) { // game not over
				endTurn();
			}

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
		battlegrid.setCurPlayerPane(battlegrid.getPaneAt(p.getX(), p.getY()));

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
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				battlegrid.getPaneAt(i, j).getStyleClass().remove("line-of-sight");
			}
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
			ArrayList<int[]> squares = battle.curPlayerCanRanged(col, row);
			if(squares == null){
				return;
			}
			Iterator<int[]> itr = squares.iterator();
			while(itr.hasNext()){
				int[] square = itr.next();
				battlegrid.getPaneAt(square[0], square[1]).getStyleClass().add("line-of-sight");
			}
		}
	}

	private void clearActions() {
		moveButton.setDisable(true);
		meleeButton.setDisable(true);
		rangedButton.setDisable(true);
		specialButton.setDisable(true);
	}

	public void moveAction() {
		TerrainPane oldPane = battlegrid.getCurPlayerPane();
		oldPane.getChildren().clear();
		oldPane.getStyleClass().remove("current-player");
		oldPane.getTerrain().leave();

		Player movingPlayer = battle.getCurPlayer();
		battle.move(movingPlayer, battlegrid.getCurSelectedCol(),
				battlegrid.getCurSelectedRow());

		TerrainPane newPane = battlegrid.getCurSelectedPane();
		newPane.getTerrain().occupy(movingPlayer);
		newPane.getStyleClass().remove("selected");
		battlegrid.setCurPlayerPane(newPane);
		Image i = new Image(movingPlayer.getSpriteLocation());
		ImageView iv = new ImageView(i);
		iv.fitHeightProperty().bind(
				newPane.heightProperty().subtract(
						newPane.getPadding().getTop()
								+ newPane.getPadding().getBottom()));
		iv.setPreserveRatio(true);
		newPane.getChildren().add(iv);

		moveButton.setDisable(true);
		setCurPlayer();
	}

	public void meleeAction(Player defender) {
		int result = battle.curPlayerAttack(defender, AttackType.MELEE);
		int end = battle.checkForEndGame();
		if (result == 0) {
			TerrainPane pane = battlegrid.getPaneAt(defender.getX(),
					defender.getY());
			pane.getChildren().clear();
			selectionPane.clear();
			// check if game over

			if (end != 0) {
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
				if (end == 1) { // you won!
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
			highlightPossibleMoves();
		}

		// need to update actions because used Action Point
		updateActions(defender.getX(), defender.getY());
	}

	private void rangedAction(Player defender) {
		int result = battle.curPlayerAttack(defender, AttackType.RANGED);
		updateActions(defender.getX(), defender.getY());

		if (result == 0) {
			TerrainPane pane = battlegrid.getPaneAt(defender.getX(),
					defender.getY());
			pane.getChildren().clear();
			selectionPane.clear();
			// check if game over
			int end = battle.checkForEndGame();
			if (end != 0) {
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
				if (end == 1) { // you won!
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
			highlightPossibleMoves();
		}

		// need to update actions because used Action Point
		updateActions(defender.getX(), defender.getY());
	}

	// AI INTERACTION TOOLS

	public void AISelectPane(int x, int y) {
		battlegrid.AISelectPane(x, y);
	}
}

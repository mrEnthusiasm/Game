package game;

import game.battles.LevelOne;
import game.battles.LevelThree;
import game.battles.LevelTwo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BattleSelectionPage extends BorderPane{
	public Stage primaryStage;
	BorderPane mainMenu;
	VBox battles;
	
	public BattleSelectionPage(Stage primaryStage, BorderPane mainMenu){
		this.primaryStage = primaryStage;
		this.mainMenu = mainMenu;
		
		battles = new VBox();
		battles.setAlignment(Pos.CENTER);
		battles.setSpacing(10.0);
		
		Text title = new Text("Battles");
		title.getStyleClass().add("title-text");
		
		Button levelOne	= new Button("Level 1");
		levelOne.setOnAction(e -> startLevelOne());
		Button levelTwo = new Button("Level 2");
		levelTwo.setOnAction(e -> startLevelTwo());
		Button levelThree = new Button("Level 3");
		levelThree.setOnAction(e -> startLevelThree());
		
		battles.getChildren().addAll(title, levelOne, levelTwo, levelThree);
		
		Button mainMenuButton = new Button("Main Menu");
		mainMenuButton.setOnAction(e -> openMainMenu());
		
		this.setCenter(battles);
		this.setBottom(mainMenuButton);
		this.setPadding(new Insets(5.0));
	}

	

	private void startLevelOne() {
		LevelOne levelOne = new LevelOne(this);
		primaryStage.getScene().setRoot(levelOne.getBattleground());
	}

	private void startLevelTwo() {
		LevelTwo levelTwo = new LevelTwo(this);
		primaryStage.getScene().setRoot(levelTwo.getBattleground());
	}
	
	private Object startLevelThree() {
		LevelThree levelThree = new LevelThree(this);
		primaryStage.getScene().setRoot(levelThree.getBattleground());
		return null;
	}
	
	public void closeLevel() {
		primaryStage.getScene().setRoot(this);
	}
	
	private void openMainMenu() {
		primaryStage.getScene().setRoot(mainMenu);
	}
}

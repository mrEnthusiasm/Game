package administration;

import game.BattleFieldPage;
import vn.StoryPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainApp extends Application {
	private Stage primaryStage;
	BorderPane rootLayout;
	BattleFieldPage battlefieldPage;
	StoryPage campaignPage;

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;
		primaryStage.setTitle("RWBY - The Game");
		initRootLayout();
	}

	public void initRootLayout() {
		VBox mainMenu = new VBox();
		mainMenu.setSpacing(10.0);
		Text title = new Text("Pre-Alpha 0.0.1");
		title.getStyleClass().add("title-text");
		Button campaign = new Button("Campaign");
		campaign.setOnAction(e -> openCampaign());
		Button battlegrounds = new Button("Battlegrounds");
		battlegrounds.setOnAction(e -> openBattleGrounds());
		Button settings = new Button("Settings");
		settings.setOnAction(e -> openSettings());
		settings.setDisable(true);
		mainMenu.getChildren().addAll(title, campaign, battlegrounds, settings);
		mainMenu.setAlignment(Pos.CENTER);
		
		rootLayout = new BorderPane();
		BorderPane.setAlignment(mainMenu, Pos.CENTER);
		rootLayout.setCenter(mainMenu);
		
		//not sure if I like this here, but it has to come after
		//rootLayout = new BorderPane()
		//may reorganize later
		battlefieldPage = new BattleFieldPage(primaryStage, rootLayout);
		campaignPage = new StoryPage();
		
		Scene scene = new Scene(rootLayout, 1120, 630);
		scene.getStylesheets().add("stylesheets/main.css");
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	

	private void openBattleGrounds() {
		primaryStage.getScene().setRoot(battlefieldPage);
	}
	
	private void openCampaign() {
		primaryStage.getScene().setRoot(campaignPage);
	}
	
	private void openSettings() {
		//TODO
	}

	public static void main(String[] args) {
		launch(args);
	}
}

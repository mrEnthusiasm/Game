package administration;

import game.BattleSelectionPage;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vn.StoryPage;
/**
 * The main entry point of of the application 
 * @author Daniel Schmidt
 *
 */
public class MainApp extends Application {
	private Stage primaryStage;
	BorderPane rootLayout;
	BattleSelectionPage battlefieldPage;
	StoryPage campaignPage;

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;
		primaryStage.setTitle("RWBY - The Game");
		initRootLayout();
	}

	public void initRootLayout() {
		Text title = new Text("Pre-Alpha 0.0.1");
		title.getStyleClass().add("title-text");
		
		Button campaign = new Button("Campaign");
		campaign.setOnAction(e -> openCampaign());
		
		Button battlegrounds = new Button("Battlegrounds");
		battlegrounds.setOnAction(e -> openBattleGrounds());
		
		Button settings = new Button("Settings");
		settings.setOnAction(e -> openSettings());
		settings.setDisable(true);
		
		VBox mainMenu = new VBox();
		mainMenu.setSpacing(10.0);
		mainMenu.getChildren().addAll(title, campaign, battlegrounds, settings);
		mainMenu.setAlignment(Pos.CENTER);
		
		BorderPane.setAlignment(mainMenu, Pos.CENTER);
		rootLayout = new BorderPane();
		rootLayout.setCenter(mainMenu);
		
		battlefieldPage = new BattleSelectionPage(primaryStage, rootLayout);
		campaignPage = new StoryPage();
		
		Scene scene = new Scene(rootLayout, 1344, 756);
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
		//TODO method to open the settings page
	}

	public static void main(String[] args) {
		launch(args);
	}
}

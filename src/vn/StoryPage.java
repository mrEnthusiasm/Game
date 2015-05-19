package vn;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class StoryPage extends StackPane {
	GridPane textPane;
	Text speakerName;
	Text dialogue;
	
	ImageView background;

	public StoryPage() {
		this.getStylesheets().add("stylesheets/StoryPage.css");
		this.setPadding(new Insets(7.0));

		textPane = new GridPane();
		textPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
		textPane.prefHeightProperty()
				.bind(this.heightProperty().multiply(0.20));
		textPane.maxHeightProperty().bind(this.heightProperty().multiply(0.20));
		StackPane.setAlignment(textPane, Pos.BOTTOM_CENTER);

		speakerName = new Text();
		speakerName.setTextAlignment(TextAlignment.CENTER);
		speakerName.setFont(new Font(30.0));
		speakerName.setFill(Color.WHITE);
		speakerName.wrappingWidthProperty().bind(
				this.widthProperty().multiply(0.25));
		GridPane.setConstraints(speakerName, 0, 0, 1, 1, HPos.CENTER,
				VPos.CENTER, Priority.NEVER, Priority.SOMETIMES, new Insets(5.0,
						5.0, 5.0, 5.0));

		dialogue = new Text();
		dialogue.setTextAlignment(TextAlignment.CENTER);
		dialogue.setFont(new Font(20.0));
		dialogue.setFill(Color.WHITE);
		dialogue.wrappingWidthProperty().bind(
				this.widthProperty().multiply(0.50));
		GridPane.setConstraints(dialogue, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER,
				Priority.NEVER, Priority.SOMETIMES, new Insets(15.0, 15.0, 15.0,
						15.0));

		textPane.getChildren().addAll(speakerName, dialogue);

		background = new ImageView(new Image("resources/images/background.png"));
		background.fitWidthProperty().bind(this.widthProperty());
		background.setPreserveRatio(true);
		//StackPane.setAlignment(background, Pos.BOTTOM_CENTER);
		
		this.getChildren().addAll(background, textPane);
		
		setSpeaker("Daniel");
		setDialog("This is a whole bunch of text because yet again I need to see how much"
				+ "stuff I can really fit into this thing. This feels like a lot");
	}

	public void setDialog(String text) {
		dialogue.setText(text);
	}

	public void setSpeaker(String speaker) {
		speakerName.setText(speaker);
	}
	
}

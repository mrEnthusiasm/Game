package game.ui;

import game.logic.Terrain;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

public class TerrainPane extends StackPane {
	private Terrain terrain;

	public TerrainPane(Terrain t) {
		this.terrain = t;
		this.getStylesheets().add("stylesheets/TerrainPane.css");
		this.getStyleClass().add("default");
		this.setMaxSize(200, 200);
		this.setMinSize(25.0, 25.0);
		this.setSnapToPixel(false);
		
		//Customize pane for specific terrain
		this.getStyleClass().add(t.getCss());
	}

	public Terrain getTerrain() {
		return terrain;
	}
}

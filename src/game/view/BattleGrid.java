package game.view;

import game.battles.Map;
import game.player.Player;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class BattleGrid extends GridPane {
	private TerrainPane curSelection;
	private int curSelectedCol;
	private int curSelectedRow;
	BooleanProperty updateSelection;
	private TerrainPane curPlayerPane;
	private TerrainPane[][] grid; //needed because gridpane has no api to retrieve by [row][col]
	private int width;
	private int height;
	
	public BattleGrid(Map map, List<Player> allies, List<Player> enemies) {
		this.getStylesheets().clear();
		this.getStylesheets().add("stylesheets/Battlegrid.css");
		this.setAlignment(Pos.CENTER);
		
		this.width = map.getNumberColumns();
		this.height = map.getNumberRows();
		
		for(int i=0; i<width; i++){
			ColumnConstraints cc = new ColumnConstraints();
			cc.setHgrow(Priority.ALWAYS);
			this.getColumnConstraints().add(cc);
		}
		for(int j=0; j<height; j++){
			RowConstraints rc = new RowConstraints();
			rc.setVgrow(Priority.ALWAYS);
			this.getRowConstraints().add(rc);
		}

		updateSelection = new SimpleBooleanProperty();
		updateSelection.set(false);
		grid = new TerrainPane[width][height];
		
		for (int i=0; i<width; i++) {
			for (int j = 0; j < height; j++) {
				TerrainPane pane = new TerrainPane(map.getTerrainAt(i, j));
				pane.setOnMouseClicked(e -> setCurSelection(pane));
				grid[i][j] = pane;
				this.add(pane, i, j);
			}
		}
		
		for (Player p : allies) {
			Image i = new Image(p.getSpriteLocation());
			ImageView iv = new ImageView(i);
			TerrainPane tp = grid[p.getX()][p.getY()];
			iv.fitHeightProperty().bind(tp.heightProperty().subtract(
					tp.getPadding().getTop()+tp.getPadding().getBottom()));
			iv.setPreserveRatio(true);
			tp.getChildren().add(iv);
		}
		for (Player p : enemies) {
			Image i = new Image(p.getSpriteLocation());
			ImageView iv = new ImageView(i);
			TerrainPane tp = grid[p.getX()][p.getY()];
			iv.fitHeightProperty().bind(tp.heightProperty().subtract(
					tp.getPadding().getTop()+tp.getPadding().getBottom()));
			iv.setPreserveRatio(true);
			tp.getChildren().add(iv);
		}
	}
	
	private void setCurSelection(TerrainPane p) {
		if (p != curPlayerPane && p != curSelection) {
			if (curSelection == null) {
				curSelection = p;
				p.getStyleClass().add("selected");
				
				curSelectedRow = GridPane.getRowIndex(p);
				curSelectedCol = GridPane.getColumnIndex(p);
				updateSelection.set(true);
			} else {
				curSelection.getStyleClass().remove("selected");
				curSelection = p;
				p.getStyleClass().add("selected");
				
				curSelectedRow = GridPane.getRowIndex(p);
				curSelectedCol = GridPane.getColumnIndex(p);
				updateSelection.set(true);
			}
		}
	}
	
	public void AISelectPane(int x, int y){
		setCurSelection(grid[x][y]);
	}
	
	public TerrainPane getCurPlayerPane(){
		return this.curPlayerPane;
	}
	
	public void setCurPlayerPane( TerrainPane t){
		this.curPlayerPane = t;
	}
	
	public TerrainPane getPaneAt(int col, int row){
		return grid[col][row];
	}
	
	public int getCurSelectedCol(){
		return curSelectedCol;
	}
	
	public int getCurSelectedRow(){
		return curSelectedRow;
	}
	public TerrainPane getCurSelectedPane() {
		return curSelection;
	}

	public void clearCurSelection() {
		curSelection.getStyleClass().remove("selected");
		curSelection = null;
		
	}
}

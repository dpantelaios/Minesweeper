package application;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import application.Tile;

public class Hyper_Tile extends Tile{
    private int x, y;
    private boolean isOpen = false;
    private boolean marked = false;
	private static final int TILE_SIZE = 40;
    private int X_TILES = 16;
    private int Y_TILES = 16;

    private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2, Color.LIGHTGRAY);
    private static final Image image = new Image("file:images/flag.png");
    ImageView imageView = new ImageView();

    public Hyper_Tile(int x1, int y1, boolean hasBomb1, int X_TILES, int Y_TILES) {
        super(x1, y1, true, X_TILES, Y_TILES);
        this.x = x1;
        this.y = y1;
        this.X_TILES = X_TILES;
        this.Y_TILES = Y_TILES;
        
        border.setStroke(Color.BLACK);

        getChildren().add(border);
        setTranslateX(y * TILE_SIZE);
        setTranslateY(x * TILE_SIZE + 100);
        setOnMouseClicked(event -> 
        {
        	MouseButton button = event.getButton();
        	if (button == MouseButton.PRIMARY) {
        		if(!marked)
        			open();
        	}
        	else if (button == MouseButton.SECONDARY){
        		imageView.setFitHeight(TILE_SIZE-2);
        		imageView.setFitWidth(TILE_SIZE-2);   
        		TextField markedbombs = MinesweeperApp.getMarkedBombes();
        		if(!marked) {
    				System.out.println(total_moves);
        			if(total_moves < 5) {
        				Image image1 = new Image("file:images/hyper_bomb.jpg");
        		           //Creating the image view
    		            ImageView imageView1 = new ImageView();
        				imageView1.setFitHeight(TILE_SIZE-2);
                		imageView1.setFitWidth(TILE_SIZE-2);
                		imageView1.setImage(image1);
                		getChildren().add(imageView1);

        				Tile[][] grid = MinesweeperApp.getGrid();
        		        for(int i=0; i<X_TILES; i++) {
        					if(i!=this.y)
        						grid[this.x][i].hyper_open();
        				}
        				for(int j=0; j<Y_TILES; j++) {
        					if(j!=this.x)
        						grid[j][this.y].hyper_open();
        				}
        			}
        			else {
        				imageView.setImage(image);
	            		getChildren().add(imageView);
	            		
	            		int marked__bombs = Integer.parseInt(markedbombs.getText());
	            		markedbombs.setText(String.valueOf((++marked__bombs)));
	            		marked = true;
        			}
        		}
        		else {
        			if(!isOpen) {
	        			imageView.setImage(image);
	        			marked = false;
	        			getChildren().remove(imageView);
	        			
	        			int marked__bombs = Integer.parseInt(markedbombs.getText());
	            		markedbombs.setText(String.valueOf((--marked__bombs)));
        			}
        		}
        	}
        });
    }

    public void open() {
        if (isOpen)
            return;

        System.out.println("Game Over");
        Image image = new Image("file:images/hyper_bomb.jpg");
       //Creating the image view
        ImageView imageView = new ImageView();
       
       //Setting the image view parameters
        imageView.setFitHeight(TILE_SIZE-2);
        imageView.setFitWidth(TILE_SIZE-2);
        imageView.setImage(image);
        getChildren().add(imageView);
        Timer tm = MinesweeperApp.getTimer();
        tm.cancel();
        bomb_pressed();
        return;
    }
}

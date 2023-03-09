package application;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Tile extends StackPane{
	private static final int TILE_SIZE = 40;
	
    protected int x, y;
    protected int number_of_bombs, difficulty_level;
    protected boolean Bomb = false;
    private boolean Open = false;
    private boolean marked = false;
    private static int tiles_revealed=0, openedWhileMarked = 0;
    protected List<Tile> neighbors = new ArrayList<>();
    
    
    protected Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2, Color.LIGHTGRAY); // create rectangle with colour gray and the specified size
    protected Text text = new Text();
    ImageView imageView = new ImageView();

    public Tile(int x, int y, boolean Bomb, int difficulty_level) {
        this.x = x; // Save Tile position
        this.y = y;
        this.Bomb = Bomb; // Save if Tile has bomb
        this.difficulty_level = difficulty_level; // Save difficulty level of current mode
        
        border.setStroke(Color.BLACK); // Set stroke colour to black
        text.setVisible(false);
        text.setFont(Font.font(19)); // Change font to 19
        
        this.setDisable(false); 
        
        getChildren().addAll(border, text);
        // Set Tile position
        setTranslateX((y+3.5*(2 -difficulty_level)) * TILE_SIZE); // x represents rows, but x axis corresponds to columns
        setTranslateY(x * TILE_SIZE + 100);
    }
    
    public void left_click() { // when a Tile is left clicked 
		tiles_revealed = 0; //in every left_click
		open(); // open Tile
    }
    
    public void right_click(int total_moves) {  // when a Tile is right clicked 
    	Image image1 = new Image("file:images/flag.png"); 
		imageView.setFitHeight(TILE_SIZE-2);
		imageView.setFitWidth(TILE_SIZE-2);
		imageView.setImage(image1);
		if(!marked && !Open) { // if not marked and closed 
    		getChildren().add(imageView); //display flag image
    		marked = true; // Set boolean equal to true to avoid displaying image more times
		}
		else {
			marked = false; // Set boolean equal to false in order to be able to display on next right click
			if(!Open) {
    			getChildren().remove(imageView); // remove flag image if Tile is still closed and already marked
			}
		}
    }
    
    public void open() {
        if (Open)
            return;

        if (Bomb) { // If the Tile contains bomb display bomb image
           Image image = new Image("file:images/bomb.png");
           //Creating the image view
           ImageView imageView = new ImageView();           
           //Setting the image view parameters
           imageView.setFitHeight(TILE_SIZE-2);
           imageView.setFitWidth(TILE_SIZE-2);
           //Setting image to the image view

           imageView.setImage(image);
           getChildren().add(imageView);
           
           Open = true; //set boolean Open equal to true
           
           return;
        }
        if(marked) { // if it was marked while opened first remove flag image
        	Image image1 = new Image("file:images/flag.png");
        	imageView.setFitHeight(TILE_SIZE-2);
    		imageView.setFitWidth(TILE_SIZE-2);
    		imageView.setImage(image1);
        	marked = false;
    		getChildren().remove(imageView);
    		openedWhileMarked += 1; // increase number of marked tiles when opened
        }
        tiles_revealed ++; //count tiles_revealed with every left click
        Open = true; //set boolean Open equal to true
        text.setVisible(true); // show Text
        border.setFill(Color.WHITE);
        if (text.getText().isEmpty()) { // if Tile does not have neighbors with bombs, open recursively all neighbors
            neighbors.forEach(Tile::open);
        }
    }
    
    public void hyper_open() { // open when a hyper bomb is flagged within 5 moves, do not open recursively
        if (Open)
            return;

        if (Bomb) { // if Tile contains bomb then display bomb image
           Image image = new Image("file:images/bomb.png");
           ImageView imageView = new ImageView();
           
           imageView.setFitHeight(TILE_SIZE-2);
           imageView.setFitWidth(TILE_SIZE-2);
           imageView.setImage(image);
           getChildren().add(imageView);
           Open = true; //set boolean Open equal to true , so it cant be pressed again
           return;
        }
        if(marked) { // if it was marked while opened first remove flag image
        	Image image1 = new Image("file:images/flag.png");
        	imageView.setFitHeight(TILE_SIZE-2);
    		imageView.setFitWidth(TILE_SIZE-2);
    		imageView.setImage(image1);
        	marked = false;
    		getChildren().remove(imageView);
    		openedWhileMarked += 1; // increase number of marked tiles when opened
        }
        tiles_revealed++; //count tiles_revealed with every left click
        Open = true; //set boolean Open equal to true  
        text.setVisible(true);
        border.setFill(Color.WHITE);
    }
    
    public boolean getBomb() { // return if Tile is marked
    	return Bomb;
    }
    
    public void setBomb(boolean bomb) { // return if Tile is open
    	Bomb = bomb;
    }  
    
    public boolean getMarkedBombs() { // return if Tile is marked
    	return marked;
    }
    
    public boolean getOpen() { // return if Tile is open
    	return Open;
    }  
    
    public int getTilesRevealed() { // get Tiles revealed during a left click or when a hyper bomb is flagged within 5 moves
    	return tiles_revealed;
    }  
    
    public void setTilesRevealed() { // Initialize tiles revealed to zero for next left click or hyper open
    	tiles_revealed=0;
    } 
    
    public int getOpenedWhileMarked() { // get Marked Tiles that got opened during a left click or when a hyper bomb is flagged within 5 moves
    	return openedWhileMarked;
    }  
    
    public void setOpenedWhileMarked() { // Initialize  Marked Tiles that got opened to zero for next left click or hyper open
    	openedWhileMarked = 0;
    } 
}

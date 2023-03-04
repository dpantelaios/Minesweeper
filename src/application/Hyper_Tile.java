package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hyper_Tile extends Tile{
    private boolean Open = false;
    private boolean marked = false;
	private static final int TILE_SIZE = 40;

    private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2, Color.GREEN);
    ImageView imageView = new ImageView();

    public Hyper_Tile(int x1, int y1, boolean Bomb1, int difficulty_level) {
        super(x1, y1, true, difficulty_level); // create parent class Tile
        this.x = x1; // Save Hyper Tile position
        this.y = y1;
        this.Bomb = Bomb1;
        border.setStroke(Color.BLACK);

        getChildren().add(border);
    }
    
    public void left_click() { // when left click is pressed open Hyper Tile
		open();
    }
    
    public void right_click(int total_moves) { // when right click is pressed
        //Setting the image view parameters
    	imageView.setFitHeight(TILE_SIZE-2);
		imageView.setFitWidth(TILE_SIZE-2);   
		if(!marked) { // if not flagged
			if(total_moves < 5) { // if flagged within 5 moves display hyper bomb image
				Image image1 = new Image("file:images/hyper_bomb.jpg");
				//Creating the image view
	            ImageView imageView1 = new ImageView();
				imageView1.setFitHeight(TILE_SIZE-2);
        		imageView1.setFitWidth(TILE_SIZE-2);
        		imageView1.setImage(image1);
        		getChildren().add(imageView1);  
        		Open = true;
			}
			else { // else display flag image
				Image image2 = new Image("file:images/flag.png");
				imageView.setImage(image2);
        		getChildren().add(imageView);        		
			}
    		marked = true; // set marked equal to true in order to prevent displaying flag image more than 1 time
		}
		else {
			if(!Open) { // if not opened and already marked remove flag image
				Image image2 = new Image("file:images/flag.png");
				imageView.setImage(image2);
    			marked = false; // Set boolean equal to false in order to be able to display on next right click
    			getChildren().remove(imageView);   			
			}
		}
    }
    
    public void open() { // when opened display hyper bomb image
        if (Open)
            return;
        
        Open = true;
        Image image = new Image("file:images/hyper_bomb.jpg");
       //Creating the image view
        ImageView imageView = new ImageView();
       //Setting the image view parameters
        imageView.setFitHeight(TILE_SIZE-2);
        imageView.setFitWidth(TILE_SIZE-2);
        imageView.setImage(image);
        getChildren().add(imageView);
    }
    
    public boolean getOpen() { // return if Hyper Tile is open
    	return Open;
    }  
    
    public boolean getMarked() { // return if Hyper Tile is open
    	return marked;
    } 
}

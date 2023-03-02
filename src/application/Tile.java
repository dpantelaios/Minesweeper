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
    protected boolean hasBomb;
    private boolean isOpen = false;
    private boolean marked = false;
    private static int tiles_revealed=0;
    protected List<Tile> neighbors = new ArrayList<>();
    
    
    private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2, Color.LIGHTGRAY);
    protected Text text = new Text();
    private static final Image image = new Image("file:images/flag.png");
    ImageView imageView = new ImageView();

    public Tile(int x, int y, boolean hasBomb, int difficulty_level) {
        this.x = x;
        this.y = y;
        this.hasBomb = hasBomb;
        this.difficulty_level = difficulty_level;
        
        border.setStroke(Color.BLACK);
        text.setFont(Font.font(18));
        text.setText(hasBomb ? "X" : "");
        text.setVisible(false);
        
        this.setDisable(false);
        
        getChildren().addAll(border, text);
        
        setTranslateX((y+3.5*(2 -difficulty_level)) * TILE_SIZE); // x represents rows, but x axis corresponds to columns
        setTranslateY(x * TILE_SIZE + 100);
    }
    
    public void left_click(int total_moves) { //returns if bomb pressed
    	if(!marked) {
    		tiles_revealed = 0; //in every left_click
    		open();
    		//total_moves++;
		}
    }
    
    public void right_click(int total_moves) {
		imageView.setFitHeight(TILE_SIZE-2);
		imageView.setFitWidth(TILE_SIZE-2);
		imageView.setImage(image);
		if(!marked && !isOpen) {
    		getChildren().add(imageView);

    		marked = true;
		}
		else {
			marked = false;
			if(!isOpen) {
    			getChildren().remove(imageView);
			}
		}
    }
    
    public void open() {
        if (isOpen)
            return;

        if (hasBomb) {
           System.out.println("Game Over");
           Image image = new Image("file:images/bomb.png");
           //Creating the image view
           ImageView imageView = new ImageView();
           //Setting image to the image view
           
           //Setting the image view parameters
           imageView.setFitHeight(TILE_SIZE-2);
           imageView.setFitWidth(TILE_SIZE-2);
           imageView.setImage(image);
           getChildren().add(imageView);
           
           isOpen = true;
           
           return;
        }
        tiles_revealed ++; //count tiles_revealed with every left click
        isOpen = true;
        text.setVisible(true);
        border.setFill(null);
        if (text.getText().isEmpty()) {
            neighbors.forEach(Tile::open);
        }
    }
    
    public void hyper_open() {
        if (isOpen)
            return;

        if (hasBomb) {
           Image image = new Image("file:images/bomb.png");
           ImageView imageView = new ImageView();
           
           imageView.setFitHeight(TILE_SIZE-2);
           imageView.setFitWidth(TILE_SIZE-2);
           imageView.setImage(image);
           getChildren().add(imageView);
           return;
        }
        tiles_revealed++;
        isOpen = true;
        text.setVisible(true);
        border.setFill(null);
    }
    
    public boolean getMarkedBombs() {
    	return marked;
    }
    
    public boolean getOpen() {
    	return isOpen;
    }  
    
    public int getTilesRevealed() {
    	return tiles_revealed;
    }  
    
    public void setTilesRevealed() {
    	tiles_revealed=0;
    }  
}

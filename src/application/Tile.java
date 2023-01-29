package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Tile extends StackPane{
	private static final int TILE_SIZE = 40;
    private int X_TILES = 16;
    private int Y_TILES = 16;
    private int W = X_TILES*40;
    private int H = Y_TILES*40+200;
	
    protected int x, y;
    protected int number_of_bombs;
    protected boolean hasBomb;
    private boolean isOpen = false;
    private boolean marked = false;
    protected List<Tile> neighbors = new ArrayList<>();
    public static int total_moves = 0;
    
    
    private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2, Color.LIGHTGRAY);
    protected Text text = new Text();
    private static final Image image = new Image("file:images/flag.png");
    ImageView imageView = new ImageView();

    public Tile(int x, int y, boolean hasBomb, int X_TILES, int Y_TILES) {
        this.x = x;
        this.y = y;
        this.X_TILES = X_TILES;
        this.Y_TILES = Y_TILES;
        this.hasBomb = hasBomb;

        border.setStroke(Color.BLACK);
        text.setFont(Font.font(18));
        text.setText(hasBomb ? "X" : "");
        text.setVisible(false);

        getChildren().addAll(border, text);
        
        int diff = MinesweeperApp.getDifficulty_level();
        setTranslateX((y+3.5*(2 -diff)) * TILE_SIZE); // x represents rows, but x axis corresponds to columns
        setTranslateY(x * TILE_SIZE + 100);
        setOnMouseClicked(event -> 
        {
        	MouseButton button = event.getButton();
        	if (button == MouseButton.PRIMARY) {
        		if(!marked) {
	        		open();
	        		total_moves++;
        		}
        	}
        	else if (button == MouseButton.SECONDARY){
        		
        		TextField markedbombs = MinesweeperApp.getMarkedBombes();
        		imageView.setFitHeight(TILE_SIZE-2);
        		imageView.setFitWidth(TILE_SIZE-2);
        		imageView.setImage(image);
        		if(!marked && !isOpen) {
            		getChildren().add(imageView);
	
            		int marked__bombs = Integer.parseInt(markedbombs.getText());
            		markedbombs.setText(String.valueOf((++marked__bombs)));
            		marked = true;
        		}
        		else {
        			marked = false;
        			if(!isOpen) {
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

        if (hasBomb) {
           System.out.println("Game Over");
           //InputStream stream = new FileInputStream("C:\\LAB-2021\\ECLIPSE\\Eclipse-Workspace\\Minesweeper\\src\\application\\bomb.png");
           //Image image = new Image("File:C:\\LAB-2021\\ECLIPSE\\Eclipse-Workspace\\Minesweeper\\src\\application\\bomb.png");
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

           Timer tm = MinesweeperApp.getTimer();
           tm.cancel();
           //scene.setRoot(createContent());
           //scene = new Scene(createContent());
           return;
        }
        
        //int total_moves = MinesweeperApp.getTotalMoves();
        isOpen = true;
        text.setVisible(true);
        border.setFill(null);
        //total_moves++;
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
           //tm.cancel();
           return;
        }

        isOpen = true;
        text.setVisible(true);
        border.setFill(null);
        //total_moves++;
    }
}

package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Hyper_Tile extends Tile{
    private boolean isOpen = false;
    private boolean marked = false;
	private static final int TILE_SIZE = 40;

    private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2, Color.LIGHTGRAY);
    private static final Image image = new Image("file:images/flag.png");
    ImageView imageView = new ImageView();

    public Hyper_Tile(int x1, int y1, boolean hasBomb1, int difficulty_level) {
        super(x1, y1, true, difficulty_level);
        this.x = x1;
        this.y = y1;
        this.hasBomb = hasBomb1;
        border.setStroke(Color.BLACK);

        getChildren().add(border);
//        setTranslateX(y * TILE_SIZE);
//        setTranslateY(x * TILE_SIZE + 100);
    }
    
    public void left_click(int total_moves) {
    	if(!marked)
			open();
    }
    
    public void right_click(int total_moves) { 
    	imageView.setFitHeight(TILE_SIZE-2);
		imageView.setFitWidth(TILE_SIZE-2);   
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
			}
			else {
				imageView.setImage(image);
        		getChildren().add(imageView);        		
			}
    		marked = true;
		}
		else {
			if(!isOpen) {
    			imageView.setImage(image);
    			marked = false;
    			getChildren().remove(imageView);   			
			}
		}
    }
    
    public void open() {
        if (isOpen)
            return;
        
        isOpen = true;
        System.out.println("Game Over");
        Image image = new Image("file:images/hyper_bomb.jpg");
       //Creating the image view
        ImageView imageView = new ImageView();
       
       //Setting the image view parameters
        imageView.setFitHeight(TILE_SIZE-2);
        imageView.setFitWidth(TILE_SIZE-2);
        imageView.setImage(image);
        getChildren().add(imageView);
    }
}

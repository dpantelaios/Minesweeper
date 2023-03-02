package application;

import classes.Coordinate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;


public class MinesweeperBoard 
{
    private int X_TILES = 16;
    private int Y_TILES = 16;
    GridPane root;
    
    protected static Tile[][] grid;
    private List<Coordinate> bombs_location;
    protected int hyper_bomb_x = 0, hyper_bomb_y = 0;

        
    public MinesweeperBoard(int difficulty_level, boolean hyper_bomb, int number_of_bombs)
    {
    	root = new GridPane();
    	if(difficulty_level == 2) {
    		X_TILES = 16;
    		Y_TILES = 16;
    	}
    	else if(difficulty_level == 1) {
    		X_TILES = 9;
    		Y_TILES = 9;    		
    	}

        grid = new Tile[X_TILES][Y_TILES];
        int remaining_bombs, remaining_tiles;
        
        if(difficulty_level == 2 && hyper_bomb) {
        	hyper_bomb_x = ThreadLocalRandom.current().nextInt(1, X_TILES);
        	hyper_bomb_y = ThreadLocalRandom.current().nextInt(2, Y_TILES);
        	System.out.print("HYPER BOMB LOCATION");
        	System.out.print(hyper_bomb_x);
        	System.out.println(hyper_bomb_y);
        }
        
    	remaining_bombs = number_of_bombs;
        remaining_tiles = X_TILES*Y_TILES;

        bombs_location = new ArrayList<Coordinate>();
               
        boolean will_have_bomb = false;
        for (int x = 0; x < X_TILES; x++) {
            for (int y = 0; y < Y_TILES; y++) {
            	if(remaining_tiles - remaining_bombs <= 0) { //if the number of remaining bombs equals the number of remaining tiles put bombs to all the rest tiles
            		will_have_bomb = true;
            	}
            	else if(remaining_bombs > 0){ //if the are still bombs to place
                	will_have_bomb = (Math.random() < (float)number_of_bombs/(X_TILES*Y_TILES));
            	}
            	else {
            		will_have_bomb = false;
            	}
            	
            	boolean hyper = (((x+1)==hyper_bomb_x) && ((y+1)==hyper_bomb_y));
            	
            	if(difficulty_level == 2 && hyper_bomb && hyper) {
            		Hyper_Tile tile = new Hyper_Tile(x, y, hyper, difficulty_level);
            		grid[x][y] = tile;
                    root.getChildren().add(tile);
            	}
            	else if(!hyper){
            		Tile tile = new Tile(x, y, will_have_bomb, difficulty_level);
            		grid[x][y] = tile;
                    root.getChildren().add(tile);
            	}
            	
            	remaining_tiles--;
                if(difficulty_level == 2 && hyper_bomb && (will_have_bomb || hyper)) {
                	remaining_bombs--;
                	bombs_location.add(new Coordinate(x, y, hyper));
                }
                else if(will_have_bomb) {
                	remaining_bombs--;
                	bombs_location.add(new Coordinate(x, y));
                }  
                hyper = false;
            }
        }
        
    	try {
    		new FileWriter("output\\mines.txt", false).close();
    		FileWriter fstream = new FileWriter("output\\mines.txt");
			BufferedWriter out = new BufferedWriter(fstream);
    		for(Coordinate i: bombs_location) {
				if(difficulty_level == 2 && hyper_bomb) {
					out.write(i.getX() + "," + i.getY() + "," + i.getHyperBomb() + "\n");
					
		    		System.out.println(i.toString_hyper());
				}
				else {
					out.write(i.getX() + "," + i.getY() + "\n");
					System.out.println(i.toString());
				}
    		}
    		out.close();
    	}catch (Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = grid[x][y];

                if (tile.hasBomb)
                    continue;
                
        		List<Tile> neighbors_help = getNeighbors(tile);
        		int bombs = 0;		
        		for (Tile neighbor : neighbors_help) {
        		    if(neighbor.hasBomb) {
        		    	bombs += 1;
        		    	
        		    }
        		}		

                if (bombs > 0) {
                    tile.number_of_bombs = bombs;
                  	tile.text.setText(String.valueOf(tile.number_of_bombs));
                  	if(bombs == 3)
                  		tile.text.setFill(Color.RED); 
                  	else if(bombs == 2)
                  		tile.text.setFill(Color.GREEN); 
                  	else
                  		tile.text.setFill(Color.BLUE); 
                }
            }
        }
    }
    
    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        int[] points = new int[] {
              -1, -1,
              -1, 0,
              -1, 1,
              0, -1,
              0, 1,
              1, -1,
              1, 0,
              1, 1
        };

        for (int i = 0; i < points.length; i++) {
            int dx = points[i];
            int dy = points[++i];

            int newX = tile.x + dx;
            int newY = tile.y + dy;

            if (newX >= 0 && newX < X_TILES
                    && newY >= 0 && newY < Y_TILES) {
                neighbors.add(grid[newX][newY]);
            }
        }
        
        tile.neighbors = neighbors;
        
        return neighbors;
    }
    
    public Tile[][] getGrid() {
    	return grid;
    }
    
    public List<Coordinate> getBombLocation() {
    	return bombs_location;
    }
    
    public GridPane getBoard()
    {
        return root;
    }
}

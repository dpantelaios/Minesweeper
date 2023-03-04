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
    private int X_AXIS_TILES = 16;
    private int Y_AXIS_TILES = 16;
    GridPane root;
    
    protected static Tile[][] grid;
    private List<Coordinate> bombs_location;
    protected int hyper_bomb_x = 0, hyper_bomb_y = 0;

        
    public MinesweeperBoard(int difficulty_level, boolean hyper_bomb, int number_of_bombs)
    {
    	root = new GridPane();
    	if(difficulty_level == 2) { // Set number of Tiles depending on difficulty level
    		X_AXIS_TILES = 16;
    		Y_AXIS_TILES = 16;
    	}
    	else if(difficulty_level == 1) {
    		X_AXIS_TILES = 9;
    		Y_AXIS_TILES = 9;    		
    	}

        grid = new Tile[X_AXIS_TILES][Y_AXIS_TILES];
        int remaining_bombs, remaining_TILES;
    	remaining_bombs = number_of_bombs; // remaining bombs to add
        remaining_TILES = X_AXIS_TILES*Y_AXIS_TILES; // remaining Tiles to create
        
        if(difficulty_level == 2 && hyper_bomb) { // Set a location for hyper bomb if exists
        	hyper_bomb_x = ThreadLocalRandom.current().nextInt(1, X_AXIS_TILES);
        	hyper_bomb_y = ThreadLocalRandom.current().nextInt(1, Y_AXIS_TILES);
        	Hyper_Tile tile = new Hyper_Tile(hyper_bomb_x-1, hyper_bomb_y-1, true, difficulty_level);
    		grid[hyper_bomb_x-1][ hyper_bomb_y-1] = tile;
            root.getChildren().add(tile);
            remaining_bombs--;
            remaining_TILES--;
            System.out.println(remaining_bombs);
            System.out.println(remaining_TILES);
            System.out.println(hyper_bomb_x);
            System.out.println(hyper_bomb_y);
        }
        

        bombs_location = new ArrayList<Coordinate>();
               
        boolean will_have_bomb = false;
        boolean hyper = false;
        for (int x = 0; x < X_AXIS_TILES; x++) {
            for (int y = 0; y < Y_AXIS_TILES; y++) {
            	if(remaining_TILES - remaining_bombs <= 0) { //if the number of remaining bombs equals the number of remaining tiles put bombs to all the rest tiles
            		will_have_bomb = true;
            	}
            	else if(remaining_bombs > 0){ //if the are still bombs to place
                	will_have_bomb = (Math.random() < (float)number_of_bombs/(X_AXIS_TILES*Y_AXIS_TILES)); // place bomb with probability (number of bombs)/(Total number of Tiles)
            	}
            	else { //if all bombs are placed
            		will_have_bomb = false;
            	}
            	
            	hyper = (((x+1)==hyper_bomb_x) && ((y+1)==hyper_bomb_y)); 
            	if(hyper == true){
            		System.out.println(x);
            		System.out.println(y);
            	}
//            	if(difficulty_level == 2 && hyper_bomb && hyper) { // if this Tile contains hyper bomb
//            		Hyper_Tile tile = new Hyper_Tile(x, y, hyper, difficulty_level);
//            		grid[x][y] = tile;
//                    root.getChildren().add(tile);
//            	}
//            	else if(!hyper){ // if this Tile does not contain hyper bomb, add normal Tile
//            		Tile tile = new Tile(x, y, will_have_bomb, difficulty_level);
//            		grid[x][y] = tile;
//                    root.getChildren().add(tile);
//            	}
            	if(!hyper) {
	            	Tile tile = new Tile(x, y, will_have_bomb, difficulty_level);
	        		grid[x][y] = tile;
	                root.getChildren().add(tile);
	            	remaining_TILES--; //decrease remaining Tiles
            	}
                if(difficulty_level == 2 && hyper_bomb && hyper) {
//                	remaining_bombs--; // decrease number of bombs if hyper bomb was added
                	bombs_location.add(new Coordinate(x, y, hyper)); //save bomb location
                } 
                else if(will_have_bomb && !hyper) {
                	remaining_bombs--; // decrease number of bombs if bomb was added
                	bombs_location.add(new Coordinate(x, y)); //save bomb location
                }  
                hyper = false; // set hyper to false
            }
        }
        
    	try {
    		new FileWriter("output\\mines.txt", false).close();  // delete previous content of mines.txt
    		FileWriter fstream = new FileWriter("output\\mines.txt");  // save bomb locations to mines.txt
			BufferedWriter out = new BufferedWriter(fstream);
    		for(Coordinate i: bombs_location) { // read bomb locations one by one
				if(difficulty_level == 2 && hyper_bomb) { //if there was a hyper bomb
					out.write(i.getX() + "," + i.getY() + "," + i.getHyperBomb() + "\n");
					
		    		System.out.println(i.toString_hyper());
				}
				else { // modes without hyper bomb
					out.write(i.getX() + "," + i.getY() + "\n");
					System.out.println(i.toString());
				}
    		}
    		out.close();
    	}catch (Exception e){
    		System.err.println("Error: " + e.getMessage());
    	}

        for (int y = 0; y < Y_AXIS_TILES; y++) {
            for (int x = 0; x < X_AXIS_TILES; x++) {
                Tile tile = grid[x][y];

                if (tile.Bomb) {
                    tile.border.setFill(Color.RED);
                    continue;
                }
                
        		List<Tile> neighbors_help = getNeighbors(tile); // find neighbors of every Tile
        		int bombs = 0;		
        		for (Tile neighbor : neighbors_help) { // Save neighbors' number of bombs
        		    if(neighbor.Bomb) { 
        		    	bombs += 1;
        		    	
        		    }
        		}		

                if (bombs > 0) { // Set Text colour depending on number of neighbors' bombs
                    tile.number_of_bombs = bombs;
                  	tile.text.setText(String.valueOf(tile.number_of_bombs));
                  	if(bombs == 3) // 3 neighbors with bomb -> RED color Text
                  		tile.text.setFill(Color.RED); 
                  	else if(bombs == 2) // 2 neighbors with bomb -> GREEN color Text
                  		tile.text.setFill(Color.GREEN); 
                  	else // 1 neighbors with bomb -> BLUE color Text
                  		tile.text.setFill(Color.BLUE); 
                }
            }
        }
    }
    
    private List<Tile> getNeighbors(Tile tile) { // find neighbors of every Tile
        List<Tile> neighbors = new ArrayList<>();

        int x_neigh[] = new int[3];
        x_neigh[0] = -1; //left column
        x_neigh[1] = 0; //same column
        x_neigh[2] = 1; // right column
        
        int y_neigh[] = new int[3];
        y_neigh[0] = -1; // previous row 
        y_neigh[1] = 0; // same row 
        y_neigh[2] = 1; // next row

        for (int i = 0; i < x_neigh.length; i++) {
            int x_axis_neigh = x_neigh[i];
            for (int j = 0; j < y_neigh.length; j++) {
            	int y_axis_neigh = y_neigh[j];
            	int x_ax = tile.x+x_axis_neigh; // set coordinates of neighbor Tile
            	int y_ax = tile.y+y_axis_neigh;
	            if (x_ax >= 0 && x_ax < X_AXIS_TILES && (x_axis_neigh != 0 || y_axis_neigh !=0)) { // if not the same Tile and not outside grid
	            	if(y_ax >= 0 && y_ax < Y_AXIS_TILES) {
	            		neighbors.add(grid[x_ax][y_ax]);  // add neighbor
                    }
            	}
            }
        }
        
        tile.neighbors = neighbors; // save neighbors
        
        return neighbors;
    }
    
    public Tile[][] getGrid() { // return grid with Tiles
    	return grid;
    }
    
    public List<Coordinate> getBombLocation() { // return bomb locations
    	return bombs_location;
    }
    
    public GridPane getBoard() // return GridPane with Tiles
    {
        return root;
    }
}

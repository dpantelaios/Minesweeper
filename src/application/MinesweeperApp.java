package application;


import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import classes.Coordinate;
import application.Tile;
import application.Hyper_Tile;
import classes.InvalidDescriptionException;
import classes.InvalidValueException;
//import javax.swing.event.ChangeListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.*;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter; 

public class MinesweeperApp extends Application {

    private static final int TILE_SIZE = 40;
    private int X_TILES = 16;
    private int Y_TILES = 16;
    private int W = X_TILES*40;
    private int H = Y_TILES*40+200;

    protected static Tile[][] grid;
    private Scene scene;
    
    TextField timeleft = new TextField();
    TextField numberofbombs  = new TextField();
    public static TextField markedbombs = new TextField();
    private static int difficulty_level = 0;
    private int number_of_bombs;
    private boolean hyper_bomb = false;
    private int time_left_in_seconds = 0;
    private int del = 0;
    private boolean timer_running = false;
    public static Timer tm;
    private int hyper_bomb_x = 0, hyper_bomb_y = 0;
    private List<Coordinate> bombs_location;
    GridPane root;
    
    public static int total_moves = 0;
    
    private Parent createContent() {

        Menu menu = new Menu("Basic Functions");
    	Menu menu_details = new Menu("Details");

    	MenuItem create = new MenuItem("Create");
        MenuItem start = new MenuItem("Start");
        MenuItem load = new MenuItem("Load");
        MenuItem exit = new MenuItem("Exit");
        
        MenuItem round = new MenuItem("Round");
        MenuItem solution = new MenuItem("Solution");
        
        menu.getItems().addAll(create, start, load, exit);
        menu_details.getItems().addAll(round, solution);
               
        // create menu bar
        MenuBar menuBar = new MenuBar();    
        menuBar.getMenus().addAll(menu, menu_details);
             
        exit.setOnAction(e->{
        	if(timer_running) {
        		tm.cancel();
        	}
        	Platform.exit();});
        
        create.setOnAction(e -> { create_mode(); });
        
        load.setOnAction(e -> { load_mode(); });
        
        start.setOnAction(f -> { start(); });    	
    	
        solution.setOnAction(e -> { show_solution(); });
        root = new GridPane();
        
        BorderPane border_root = new BorderPane();
        border_root.setTop(menuBar); // install menu bar
        
        root.setPrefSize(W, H);
        numberofbombs.setEditable(false);
        markedbombs.setEditable(false);
        timeleft.setEditable(false);
        
        Label label1 = new Label("BOMBS");
        HBox number_of_bombs_hb = new HBox();
        number_of_bombs_hb.getChildren().addAll(label1, numberofbombs);
        root.add(number_of_bombs_hb, 1, 1);
        
        Label label2 = new Label("MARKED BOMBS");
        HBox marked_bombs_hb = new HBox();
        marked_bombs_hb.getChildren().addAll(label2, markedbombs);
        root.add(marked_bombs_hb, 2, 1);

        Label label3 = new Label("TIMER");
        HBox timer_hb = new HBox();
        timer_hb.getChildren().addAll(label3, timeleft);
        root.add(timer_hb, 3, 1);
     
        border_root.setCenter(root);
        
        return border_root;
    }
     
    private void start() {
    	if(timer_running) { //if starts is being pressed while playing
//    		Platform.exit();
    		tm.cancel();
    		timer_running = false;
    		root.getChildren().clear();
    		Stage test_stage = new Stage();
    		try {
				start(test_stage);
				start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   	
		}
    	else {
    		timer_running = true;
	    	total_moves = 0;
	    	if(difficulty_level == 2) {
	    		X_TILES = 16;
	    		Y_TILES = 16;
	    	}
	    	else if(difficulty_level == 1) {
	    		X_TILES = 9;
	    		Y_TILES = 9;    		
	    	}
	    	W = X_TILES*40;
	        H = Y_TILES*40+200;
	        
	        numberofbombs.setText(String.valueOf(number_of_bombs));
	        markedbombs.setText("0");
	        timeleft.setText(String.valueOf(time_left_in_seconds));
	        timer_running = true;
	        tm = new Timer();        
	        
	        tm.scheduleAtFixedRate(new TimerTask(){
	        	//override run method
	        	@Override
	        	public void run() {
		        	//print a message notifying about timer
		        	System.out.println("Timer works. . . .");
		        	String time = timeleft.getText();
		    		int time_int = Integer.parseInt(time);
		    		time_int --;
		    		System.out.println(time_int);
		    		timeleft.setText(String.valueOf(time_int));
		    		if(time_int <= 0) {
		    			tm.cancel();
		    			timer_running=false;
		    		}
	        	}
	    	}, 1000, 1000);
	        
	        grid = new Tile[X_TILES][Y_TILES];
	        int remaining_bombs, remaining_tiles;
	        
	        if(difficulty_level == 2 && hyper_bomb) {
	        	hyper_bomb_x = ThreadLocalRandom.current().nextInt(0, X_TILES - 1);
	        	hyper_bomb_y = ThreadLocalRandom.current().nextInt(0, Y_TILES - 1);
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
	            		Hyper_Tile tile = new Hyper_Tile(x, y, hyper, X_TILES, Y_TILES);
	            		grid[x][y] = tile;
	                    root.getChildren().add(tile);
	            	}
	            	else {
	            		Tile tile = new Tile(x, y, will_have_bomb, X_TILES, Y_TILES);
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
	                	//System.out.println(String.valueOf(bombs));
	                    tile.number_of_bombs = bombs;
	                  	tile.text.setText(String.valueOf(tile.number_of_bombs));
	                }
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
      

    @Override
    public void start(Stage primaryStage) throws Exception {
   
        scene = new Scene(createContent());

        primaryStage.setScene(scene);
        primaryStage.setTitle("MediaLab Minesweeper");
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setResizable(false);
        
        primaryStage.setOnCloseRequest(event -> {
        	if(timer_running) {
        		tm.cancel();
        	}
        	Platform.exit();
    	});
        
        primaryStage.show();
    }
    
    private void create_mode()
    {
        Stage stage = new Stage();
        stage.setTitle("Create and Save new game mode");
        stage.setResizable(false);
        
        Label label1 = new Label("Scenario ID:");        
        TextField scenario_id = new TextField();
        HBox scenario_id_hb = new HBox();
        scenario_id_hb.getChildren().addAll(label1, scenario_id);
        
        Label label2 = new Label("Difficulty Level:"); 
        ChoiceBox difficulty_level = new ChoiceBox(FXCollections.observableArrayList("1", "2"));
        HBox difficulty_level_hb = new HBox();
        difficulty_level_hb.getChildren().addAll(label2, difficulty_level);
        //System.out.println(difficulty_level.getValue());
        
        Label label3 = new Label("Number of Bombs:"); 
        ChoiceBox number_of_bombs = new ChoiceBox();
        HBox number_of_bombs_hb = new HBox();
        number_of_bombs_hb.getChildren().addAll(label3, number_of_bombs);
        
        Label label4 = new Label("Hyper bomb:"); 
        ChoiceBox hyper_bomb =  new ChoiceBox();
        HBox hyper_bomb_hb = new HBox();
        hyper_bomb_hb.getChildren().addAll(label4, hyper_bomb);
        
        Label label5 = new Label("Time Left(sec):"); 
        ChoiceBox time_left = new ChoiceBox();
        HBox time_left_hb = new HBox();
        time_left_hb.getChildren().addAll(label5, time_left);
        
        difficulty_level.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            // if items of the list are changed
            public void changed(ObservableValue ov, Number value, Number new_value) {
            	
            	if(new_value.intValue() == 0) {
            		number_of_bombs.getItems().removeAll(number_of_bombs.getItems());
            		for(int i=9; i<12; i++) {
            			number_of_bombs.getItems().add(i);
            		}
            		
            		hyper_bomb.getItems().removeAll(hyper_bomb.getItems());
            		hyper_bomb.getItems().add(0);
            		
            		time_left.getItems().removeAll(time_left.getItems());
            		for(int i=12; i<19; i++) {
            			time_left.getItems().add(i*10);
            		}
            	}
            	else if(new_value.intValue() == 1) {
            		number_of_bombs.getItems().removeAll(number_of_bombs.getItems());
            		for(int i=35; i<46; i++) {
            			number_of_bombs.getItems().add(i);
            		}  
            		
            		hyper_bomb.getItems().removeAll(hyper_bomb.getItems());
            		hyper_bomb.getItems().addAll(0, 1);
            		
            		time_left.getItems().removeAll(time_left.getItems());
            		for(int i=24; i<37; i++) {
            			time_left.getItems().add(i*10);
            		}
            	}
            	//System.out.println(new_value.intValue());
            }
        });
        
        Button create = new Button("Create");
        create.setOnAction(action -> {
        	try{
        		if(!scenario_id.getText().isEmpty() && difficulty_level.getValue() != null && number_of_bombs.getValue() != null && hyper_bomb.getValue() != null && time_left.getValue() != null) {
        			new FileWriter("medialab\\SCENARIO-" + scenario_id.getText() + ".txt", false).close();
	        		FileWriter fstream = new FileWriter("medialab\\SCENARIO-" + scenario_id.getText() + ".txt");
	        		BufferedWriter out = new BufferedWriter(fstream);
	        		out.write(difficulty_level.getValue() + "\n");
	        		out.write(number_of_bombs.getValue() + "\n");
	        		out.write(time_left.getValue() + "\n");
	        		out.write(hyper_bomb.getValue() + "\n");
	        		out.close();
        		}
        		else {
        			System.out.println("error");
        		}
        	}catch (Exception e){
        		System.err.println("Error: " + e.getMessage());
        	}
        });
        
        Button close = new Button("Close");
        close.setOnAction(bar -> {stage.close();});
        
        HBox buttons = new HBox();
        buttons.getChildren().addAll(create, close);
        
        VBox vb = new VBox();
        vb.getChildren().addAll(scenario_id_hb, difficulty_level_hb, number_of_bombs_hb, hyper_bomb_hb, time_left_hb, buttons);
        vb.setSpacing(5);
        
        Scene popup = new Scene(vb, 270, 200);
        stage.setScene(popup);
        stage.show();
    }
    
    
    private void load_mode()
    {
        Stage stage = new Stage();
        stage.setTitle("Load mode");
        stage.setResizable(false);
        
        Label label1 = new Label("Scenario ID:");        
        ChoiceBox scenario_id = new ChoiceBox();
        HBox scenario_id_hb = new HBox();
        scenario_id_hb.getChildren().addAll(label1, scenario_id);
        
        File directoryPath = new File("medialab");
        //List of all files and directories
        scenario_id.getItems().removeAll(scenario_id.getItems());
        String contents[] = directoryPath.list();
        for(int i=0; i<contents.length; i++) {
           //System.out.println(contents[i]);
           String[] arrOfStr = contents[i].split("-", 2);
           String[] keep_id = arrOfStr[1].split(".t", 2);
           //System.out.println(keep_id[0]);
           scenario_id.getItems().add(keep_id[0]);
        }        
   
        Button load = new Button("Load");
        load.setOnAction(action -> {
        	try{	    
        		if(scenario_id.getValue() != null) {
        			File myObj = new File("medialab\\SCENARIO-" + scenario_id.getValue() + ".txt");
        			Scanner myReader = new Scanner(myObj);
        			int count_lines = 0;
        			while (myReader.hasNextLine()) {
        				String data = myReader.nextLine();
        				if(count_lines == 0) {
        					difficulty_level = Integer.parseInt(data);
        					if(difficulty_level != 1 && difficulty_level != 2) {
        						//System.out.println("Throw InvalidValueException");
        						throw new InvalidValueException();
        					}
        				} 
        				else if(count_lines == 1) {
        					number_of_bombs = Integer.parseInt(data);
        					if(difficulty_level == 1 && (number_of_bombs < 9 || number_of_bombs > 11)) {
        						//System.out.println("Throw InvalidValueException 1");
        						throw new InvalidValueException();
        					}
        					else if(difficulty_level == 2 && (number_of_bombs < 35 || number_of_bombs > 45)) {
//        						System.out.println("Throw InvalidValueException");
        						throw new InvalidValueException();
        					}
        				}  
        				else if(count_lines == 2) {
        					time_left_in_seconds = Integer.parseInt(data);
        					if(difficulty_level == 1 && (time_left_in_seconds < 120 || time_left_in_seconds > 180)) {
//        						System.out.println("Throw InvalidValueException2");
        						throw new InvalidValueException();
        					}
        					else if(difficulty_level == 2 && (time_left_in_seconds < 240 || time_left_in_seconds > 360)) {
//        						System.out.println("Throw InvalidValueException");
        						throw new InvalidValueException();
        					}
        				}  
        				else if(count_lines == 3) {
        					int help = Integer.parseInt(data);
        					hyper_bomb =  (help == 1);
        					if(difficulty_level == 1 && hyper_bomb == true) {
//        						System.out.println("Throw InvalidValueException3");
        						throw new InvalidValueException();
        					}
        				}  
        				else {
        					//throw exception
//        					System.out.println("Throw InvalidDescriptionException");
        					throw new InvalidDescriptionException();
        				}
        				count_lines++;
        			}
        			myReader.close();
        		}
        		else {
        			System.out.println("error");
        		}	
        	} catch (FileNotFoundException e) {
        	      System.out.println("An error occurred.");
        	      e.printStackTrace();
        	}
        	catch (Exception e) {
        		if(e instanceof InvalidValueException) {
	      	      System.out.println("Invalid Value Exception.");
	      	      e.printStackTrace();
        		}
        		if(e instanceof InvalidDescriptionException) {
        			System.out.println("Invalid Description Exception.");
        			e.printStackTrace();
        		}
        	}
        });
        
        
        Button close = new Button("Close");
        close.setOnAction(bar -> {stage.close();});
        
        HBox buttons = new HBox();
        buttons.getChildren().addAll(load, close);
        
        VBox vb = new VBox();
        vb.getChildren().addAll(scenario_id_hb, buttons);
        vb.setSpacing(5);
        
        Scene popup = new Scene(vb, 270, 100);
        stage.setScene(popup);
        stage.show();
    }
    
    private void show_solution()
    {
		System.out.println("X_AXIS, Y_AXIS");
    	for(Coordinate i: bombs_location) {
    		int x_axis = i.getX()-1;
    		int y_axis = i.getY()-1;
    		System.out.print(x_axis);
    		System.out.print("  ");
    		System.out.println(y_axis);
    		if(i.getHyperBomb()) {
    			grid[x_axis][y_axis].open();
    			Hyper_Tile test = (Hyper_Tile) grid[x_axis][y_axis];
    			test.open();
    		}
    		else {
    			grid[x_axis][y_axis].hyper_open();
    		}
		}
    	if(timer_running) {
    		tm.cancel();
    	}
    }
    
    public static TextField getMarkedBombes(){
        return markedbombs;
    }
    
    public static Timer getTimer(){
        return tm;
    }
    
    public static Tile[][] getGrid(){
        return grid;
    }
    
    public static int getDifficulty_level(){
        return difficulty_level;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
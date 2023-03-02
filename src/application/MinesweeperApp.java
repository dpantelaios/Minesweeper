package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import classes.Coordinate;
import classes.Round_result;
import classes.InvalidDescriptionException;
import classes.InvalidValueException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.*;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter; 

public class MinesweeperApp extends Application {
    private int X_TILES = 16;
    private int Y_TILES = 16;
    private int W = 16*40;
    private int H = 16*40+200;

    protected static Tile[][] grid;
    private Scene scene;
    
    TextField timeleft = new TextField();
    TextField numberofbombs  = new TextField();
    public static TextField markedbombs = new TextField();
    private static int difficulty_level = 0;
    private int number_of_bombs;
    private boolean hyper_bomb = false, valid_data = false;
    private int time_left_in_seconds = 0;
    private boolean timer_running = false;
    public static Timer tm;
    private List<Coordinate> bombs_location;
    private MinesweeperBoard board;
    private boolean something_already_drawn = false;
    private Stage primaryStage = new Stage();
    protected GridPane root;
    
    private ArrayList<Round_result> results = new ArrayList<Round_result>(); 
    private int total_results = 0;
    public static int total_moves = 0;
    private int tiles_to_win = X_TILES*Y_TILES;
    
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
        round.setOnAction(e -> { show_results(); });
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
		if(difficulty_level == 2) {
    		X_TILES = 16;
    		Y_TILES = 16;
    	}
    	else if(difficulty_level == 1) {
    		X_TILES = 9;
    		Y_TILES = 9;    		
    	}
		tiles_to_win = X_TILES*Y_TILES - number_of_bombs; //i have to reveal these many tiles to win
		
    	if(something_already_drawn && valid_data) { //if starts is being pressed while playing
//    		Platform.exit();
    		tm.cancel();
    		timer_running = false;
    		something_already_drawn = false;
    		root.getChildren().clear();
    		//Stage test_stage = new Stage();
    		try {
				start(primaryStage);
				start();
			} catch (Exception e) {
				e.printStackTrace();
			}   	
		}
    	else if (valid_data) {
    		timer_running = true;
    		something_already_drawn = true;
	    	total_moves = 0;
	        
	        board = new MinesweeperBoard(difficulty_level, hyper_bomb, number_of_bombs);
	        
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
		    			bomb_pressed();
		    		}
	        	}
	    	}, 1000, 1000);
	        
	        grid = board.getGrid();
	        bombs_location = board.getBombLocation();      
	        GridPane help_root = board.getBoard();
	        root.getChildren().add(help_root);
	        
	        int i=0, j=0;
	        for(i=0; i<X_TILES; i++) {
		        for(j=0; j<Y_TILES; j++) {
		            final int innerI = i;
		            final int innerJ = j;
		        	grid[i][j].setOnMouseClicked(event -> 
			        {
			        	MouseButton button = event.getButton();
			        	if (button == MouseButton.PRIMARY) {
			        		boolean previous_open = grid[innerI][innerJ].getOpen();
			        		grid[innerI][innerJ].left_click(total_moves);
			        		total_moves++;
			        		boolean opened = grid[innerI][innerJ].getOpen();
			        		boolean bomb_press = (!previous_open && opened && grid[innerI][innerJ].hasBomb);  //opened now and has bomb
			        		if(bomb_press) { bomb_pressed(); }
			        		tiles_to_win -= grid[innerI][innerJ].getTilesRevealed();
			        		if(tiles_to_win == 0) {
			        			winning();
			        		}
			        	}
			        	else if (button == MouseButton.SECONDARY){
			        		if(Integer.parseInt(markedbombs.getText()) < number_of_bombs || grid[innerI][innerJ].getMarkedBombs()){
				        		boolean opened = grid[innerI][innerJ].getOpen(), marked = grid[innerI][innerJ].getMarkedBombs();
				        		int added_marked_bombs=0;
				        		if(!opened && !marked && (total_moves<5) && hyper_bomb && innerI == board.hyper_bomb_x-1 && innerJ == board.hyper_bomb_y-1) { //hyper_bomb not opened, not marked with a flag, less than 5 moves => open row and column of hyper_bomb
				        			added_marked_bombs = 0;
				        			hyper_bomb_found_in_5_moves(innerI, innerJ);
				        		}
				        		grid[innerI][innerJ].right_click(total_moves);
				        		if(!opened && marked) //it was marked with a flag before, so now it's not
				        			added_marked_bombs = -1;
				        		else if(!opened && !marked) //it wasn't marked with a flag before, now it is
				        			added_marked_bombs = +1;
				        		int marked__bombs = Integer.parseInt(markedbombs.getText());
				        		markedbombs.setText(String.valueOf((marked__bombs + added_marked_bombs)));
			        		}
			        	}
			        });
		        }
	        }
	    
    	}
    	else {
    		Alert a = new Alert(AlertType.INFORMATION);
    		a.setHeaderText("Invalid or No Data");
            a.show();
    	}
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
//        ChoiceBox time_left = new ChoiceBox();
        HBox time_left_hb = new HBox();
        Slider time_left = new Slider();
		time_left.setBlockIncrement(1);
//		time_left.setShowTickMarks(true);
		time_left.setShowTickLabels(true);
		time_left.setMajorTickUnit(1);
		time_left.setMinorTickCount(0);
		time_left.setShowTickLabels(true);
		time_left.setSnapToTicks(true);
		time_left.setPrefWidth(500);
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
            		
            		time_left.setMin(120);
            		time_left.setMax(180);
            	}
            	else if(new_value.intValue() == 1) {
            		number_of_bombs.getItems().removeAll(number_of_bombs.getItems());
            		for(int i=35; i<46; i++) {
            			number_of_bombs.getItems().add(i);
            		}  
            		
            		hyper_bomb.getItems().removeAll(hyper_bomb.getItems());
            		hyper_bomb.getItems().addAll(0, 1);

            		time_left.setMin(240);
            		time_left.setMax(360);
            	}
            }
        });
        
        Button create = new Button("Create");
        create.setOnAction(action -> {
        	try{
        		if(!scenario_id.getText().isEmpty() && difficulty_level.getValue() != null && number_of_bombs.getValue() != null && hyper_bomb.getValue() != null) {
        			new FileWriter("medialab\\SCENARIO-" + scenario_id.getText() + ".txt", false).close();
	        		FileWriter fstream = new FileWriter("medialab\\SCENARIO-" + scenario_id.getText() + ".txt");
	        		BufferedWriter out = new BufferedWriter(fstream);
	        		out.write(difficulty_level.getValue() + "\n");
	        		out.write(number_of_bombs.getValue() + "\n");
	        		out.write((int)time_left.getValue() + "\n");
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
        
        Scene popup = new Scene(vb, 520, 200);
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
        						throw new InvalidValueException();
        					}
        				} 
        				else if(count_lines == 1) {
        					number_of_bombs = Integer.parseInt(data);
        					if(difficulty_level == 1 && (number_of_bombs < 9 || number_of_bombs > 11)) {
        						throw new InvalidValueException();
        					}
        					else if(difficulty_level == 2 && (number_of_bombs < 35 || number_of_bombs > 45)) {
        						throw new InvalidValueException();
        					}
        				}  
        				else if(count_lines == 2) {
        					time_left_in_seconds = Integer.parseInt(data);
        					if(difficulty_level == 1 && (time_left_in_seconds < 120 || time_left_in_seconds > 180)) {
        						throw new InvalidValueException();
        					}
        					else if(difficulty_level == 2 && (time_left_in_seconds < 240 || time_left_in_seconds > 360)) {
        						throw new InvalidValueException();
        					}
        				}  
        				else if(count_lines == 3) {
        					int help = Integer.parseInt(data);
        					hyper_bomb =  (help == 1);
        					if(difficulty_level == 1 && hyper_bomb == true) {
        						throw new InvalidValueException();
        					}
        				}  
        				else {
        					throw new InvalidDescriptionException();
        				}
        				count_lines++;
        				valid_data = true;
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
	      	      valid_data = false;
	      	      e.printStackTrace();
        		}
        		if(e instanceof InvalidDescriptionException) {
        			System.out.println("Invalid Description Exception.");
        			valid_data = false;
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
    	if(something_already_drawn && timer_running) {
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
	    	Round_result helper = new Round_result(number_of_bombs, total_moves, time_left_in_seconds - Integer.parseInt(timeleft.getText()), "Computer");
	    	total_results++;
	    	results.add(helper);
	    	if(total_results > 5) {
	    		results.remove(0);
	    	}
    	}
    }
    
   private void show_results() {
	   Stage stage = new Stage();
       stage.setTitle("Rounds");
       stage.setResizable(false);


       Label title_first = new Label("TITLES");
       title_first.setMinWidth(53);
       Button column [] = new Button[4];
       column[0] = new Button("Number of Bombs");
       column[1] = new Button("Total Moves");
       column[2] = new Button("Total Time");
       column[3] = new Button("Winner");
       
       HBox scenario_id_hb_first = new HBox();
       scenario_id_hb_first.getChildren().addAll(title_first, column[0], column[1], column[2], column[3]);

       VBox vb = new VBox();
       vb.getChildren().addAll(scenario_id_hb_first);
       Scene popup = new Scene(vb, 600, 220);
       stage.setScene(popup);
       stage.show();
       
       Label titles [] = new Label[5];
       for(int i=0; i<5; i++) {
    	   titles[i] = new Label("ROUND " + Integer.toString(i+1));
       }

       Button a[] = new Button[20];
       double width = 0;
       for(int i =0; i<4; i++) {
    	   width = column[i].getWidth();
    	   for(int j = i; j<20; j+=4) {
        	   a[j] = new Button("-");
        	   a[j].setMinWidth(width);
    	   }
       }
       
       int counter = 0;
       for(Round_result result : results) {
		   a[counter*4].setText(Integer.toString(result.getBombs()));
		   a[counter*4+1].setText(Integer.toString(result.getTotal_moves()));
		   a[counter*4+2].setText(Integer.toString(result.getTotal_time()));
		   a[counter*4+3].setText(result.getWinner());
		   counter++;
       }

       HBox scenario_id_hb = new HBox();
       scenario_id_hb.getChildren().addAll(titles[0] , a[0], a[1], a[2], a[3]);

       HBox scenario_id_hb2 = new HBox();
       scenario_id_hb2.getChildren().addAll(titles[1], a[4], a[5], a[6], a[7]);

       HBox scenario_id_hb3 = new HBox();
       scenario_id_hb3.getChildren().addAll(titles[2], a[8], a[9], a[10], a[11]);

       HBox scenario_id_hb4 = new HBox();
       scenario_id_hb4.getChildren().addAll(titles[3], a[12], a[13], a[14], a[15]);

       HBox scenario_id_hb5 = new HBox();
       scenario_id_hb5.getChildren().addAll(titles[4], a[16], a[17], a[18], a[19]);
     
       Button close = new Button("Close");
       close.setOnAction(bar -> {stage.close();});
     
       vb.getChildren().addAll(scenario_id_hb, scenario_id_hb2, scenario_id_hb3, scenario_id_hb4, scenario_id_hb5, close);
       vb.setSpacing(5);
	}
   
   private void winning() {
       for(int i=0; i<X_TILES; i++) {
    	   for(int j=0; j<Y_TILES; j++) {
    		   grid[i][j].setDisable(true);
    	   }
       }
       tm.cancel();
       
       Round_result helper = new Round_result(number_of_bombs, total_moves, time_left_in_seconds - Integer.parseInt(timeleft.getText()), "Player");
   		total_results++;
   		results.add(helper);
   		if(total_results > 5) {
   			results.remove(0);
   		}
   		timer_running = false;
       Alert a = new Alert(AlertType.INFORMATION);
       a.setHeaderText("YOU WON, PRESS START TO PLAY AGAIN");
       a.show();
   }
   
   private void bomb_pressed() {
       for(int i=0; i<X_TILES; i++) {
    	   for(int j=0; j<Y_TILES; j++) {
    		   grid[i][j].setDisable(true);
    	   }
       }
       tm.cancel();
       
       Round_result helper = new Round_result(number_of_bombs, total_moves, time_left_in_seconds - Integer.parseInt(timeleft.getText()), "Computer");
   		total_results++;
   		results.add(helper);
   		if(total_results > 5) {
   			results.remove(0);
   		}
   		timer_running = false;
       Alert a = new Alert(AlertType.INFORMATION);
       a.setHeaderText("YOU LOST, PRESS START TO TRY AGAIN");
       a.show();
   }
   
    private void hyper_bomb_found_in_5_moves(int x, int y) {
		grid[board.hyper_bomb_x-1][board.hyper_bomb_y-1].setTilesRevealed();
    	for(int i=0; i<X_TILES; i++) {
			if(i!=y)
				grid[x][i].hyper_open();
		}
		for(int j=0; j<Y_TILES; j++) {
			if(j!=x)
				grid[j][y].hyper_open();
		}
		tiles_to_win -= grid[board.hyper_bomb_x-1][board.hyper_bomb_y-1].getTilesRevealed();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
   
        scene = new Scene(createContent());

        this.primaryStage.setScene(scene);
        this.primaryStage.setTitle("MediaLab Minesweeper");
        this.primaryStage.setResizable(false);
        
        this.primaryStage.setOnCloseRequest(event -> {
        	if(timer_running) {
        		tm.cancel();
        	}
        	Platform.exit();
    	});
        
        this.primaryStage.show();
    }
     
    public static void main(String[] args) {
        launch(args);
    }
}
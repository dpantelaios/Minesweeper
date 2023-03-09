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
    private int X_AXIS_TILES = 16; // number of tiles on x axis
    private int Y_AXIS_TILES = 16; // number of tiles on y axis
    private int W = 16*40;  // gridPane width
    private int H = 16*40+200; // GridPane height

    protected static Tile[][] grid;
    private Scene scene;
    
    private TextField timeleft = new TextField();
    private TextField numberofbombs  = new TextField();
    private static TextField markedbombs = new TextField();
    private static int difficulty_level = 0, difficulty_level_temp = 0;
    private int number_of_bombs=0, number_of_bombs_temp=0;
    private boolean hyper_bomb = false, hyper_bomb_temp = false, valid_data = false;
    private int time_left_in_seconds = 0, time_left_in_seconds_temp = 0;
    private boolean timer_running = false, solution_revealed = false;
    public static Timer tm;
    private List<Coordinate> bombs_location;
    private MinesweeperBoard board;
    private boolean something_already_drawn = false;
    private Stage primaryStage = new Stage();
    protected GridPane root;
    
    private ArrayList<Round_result> results = new ArrayList<Round_result>(); 
    private int total_results = 0;
    public static int total_moves = 0;
    private int tiles_to_win = X_AXIS_TILES*Y_AXIS_TILES;
    
    private Parent createContent() {
    	// define two menus
        Menu menu = new Menu("Basic Functions");
    	Menu menu_details = new Menu("Details");
    	
    	// define menuItems of menu Basic Function
    	MenuItem create = new MenuItem("Create");
        MenuItem start = new MenuItem("Start");
        MenuItem load = new MenuItem("Load");
        MenuItem exit = new MenuItem("Exit");
        // define menuItems of menu Details
        MenuItem round = new MenuItem("Round");
        MenuItem solution = new MenuItem("Solution");
        
        menu.getItems().addAll(create, start, load, exit); //add menuItems to menu Basic Function
        menu_details.getItems().addAll(round, solution); //add menuItems to menu Details
               
        // create menu bar
        MenuBar menuBar = new MenuBar();    
        menuBar.getMenus().addAll(menu, menu_details); //add menus to menu Bar
             
        exit.setOnAction(e->{  // when exit is pressed close platform and stop timer if it is in operation
        	if(timer_running) {
        		tm.cancel();
        	}
        	Platform.exit();});
        
        create.setOnAction(e -> { create_mode(); });  //call the corresponding function for every menuItem selected
        load.setOnAction(e -> { load_mode(); });
        start.setOnAction(f -> { start(); });    	
        solution.setOnAction(e -> { show_solution(); });
        round.setOnAction(e -> { show_results(); });
        root = new GridPane();
        
        BorderPane border_root = new BorderPane();
        border_root.setTop(menuBar); // install menu bar
        
        root.setPrefSize(W, H); // set size of gridPane
        numberofbombs.setEditable(false); // we cant edit textfields numberofbombs, markedbombs and timeleft
        markedbombs.setEditable(false);
        timeleft.setEditable(false);
        
        Label label1 = new Label("BOMBS");  // Set labels for TextFields
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
		difficulty_level = difficulty_level_temp;
        number_of_bombs = number_of_bombs_temp;
        hyper_bomb = hyper_bomb_temp;
        time_left_in_seconds = time_left_in_seconds_temp;
		if(difficulty_level == 2) {  // Set number of Tiles depending on the difficult level given
    		X_AXIS_TILES = 16;
    		Y_AXIS_TILES = 16;
    	}
    	else if(difficulty_level == 1) {
    		X_AXIS_TILES = 9;
    		Y_AXIS_TILES = 9;    		
    	}
		tiles_to_win = X_AXIS_TILES*Y_AXIS_TILES - number_of_bombs; //i have to reveal these many tiles to win
		
    	if(something_already_drawn && valid_data) { //if start is being pressed while playing and the data given are valid
    		tm.cancel(); //cancel running timer of previous execution
    		timer_running = false; // set boolean so we know that there is no timer running from now on
    		something_already_drawn = false; // set boolean so we know there is no Board drawn from now on
    		root.getChildren().clear(); // clear GridPane from tiles and every component
    		try {
				start(primaryStage);  // set basic components in the new stage with createContent function
				start(); 
			} catch (Exception e) {
				e.printStackTrace();
			}   	
		}
    	else if (valid_data) {  // if the data given are valid and there isn't something already drawn
    		timer_running = true; //set boolean equal to true in order to know that a timer is running 
    		something_already_drawn = true; //set boolean equal to true in order to know that something is already drawn
    		solution_revealed = false; //used in order to not reveal same solution more than 1 time
	    	total_moves = 0; // set total moves to zero
	        
	        board = new MinesweeperBoard(difficulty_level, hyper_bomb, number_of_bombs); // create new board with the appropriate number of Tiles
	        
	        numberofbombs.setText(String.valueOf(number_of_bombs));  // Set Textfield with information given by chosen game mode
	        markedbombs.setText("0");
	        timeleft.setText(String.valueOf(time_left_in_seconds));
	        tm = new Timer();   // create new timer   
	        
	        tm.scheduleAtFixedRate(new TimerTask(){ // get Textfield timeleft value, decrease it by 1 and display the new value
	        	//override run method
	        	@Override
	        	public void run() {
		        	//print a message notifying about timer
//		        	System.out.println("Timer works. . . .");
		        	String time = timeleft.getText();
		    		int time_int = Integer.parseInt(time);
		    		time_int --;
		    		timeleft.setText(String.valueOf(time_int));
		    		if(time_int <= 0) {
		    			tm.cancel();
		    			timer_running=false; 
		    			bomb_pressed(); // the game is lost
		    		}
	        	}
	    	}, 1000, 1000);
	        
	        grid = board.getGrid(); // get Tiles Created
	        bombs_location = board.getBombLocation();   //get bomb locations   
	        GridPane help_root = board.getBoard();
	        root.getChildren().add(help_root); //add board's GridPane to our main GridPane
	        
	        int i=0, j=0;
	        for(i=0; i<X_AXIS_TILES; i++) {  //Set actions for every Tile
		        for(j=0; j<Y_AXIS_TILES; j++) {
		            final int innerI = i;
		            final int innerJ = j;
		        	grid[i][j].setOnMouseClicked(event -> 
			        {
			        	MouseButton button = event.getButton();  
			        	if (button == MouseButton.PRIMARY) {  // left click
			        		boolean previous_open = grid[innerI][innerJ].getOpen(); //get if Tile clicked is already open
			        		grid[innerI][innerJ].left_click(); // call corresponding function of Tile
			        		if (!previous_open){total_moves++;} //increase total moves by 1
			        		boolean opened = grid[innerI][innerJ].getOpen(); 
			        		boolean bomb_press = (!previous_open && opened && grid[innerI][innerJ].Bomb);  //before clicked now the Tile was closed, now is open and has bomb
			        		
			        		int openedWhileMarked = grid[innerI][innerJ].getOpenedWhileMarked(); //get number of Tiles opened while marked to update marked Tiles Value
			        		grid[innerI][innerJ].setOpenedWhileMarked();
			        		int marked__bombs = Integer.parseInt(markedbombs.getText());
			        		markedbombs.setText(String.valueOf((marked__bombs - openedWhileMarked))); // Set new number of marked bombs
			        		
			        		if(bomb_press) { bomb_pressed(); }
			        		tiles_to_win -= grid[innerI][innerJ].getTilesRevealed(); //decrease number of Tiles needed to win by the number of total tiles opened with current left click
			        		grid[innerI][innerJ].setTilesRevealed();
			        		if(tiles_to_win == 0) { // if all Tiles without bomb are opened
			        			winning();
			        		}
			        	}
			        	else if (button == MouseButton.SECONDARY){
			        		if(Integer.parseInt(markedbombs.getText()) < number_of_bombs || grid[innerI][innerJ].getMarkedBombs()){ //the number of marked Tiles can't be bigger than number of bombs
				        		boolean opened = grid[innerI][innerJ].getOpen(), marked = grid[innerI][innerJ].getMarkedBombs();
				        		int added_marked_bombs=0;
				        		if(!opened && !marked && (total_moves<5) && hyper_bomb && innerI == board.hyper_bomb_x-1 && innerJ == board.hyper_bomb_y-1) { //hyper_bomb not opened, not marked with a flag, less than 5 moves => open row and column of hyper_bomb
				        			added_marked_bombs = 0;
				        			hyper_bomb_found_in_5_moves(innerI, innerJ); //open row and column of Hyper bomb
				        			int openedWhileMarked = grid[innerI][innerJ].getOpenedWhileMarked(); //get number of Tiles opened while marked to update marked Tiles Value
					        		grid[innerI][innerJ].setOpenedWhileMarked();
					        		markedbombs.setText(String.valueOf((Integer.parseInt(markedbombs.getText()) - openedWhileMarked))); // Set new number of marked bombs
				        		}
				        		grid[innerI][innerJ].right_click(total_moves);
				        		if(!opened && marked) //it was marked with a flag before, so now it's not
				        			added_marked_bombs = -1;
				        		else if(!opened && !marked) //it wasn't marked with a flag before, now it is
				        			added_marked_bombs = +1;
				        		int marked__bombs = Integer.parseInt(markedbombs.getText());
				        		markedbombs.setText(String.valueOf((marked__bombs + added_marked_bombs))); // Set new number of marked bombs
			        		}
			        	}
			        });
		        }
	        }
	    
    	}
    	else {
    		Alert a = new Alert(AlertType.INFORMATION); //Alert that no Data or Invalid Data were Given
    		a.setHeaderText("Invalid or No Data");
            a.show();
    	}
    }
         
    private void create_mode() //function to create a new mode
    {
        Stage stage = new Stage(); // create new smaller stage to display field
        stage.setTitle("Create and Save new game mode");
        stage.setResizable(false);
        // Add Fields and Labels
        Label label1 = new Label("Scenario ID:");  
        TextField scenario_id = new TextField();
        HBox scenario_id_hb = new HBox();
        scenario_id_hb.getChildren().addAll(label1, scenario_id);
        
        Label label2 = new Label("Difficulty Level:"); 
        ChoiceBox difficulty_level = new ChoiceBox(FXCollections.observableArrayList("1", "2")); // choose difficulty level from possible values
        HBox difficulty_level_hb = new HBox();
        difficulty_level_hb.getChildren().addAll(label2, difficulty_level);
        
        Label label3 = new Label("Number of Bombs:"); 
        ChoiceBox number_of_bombs = new ChoiceBox(); // create choice box for number of bombs
        HBox number_of_bombs_hb = new HBox();
        number_of_bombs_hb.getChildren().addAll(label3, number_of_bombs);
        
        Label label4 = new Label("Hyper bomb:"); 
        ChoiceBox hyper_bomb =  new ChoiceBox(); // create choice box for hyper bomb
        HBox hyper_bomb_hb = new HBox();
        hyper_bomb_hb.getChildren().addAll(label4, hyper_bomb);
        
        Label label5 = new Label("Time Left(sec):"); 
        HBox time_left_hb = new HBox();
        Slider time_left = new Slider(); //create slider for possible total time values
		time_left.setBlockIncrement(1); //step 1 between values
		time_left.setMajorTickUnit(1);
		time_left.setMinorTickCount(0);
		time_left.setShowTickLabels(true); //show values
		time_left.setSnapToTicks(true);
		time_left.setPrefWidth(500);
        time_left_hb.getChildren().addAll(label5, time_left);
        
        difficulty_level.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() { // set possible values for TimeLeft, number of bombs and hyper bomb depending on the level
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
        create.setOnAction(action -> { // if create button is pressed
        	try{
        		if(!scenario_id.getText().isEmpty() && difficulty_level.getValue() != null && number_of_bombs.getValue() != null && hyper_bomb.getValue() != null) { //if none of the fields are empty
        			new FileWriter(System.getProperty("user.dir") + "/medialab\\SCENARIO-" + scenario_id.getText() + ".txt", false).close(); // delete file contents if there was a file with same id
	        		FileWriter fstream = new FileWriter(System.getProperty("user.dir") + "/medialab\\SCENARIO-" + scenario_id.getText() + ".txt");
	        		BufferedWriter out = new BufferedWriter(fstream);
	        		out.write(difficulty_level.getValue() + "\n"); // write difficulty level in first line
	        		out.write(number_of_bombs.getValue() + "\n"); // write number of bombs in second line
	        		out.write((int)time_left.getValue() + "\n"); // write Total Time Given in third line
	        		out.write(hyper_bomb.getValue() + "\n"); // write if there is a hyper bomb in fourth line
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
        close.setOnAction(bar -> {stage.close();}); // close pop up when button is pressed
        
        HBox buttons = new HBox();
        buttons.getChildren().addAll(create, close);
        
        VBox vb = new VBox();
        vb.getChildren().addAll(scenario_id_hb, difficulty_level_hb, number_of_bombs_hb, hyper_bomb_hb, time_left_hb, buttons);
        vb.setSpacing(5);
        
        Scene popup = new Scene(vb, 520, 200); // create pop up scene with specific size
        stage.setScene(popup);
        stage.show();
    }
    
    
    private void load_mode() //  function when load button is pressed 
    {
        Stage stage = new Stage();
        stage.setTitle("Load mode");
        stage.setResizable(false);
        
        Label label1 = new Label("Scenario ID:");  // field to select Id       
        ChoiceBox scenario_id = new ChoiceBox();
        HBox scenario_id_hb = new HBox();
        scenario_id_hb.getChildren().addAll(label1, scenario_id);
        
        File directoryPath = new File("medialab");
        //List of all files and directories
        scenario_id.getItems().removeAll(scenario_id.getItems()); // get all the ids of the given files and display them
        String contents[] = directoryPath.list();
        for(int i=0; i<contents.length; i++) {
           String[] arrOfStr = contents[i].split("-", 2);
           String[] keep_id = arrOfStr[1].split(".t", 2);
           scenario_id.getItems().add(keep_id[0]);
        }        
   
        Button load = new Button("Load");
        load.setOnAction(action -> {
        	try{	    
        		if(scenario_id.getValue() != null) {
        			File myObj = new File(System.getProperty("user.dir") + "/medialab\\SCENARIO-" + scenario_id.getValue() + ".txt"); // open files and read values
        			Scanner myReader = new Scanner(myObj);
        			int count_lines = 0;
        			while (myReader.hasNextLine()) {
        				String data = myReader.nextLine();
        				if(count_lines == 0) { //first line contains difficulty level
        					difficulty_level_temp = Integer.parseInt(data);
        					if(difficulty_level_temp != 1 && difficulty_level_temp != 2) { // if difficulty level value outside boundaries throw exception
        						throw new InvalidValueException();
        					}
        				} 
        				else if(count_lines == 1) { // second line contains number of bombs
        					number_of_bombs_temp = Integer.parseInt(data);
        					if(difficulty_level_temp == 1 && (number_of_bombs_temp < 9 || number_of_bombs_temp > 11)) { // if number of bombs value outside boundaries throw exception
        						throw new InvalidValueException();
        					}
        					else if(difficulty_level_temp == 2 && (number_of_bombs_temp < 35 || number_of_bombs_temp > 45)) { // if number of bombs value outside boundaries throw exception
        						throw new InvalidValueException();
        					}
        				}  
        				else if(count_lines == 2) { // third line contains Total Time Given
        					time_left_in_seconds_temp = Integer.parseInt(data);
        					if(difficulty_level_temp == 1 && (time_left_in_seconds_temp < 120 || time_left_in_seconds_temp > 180)) { // if number of bombs value outside boundaries throw exception
        						throw new InvalidValueException();
        					}
        					else if(difficulty_level_temp == 2 && (time_left_in_seconds_temp < 240 || time_left_in_seconds_temp > 360)) { // if number of bombs value outside boundaries throw exception
        						throw new InvalidValueException();
        					}
        				}  
        				else if(count_lines == 3) { // fourth line contains boolean hyper bomb
        					int help = Integer.parseInt(data);
        					hyper_bomb_temp =  (help == 1);
        					if(difficulty_level_temp == 1 && hyper_bomb_temp == true) { // if difficulty level equals to 1 and we have a hyper bomb throw exception
        						throw new InvalidValueException();
        					}
        				}  
        				else { // if we have more than four values throw invalid description exception
        					throw new InvalidDescriptionException();
        				}
        				count_lines++;
        				valid_data = true;
        			}
        			if(count_lines < 4) { //if we read less than 4 values
    					throw new InvalidDescriptionException();
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
        	catch (Exception e) {  // catch exceptions and print message
        		if(e instanceof InvalidValueException) {
	      	      valid_data = false;
	      	      e.printStackTrace();
        		}
        		if(e instanceof InvalidDescriptionException) {
        			valid_data = false;
        			e.printStackTrace();
        		}
        	}
        });
             
        Button close = new Button("Close");
        close.setOnAction(bar -> {stage.close();}); // close pop up when close button is pressed
        
        HBox buttons = new HBox();
        buttons.getChildren().addAll(load, close);
        
        VBox vb = new VBox();
        vb.getChildren().addAll(scenario_id_hb, buttons);
        vb.setSpacing(5);
        
        Scene popup = new Scene(vb, 270, 100); // create pop up scene with certain size
        stage.setScene(popup);
        stage.show();
    }
    
    private void show_solution() // function when Solution is pressed
    {
    	if(something_already_drawn && timer_running && !solution_revealed) { // if we are in the middle of a game and a solution has not been revealed yet
	    	for(Coordinate i: bombs_location) { // read bomb location and open corresponding Tiles
	    		int x_axis = i.getX()-1;
	    		int y_axis = i.getY()-1;
	    		if(i.getHyperBomb()) { //hyper bomb
	    			grid[x_axis][y_axis].open();
	    			Hyper_Tile test = (Hyper_Tile) grid[x_axis][y_axis];
	    			test.open(); 
	    		}
	    		else {
	    			grid[x_axis][y_axis].hyper_open(); // not like usually opening a bomb, the game is considered lost only all bombs are revealed
	    		}
			}
	    	if(timer_running) { // if there is a timer running then cancel it
	    		tm.cancel();
		   		timer_running = false;
	    	}
	    	Round_result helper = new Round_result(number_of_bombs, total_moves, time_left_in_seconds - Integer.parseInt(timeleft.getText()), "Computer"); // save result as won by computer
	    	total_results++;
	    	results.add(helper); // save result
	    	if(total_results > 5) { // maximum 5 results saved
	    		results.remove(0);
	    	}
	    	for(int i=0; i<X_AXIS_TILES; i++) { // make all Tiles not editable
	     	   for(int j=0; j<Y_AXIS_TILES; j++) {
	     		   grid[i][j].setDisable(true);
	     	   }
	        } 
	    	solution_revealed = true; // so we dont reveal solution 2 times, game considered lost only 1 time
    	}
    }
    
   private void show_results() { // function when Results is pressed
	   Stage stage = new Stage();
       stage.setTitle("Rounds");
       stage.setResizable(false);


       Label title_first = new Label("TITLES");
       title_first.setMinWidth(53);
       Button column [] = new Button[4];
       column[0] = new Button("Number of Bombs"); // Set title for every column
       column[1] = new Button("Total Moves");
       column[2] = new Button("Total Time");
       column[3] = new Button("Winner");
       
       HBox scenario_id_hb_first = new HBox();
       scenario_id_hb_first.getChildren().addAll(title_first, column[0], column[1], column[2], column[3]);

       VBox vb = new VBox();
       vb.getChildren().addAll(scenario_id_hb_first);
       Scene popup = new Scene(vb, 600, 220); // create pop up scene and add VBox to the scene
       stage.setScene(popup);
       stage.show();
       
       Label titles [] = new Label[5];
       for(int i=0; i<5; i++) {
    	   titles[i] = new Label("ROUND " + Integer.toString(i+1)); // create label for every row
       }

       Button a[] = new Button[20]; // create buttons and fix size
       double width = 0;
       for(int i =0; i<4; i++) {
    	   width = column[i].getWidth();
    	   for(int j = i; j<20; j+=4) {
        	   a[j] = new Button("-");
        	   a[j].setMinWidth(width);
    	   }
       }
       
       int counter = 0;
       for(Round_result result : results) { // Write the results on not editable buttons and display them
		   a[counter*4].setText(Integer.toString(result.getBombs()));
		   a[counter*4+1].setText(Integer.toString(result.getTotal_moves()));
		   a[counter*4+2].setText(Integer.toString(result.getTotal_time()));
		   a[counter*4+3].setText(result.getWinner());
		   counter++;
       }

       HBox scenario_id_hb = new HBox(); // add buttons and titles to HBoxes and the to VBox
       scenario_id_hb.getChildren().addAll(titles[0] , a[0], a[1], a[2], a[3]);

       HBox scenario_id_hb2 = new HBox();
       scenario_id_hb2.getChildren().addAll(titles[1], a[4], a[5], a[6], a[7]);

       HBox scenario_id_hb3 = new HBox();
       scenario_id_hb3.getChildren().addAll(titles[2], a[8], a[9], a[10], a[11]);

       HBox scenario_id_hb4 = new HBox();
       scenario_id_hb4.getChildren().addAll(titles[3], a[12], a[13], a[14], a[15]);

       HBox scenario_id_hb5 = new HBox();
       scenario_id_hb5.getChildren().addAll(titles[4], a[16], a[17], a[18], a[19]);
     
       Button close = new Button("Close"); // close when close button is pressed
       close.setOnAction(bar -> {stage.close();});
     
       vb.getChildren().addAll(scenario_id_hb, scenario_id_hb2, scenario_id_hb3, scenario_id_hb4, scenario_id_hb5, close);
       vb.setSpacing(5);
	}
   
   private void winning() { // when all Tiles without a bomb have been opened
       for(int i=0; i<X_AXIS_TILES; i++) { //make all Tiles not editable
    	   for(int j=0; j<Y_AXIS_TILES; j++) {
    		   grid[i][j].setDisable(true);
    	   }
       }
       tm.cancel(); // cancel timer
       
       Round_result helper = new Round_result(number_of_bombs, total_moves, time_left_in_seconds - Integer.parseInt(timeleft.getText()), "Player"); // add result with Player as the winner
   		total_results++;
   		results.add(helper);
   		if(total_results > 5) { // max 5 results
   			results.remove(0);
   		}
   		timer_running = false;
       Alert a = new Alert(AlertType.INFORMATION);
       a.setHeaderText("YOU WON, PRESS START TO PLAY AGAIN"); //Inform the player that he won
       a.show();
   }
   
   private void bomb_pressed() { // When a Tile with a Bomb is pressed
       for(int i=0; i<X_AXIS_TILES; i++) { // disable all Tiles
    	   for(int j=0; j<Y_AXIS_TILES; j++) {
    		   grid[i][j].setDisable(true);
    	   }
       }
       if(timer_running) {
    	   tm.cancel(); //cancel timer
    	   timer_running = false;
       }
       
       Round_result helper = new Round_result(number_of_bombs, total_moves, time_left_in_seconds - Integer.parseInt(timeleft.getText()), "Computer"); // add result with Computer as the winner
   		total_results++;
   		results.add(helper);
   		if(total_results > 5) { // maximum 5 results
   			results.remove(0);
   		}
   		
   		Runnable updateUIRunnable = new Runnable() {
   		    @Override
   		    public void run() {
   		    	Alert a = new Alert(AlertType.INFORMATION);
   		    	a.setHeaderText("YOU LOST, PRESS START TO TRY AGAIN"); // Inform Player that he lost
   		    	a.show();
   		    }
   		};

   		Platform.runLater(updateUIRunnable);

       
   }
   
    private void hyper_bomb_found_in_5_moves(int x, int y) { // Hyper Bomb flagged within 5 moves
		grid[board.hyper_bomb_x-1][board.hyper_bomb_y-1].setTilesRevealed();
    	for(int i=0; i<X_AXIS_TILES; i++) {
			if(i!=y)
				grid[x][i].hyper_open(); // open Tiles on the same row with the hyper bomb
		}
		for(int j=0; j<Y_AXIS_TILES; j++) {
			if(j!=x)
				grid[j][y].hyper_open(); // open Tiles on the same column with the hyper bomb
		}
		tiles_to_win -= grid[board.hyper_bomb_x-1][board.hyper_bomb_y-1].getTilesRevealed(); // decrease total number of Tiles needed to win
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
   
        scene = new Scene(createContent()); //add main component to scene

        this.primaryStage.setScene(scene);
        this.primaryStage.setTitle("MediaLab Minesweeper"); //scene title
        this.primaryStage.setResizable(false);
        
        this.primaryStage.setOnCloseRequest(event -> { //when the scene closes, if the timer is running, cancel it
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
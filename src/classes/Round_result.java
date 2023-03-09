package classes;

/**
 * Round_result class purpose is to create objects which 
 * store four values,
 * this class is used in MinesweeperApp class to store 
 * round results of last 5 rounds and display them when 
 * the button Rounds is pressed
 */
public class Round_result
{
    private int bombs, total_moves, total_time;
    private String winner;
    
    /**
     * @param bombs  The number of bombs in the current mode
     * @param total_moves  The number of moves played until now in the game
     * @param total_time  The total amount of time that has passed since the start of the current game
     * @param winner  The winner of the round, "Player" if the player wins or "Computer" if the player loses
     */
    public Round_result(int bombs, int total_moves, int total_time, String winner)
    {
    	this.bombs = bombs;
    	this.total_moves = total_moves;
    	this.total_time = total_time;
    	this.winner = winner;
    }
    
    /**
     * @param bombs: The number of bombs in the current mode
     * @param total_moves: The number of moves played until now in the game
     * @param total_time: The total amount of time that has passed since the start of the current game
     * @param winner: The winner of the round, "Player" if the player wins or "Computer" if the player loses
     * @return true if total_moves and total time are not negative
     */
    public static boolean isValidRound_result(int bombs, int total_moves, int total_time, String winner)
    {
        return (total_moves >= 0 && total_time >=0);
    }
    
    /**
     * @param c, Round_result object which contains number of bombs, 
     * 			 total number of moves, total time passed and the 
     * 			 winner of the game
     * @return true if total_moves and total time of the Round 
     * 	       Result are not negative
     */
    public static boolean isValidRound_result(Round_result c)
    {
        return (c.total_moves >= 0 && c.total_time >=0);
    }
    
    /**
     * @return The number of bombs in the current game
     */
    public int getBombs()
    {
        return bombs;
    }
    
    /**
     * @return The total number of moves played until now in the game
     */
    public int getTotal_moves()
    {
        return total_moves;
    }
    
    /**
     * @return The total amount of time in seconds that has passed since
     * 		   the start of the game.
     */
    public int getTotal_time()
    {
        return total_time;
    }
    
    /**
     * @return A String that contains the winner of the game
 * 			   "Computer" if the player loses
     * 		   "Player" if the player wins
     */
    public String getWinner()
    {
        return winner;
    }
    
    /**
     * @return A String containing the round results, and more specifically
     *         the number of bombs, the total time spent and total moves until
     *         now and the winner of the game
     */
    public String toString()
    {
    	return "bombs: " + String.valueOf(bombs) + " total_moves: " + String.valueOf(total_moves) + " total_time: " + String.valueOf(total_time) + " winner: " + winner;
    }
    
}

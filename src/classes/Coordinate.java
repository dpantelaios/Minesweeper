package classes;

public class Coordinate // Coordinate is used as Bombs location
{
    private int x, y;
    private boolean hyper_bomb= false;

    public Coordinate() // create Coordinate
    {
        this.x = 0;
        this.y = 0;
    }
    
    public Coordinate(int x, int y) // create Coordinate
    {
        this.x = x+1;
        this.y = y+1;
    }  
    
    public Coordinate(int x, int y, boolean hyper_bomb) // create Coordinate
    {
        this.x = x+1;
        this.y = y+1;
        this.hyper_bomb = hyper_bomb;
    }
    
   
    public static boolean isValidCoordinate(int x, int y, int difficulty_level) // check if coordinate is valid
    {
        return (x >= 0 && x < 9 && y >= 0 && y < 9 && difficulty_level == 0) || (x >= 0 && x < 16 && y >= 0 && y < 16 && difficulty_level == 0);
    }
    
    public static boolean isValidCoordinate(Coordinate c, int difficulty_level) // check if coordinate is valid
    {
        return (c.getX() >= 0 && c.getX() < 9 && c.getY() >= 0 && c.getY() < 9 && difficulty_level == 0) || (c.getX() >=0&& c.getX() <16 && c.getY() >= 0 && c.getY() < 16 && difficulty_level == 1);

    }
    // Get Coordinate Values    
    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
    
    public boolean getHyperBomb()
    {
        return hyper_bomb;
    }
    // Convert Coordinate to String
    public String toString()
    {
    	return "x: " + String.valueOf(x) + " y: " + String.valueOf(y);
    }
    
    public String toString_hyper()
    {
    	return "x: " + String.valueOf(x) + " y: " + String.valueOf(y) + " hyper_bomb: " + String.valueOf(hyper_bomb);

    }
}

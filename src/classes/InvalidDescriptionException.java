package classes;

@SuppressWarnings("serial")
public class InvalidDescriptionException extends Exception{
	
	public InvalidDescriptionException()
    {
        super("Invalid Description Exception");
    }
    
    public InvalidDescriptionException(String message)
    {
        super(message);
    }

}

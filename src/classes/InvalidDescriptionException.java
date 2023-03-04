package classes;

@SuppressWarnings("serial")
public class InvalidDescriptionException extends Exception{ // extend Exception and create new exception with our message
	
	public InvalidDescriptionException()
    {
        super("Invalid Description Exception");
    }
    
    public InvalidDescriptionException(String message)
    {
        super(message);
    }

}

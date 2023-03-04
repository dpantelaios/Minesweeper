package classes;

@SuppressWarnings("serial")
public class InvalidValueException extends Exception{ // extend Exception and create new exception with our message
	
	public InvalidValueException()
    {
        super("InvalidValueException");
    }
    
    public InvalidValueException(String message)
    {
        super(message);
    }

}
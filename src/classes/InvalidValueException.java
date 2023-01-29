package classes;

@SuppressWarnings("serial")
public class InvalidValueException extends Exception{
	
	public InvalidValueException()
    {
        super("InvalidValueException");
    }
    
    public InvalidValueException(String message)
    {
        super(message);
    }

}
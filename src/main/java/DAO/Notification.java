package DAO;

public class Notification {
	public String username;
	public String message;
	public int messageType; //0 = Demande d'ami, 1 = Avertissement, 2 = Envoie de map
	public boolean readed;
	
	public Notification()
	{
		
	}
	public Notification(String username, String message, int messageType)
	{
		this.username = username;
		this.message = message;
		this.messageType = messageType;
		this.readed = false;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getUsername()
	{
		return username;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	public String getMessage()
	{
		return message;
	}
	
	public void setMessageType(String message)
	{
		this.message = message;
	}
	public String getMessageType()
	{
		return message;
	}
	public void setReaded(boolean readed)
	{
		this.readed = readed;
	}
	public boolean getReaded()
	{
		return this.readed;
	}
	@Override
    public String toString(){
        return "|user : "+username+", message : "+message+", messageType : "+messageType+", readed : "+readed;
    }
}

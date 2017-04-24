package device.device.aes;

public  class UserData {

	
    int _id;
    String _imei;
    String _name;
    String _message;
    
   
    public UserData(){
 
    }
  
    public UserData(int id, String imei, String name, String message){
        this._id      = id;
        this._imei    = imei;
        this._name    = name;
        this._message = message;
        
    }
    public UserData(String imei, String name, String message){
        
        this._imei    = imei;
        this._name    = name;
        this._message = message;
        
    }
    
    public int getID(){
        return this._id;
    }
 
   
    public void setID(int id){
        this._id = id;
    }
 
    
    public String getIMEI(){
        return this._imei;
    }
 
    
    public void setIMEI(String imei){
        this._imei = imei;
    }
    
    
    public String getName(){
        return this._name;
    }
 
    
    public void setName(String name){
        this._name = name;
    }
    

    public String getMessage(){
        return this._message;
    }
 
   
    public void setMessage(String message){
        this._message = message;
    }

	
	@Override
	public String toString() {
		return "UserInfo [name=" + _name + "]";
	}
	
}

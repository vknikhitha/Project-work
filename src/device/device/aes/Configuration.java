package device.device.aes;

public interface Configuration 
{
		
	static final boolean SECOND_SIMULATOR = false;	
    static final String YOUR_SERVER_URL = "http://134.154.79.156/GCM/gcm_server_files/";
	static final String GOOGLE_SENDER_ID ="962082678093";
   
    static final String TAG = "GCM Android Example";

    static final String DISPLAY_REGISTRATION_MESSAGE_ACTION =
           "com.androidexample.gcm.DISPLAY_REGISTRATION_MESSAGE";
    
    static final String DISPLAY_MESSAGE_ACTION ="com.androidexample.gcm.DISPLAY_MESSAGE";

    
    static final String EXTRA_MESSAGE = "message";
    	
}

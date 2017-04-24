package device.device.aes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

import device.device.aes.DataBaseAdapter;
import device.device.aes.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartPushNotification extends Activity 
{
	
	
	EditText txtMessage; 
	TextView sendTo;
	   String normalTextEnc;
	  String seedValue = "This Is MySecure";
	Button btnSend;
    
	ControllerApplication aController = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		DataBaseAdapter.init(this);
		
		setContentView(R.layout.send_push_notification);
		
		
		aController = (ControllerApplication) getApplicationContext();
		
		
		if (!aController.isConnectingToInternet()) {
			
		
			aController.showAlertDialog(StartPushNotification.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			
			
			return;
		}

		
		Intent i = getIntent();
		
		final String name = i.getStringExtra("name");
		final String imei = i.getStringExtra("imei");
		final String sendfrom = i.getStringExtra("sendfrom");
		
		
		txtMessage = (EditText) findViewById(R.id.txtMessage);
		sendTo     = (TextView) findViewById(R.id.sendTo);
		btnSend    = (Button) findViewById(R.id.btnSend);
		
		sendTo.setText("Send To : "+name);
		
		
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{  
				
				String message1 = txtMessage.getText().toString(); 
				
//				String normalText = "DHARANI";
	           
	                     try {
	                               normalTextEnc = Encryptor.encrypt(seedValue, message1);
//	                               String normalTextDec = Encryptor.decrypt(seedValue, normalTextEnc);
	                             
	                     } catch (Exception e) {
	                           // TODO Auto-generated catch block
	                           e.printStackTrace();
	                     }
				
				    int max=10000;
				    int min=1000;
				    int diff=max-min;
				    Random rn = new Random();
				    int i = rn.nextInt(diff+1);
				    i+=min;
				    System.out.print("The Random Number Key is " + i);
				    
				    String key=String.valueOf(i);
				    
				    String message=normalTextEnc+","+key;
				    
				  Log.e("!!!!!Important !!!!!!! Msg + Key ", message);
				  
				  
				   
				
		        String serverURL = Configuration.YOUR_SERVER_URL+"sendpush.php";
		        
		        
		        new LongOperation().execute(serverURL,imei,message,sendfrom); 
		        
		        txtMessage.setText("");
			}
		});
	}
	
	
	public class LongOperation  extends AsyncTask<String, Void, String> {
        
    	
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(StartPushNotification.this); 
        String data  = ""; 
        int sizeData = 0;  
        
        
        protected void onPreExecute() {
           
           
            Dialog.setMessage("Please wait..");
            Dialog.show();
            
        }
 
       
        protected String doInBackground(String... params) {
        	
        	
        	BufferedReader reader=null;
        	String Content = "";
	            
	            try{
	            	
	            	
		            URL url = new URL(params[0]);
	            	
		           
		            if(!params[1].equals(""))
	               	   data +="&" + URLEncoder.encode("data1", "UTF-8") + "="+params[1].toString();
		            if(!params[2].equals(""))
		               data +="&" + URLEncoder.encode("data2", "UTF-8") + "="+params[2].toString();	
		            if(!params[3].equals(""))
			           data +="&" + URLEncoder.encode("data3", "UTF-8") + "="+params[3].toString();	
	              
		            
		        
	   
	              URLConnection conn = url.openConnection(); 
	              conn.setDoOutput(true); 
	              OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
	              wr.write( data ); 
	              wr.flush(); 
	          
	           
	               
	              reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	              StringBuilder sb = new StringBuilder();
	              String line = null;
	            
		           
		            while((line = reader.readLine()) != null)
		                {
		                      
		                       sb.append(line + "\n");
		                }
	                
	              
	               Content = sb.toString();
	            }
	            catch(Exception ex)
	            {
	            	Error = ex.getMessage();
	            }
	            finally
	            {
	                try
	                {
	     
	                    reader.close();
	                }
	   
	                catch(Exception ex) {}
	            }
        	
           
            return Content;
        }
         
        protected void onPostExecute(String Result) {
           
            Dialog.dismiss();
            
            if (Error != null) {
            	Toast.makeText(getBaseContext(), "Error: "+Error, Toast.LENGTH_LONG).show();  
                 
            } else {
              
            	
            	 Toast.makeText(getBaseContext(), "Message sent."+Result, Toast.LENGTH_LONG).show();  
                 
             }
        }
         
    }

}

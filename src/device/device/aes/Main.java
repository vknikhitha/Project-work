package device.device.aes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import device.device.aes.R;
import device.device.aes.UserViewActivity.LongOperation;

import com.google.android.gcm.GCMRegistrar;

public class Main extends Activity {
	
	
	TextView lblMessage;
	ControllerApplication aController;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		DataBaseAdapter.init(this);
		
		
		aController = (ControllerApplication) getApplicationContext();
		
		
		
		if (!aController.isConnectingToInternet()) {
			
		
			aController.showAlertDialog(Main.this,
					"Internet Connection Error",
					"Please connect to Internet connection", false);
			
			return;
		}
	
		
		int vDevice = DataBaseAdapter.validateDevice();	
		
		if(vDevice > 0)
		{	
			
			
			Intent i = new Intent(getApplicationContext(), UserViewActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			String deviceIMEI = "";
			if(Configuration.SECOND_SIMULATOR){
			
				
				deviceIMEI = "000000000000005";
			}	
			else
			{
			 
			 TelephonyManager tManager = (TelephonyManager) getBaseContext()
			    .getSystemService(Context.TELEPHONY_SERVICE);
			  deviceIMEI = tManager.getDeviceId(); 
			}
			
			
	        String serverURL = Configuration.YOUR_SERVER_URL+"validate_device.php";
	       
	        LongOperation serverRequest = new LongOperation(); 
	        
	        serverRequest.execute(serverURL,deviceIMEI,"","");
			
		}	
		
	}		
	
	
	
	public class LongOperation  extends AsyncTask<String, Void, String> {
	         
	       
	        private String Error = null;
	        private ProgressDialog Dialog = new ProgressDialog(Main.this); 
	        String data =""; 
	        int sizeData = 0;  
	        
	        
	        protected void onPreExecute() {
	           
	           
	            Dialog.setMessage("Validating Device..");
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
		              Log.i("GCM",data);
			            
			         
		   
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
	         
	        protected void onPostExecute(String Content) {
	           
	            Dialog.dismiss();
	            
	            if (Error != null) {
	                 
	                 
	            } else {
	              
	            	aController.clearUserData();
	            	
	            	JSONObject jsonResponse;
	                      
	                try {
	                      
	                    
	                     jsonResponse = new JSONObject(Content);
	                      
	                   
	                     JSONArray jsonMainNode = jsonResponse.optJSONArray("Android");
	                      
	                    
	  
	                     int lengthJsonArr = jsonMainNode.length();  
	  
	                     for(int i=0; i < lengthJsonArr; i++) 
	                     {
	                        
	                         JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
	                          
	                        
	                         String Status = jsonChildNode.optString("status").toString();
	                         
	                         Log.i("GCM","---"+Status);
	                         
	                       
	                         if(Status.equals("update")){
	                            
	                        	String RegID      = jsonChildNode.optString("regid").toString();
	                            String Name       = jsonChildNode.optString("name").toString();
	                            String Email      = jsonChildNode.optString("email").toString();
	                            String IMEI       = jsonChildNode.optString("imei").toString();
	                            
	                           
	                            DataBaseAdapter.addDeviceData(Name, Email,RegID, IMEI);
	                            
	                            
	                			Intent i1 = new Intent(getApplicationContext(), UserViewActivity.class);
	                			startActivity(i1);
	                			finish();
	                           
	                            Log.i("GCM","---"+Name);
	                         }
	                         else if(Status.equals("install")){  
	                        	
	                        	
		                		Intent i1 = new Intent(getApplicationContext(), Registration.class);
		                		startActivity(i1);
		                		finish();
	                        	 
	                         }
	                         
	                        
	                    }
	                     
	               
	                      
	                 } catch (JSONException e) {
	          
	                     e.printStackTrace();
	                 }
	  
	                 
	             }
	        }
	         
	    }

	
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

}

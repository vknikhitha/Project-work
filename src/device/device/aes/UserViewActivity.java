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
import device.device.aes.CustomGridAdapter;
import device.device.aes.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class UserViewActivity extends Activity {

	GridView gridView;
    ControllerApplication aController = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_view_android_example); 

		
		gridView = (GridView) findViewById(R.id.gridView1);

		
	    aController = (ControllerApplication) getApplicationContext();
		
		
        String serverURL = Configuration.YOUR_SERVER_URL+"userdata.php";
        
      
        LongOperation serverRequest = new LongOperation(); 
        
        serverRequest.execute(serverURL,"rrr","","");
		
        
       
		 
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				String deviceIMEI = "";
				if(Configuration.SECOND_SIMULATOR){
					
					
					
					deviceIMEI = "000000000000001";
				}	
				else
				{
				    
				 TelephonyManager tManager = (TelephonyManager) getBaseContext()
				    .getSystemService(Context.TELEPHONY_SERVICE);
				  deviceIMEI = tManager.getDeviceId(); 
				}
				
				String uIMEI = aController.getUserData(position).getIMEI();
				String uName = aController.getUserData(position).getName();
				
				
				
				
				Intent i = new Intent(getApplicationContext(), StartPushNotification.class);
				
				
				i.putExtra("name", uName);
				i.putExtra("imei", uIMEI); 
				i.putExtra("sendfrom", deviceIMEI);
				startActivity(i);
			
				
				
			}
		});

	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.datahiding_activity, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.datahiding:
//			Intent i=new Intent(UserViewActivity.this,Index.class);
//			startActivity(i);
			break;

		}
		return true;
	}
	
	public class LongOperation  extends AsyncTask<String, Void, String> {
	         
	    
	        private String Error = null;
	        private ProgressDialog Dialog = new ProgressDialog(UserViewActivity.this); 
	        String data =""; 
	        int sizeData = 0;  
	        
	        
	        protected void onPreExecute() {
	            
	           
	            Dialog.setMessage("Getting registered users ..");
	            Dialog.show();
	            
	        }
	 
	       
	        protected String doInBackground(String... params) {
	        	
	        	
	        	BufferedReader reader=null;
	        	String Content = "";
		           
		            try{
		            	
		            	
			               URL url = new URL(params[0]);
		            	
			           
			            if(!params[1].equals(""))
		               	   data +="&" + URLEncoder.encode("data", "UTF-8") + "="+params[1].toString();
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
	                          
	                        
	                         String IMEI       = jsonChildNode.optString("imei").toString();
	                         String Name       = jsonChildNode.optString("name").toString();
	                          
	                         Log.i("GCM","---"+Name);
	                         
	                         UserData userdata = new UserData();
	                         userdata.setIMEI(IMEI);
	                         userdata.setName(Name);
	                        
	                       
	                         aController.setUserData(userdata);
	                         
	                    }
	               
	                  gridView.setAdapter(new CustomGridAdapter(getBaseContext(), aController));
	                    
	                      
	                 } catch (JSONException e) {
	          
	                     e.printStackTrace();
	                 }
	  
	                 
	             }
	        }
	         
	    }


}
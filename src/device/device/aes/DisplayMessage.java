package device.device.aes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import register.login.encrypt.LoginActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import device.device.aes.R;
import device.device.aes.R.id;
import device.device.aes.R.layout;

public class DisplayMessage extends Activity {
	
	
	EditText txtMessage; 
	
	Button btnSend;
	String UserName;
	
	TextView lblMessage;
	ControllerApplication aController;
	Context context=this;
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	String name;
	String message;
    String UserDeviceIMEI;
	
	
    public  ArrayList<UserData> CustomListViewValuesArr = new ArrayList<UserData>();
    TextView output = null;
    CustomAdapter adapter;
    DisplayMessage activity = null;
   String password;
   String key,msg;
   String seedValue = "This Is MySecure";
    

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{ 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_message);
		
//		AlertDialogBox();
		PromptDialogBox();
		
		DataBaseAdapter.init(this);
		
		 SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE); 
		 Editor editor = pref.edit();
		 msg=pref.getString("msg", "hai");  
		 key=pref.getString("key", "123");  
		 
		aController = (ControllerApplication) getApplicationContext();
		
		if (!aController.isConnectingToInternet())
		{
			aController.showAlertDialog(DisplayMessage.this,"Internet Connection Error","Please connect to Internet connection", false);
			
			return;
		}
		
		lblMessage = (TextView) findViewById(R.id.lblMessage);
		
		
		
		if(lblMessage.getText().equals("")){
		
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Configuration.DISPLAY_MESSAGE_ACTION));
		}
		
		List<UserData> data = DataBaseAdapter.getAllUserData();      
		 
		
        for (UserData dt : data)
        {
            if(dt.getName().startsWith("Me"))
            {
            lblMessage.append(dt.getName()+" : "+dt.getMessage()+"1 \n");
            }
            else
            {
            lblMessage.append(dt.getName()+" : "+dt.getMessage()+"2 \n");
            }
        }
        
      
          
        activity  = this;
        
        
        List<UserData> SpinnerUserData = DataBaseAdapter.getDistinctUser();

        for (UserData spinnerdt : SpinnerUserData) 
        {
            
        	 UserData schedSpinner = new UserData();
            
        
        	schedSpinner.setName(spinnerdt.getName());
        	schedSpinner.setIMEI(spinnerdt.getIMEI());
             
        	Log.i("GCMspinner", "-----"+spinnerdt.getName());
              
         
          CustomListViewValuesArr.add(schedSpinner);
          
        }
        
        
        Spinner  SpinnerExample = (Spinner)findViewById(R.id.spinner);
       
        Resources res = getResources(); 
         
       
        adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res);
         
     
        SpinnerExample.setAdapter(adapter);
         
       
        SpinnerExample.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
             
                 UserName       = ((TextView) v.findViewById(R.id.username)).getText().toString();
                UserDeviceIMEI        = ((TextView) v.findViewById(R.id.imei)).getText().toString();
                 
                String OutputMsg = "Selected User : \n\n"+UserName+"\n"+UserDeviceIMEI;
                 
                Toast.makeText(
                        getApplicationContext(),OutputMsg, Toast.LENGTH_LONG).show();
            }
 
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
               
            }
 
        });
        
        
        txtMessage = (EditText) findViewById(R.id.txtMessage);
		btnSend    = (Button) findViewById(R.id.btnSend);
        
   
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {  
				
			
				String message1 = txtMessage.getText().toString(); 
				
			    int max=10000;
			    int min=1000;
			    int diff=max-min;
			    Random rn = new Random();
			    int i = rn.nextInt(diff+1);
			    i+=min;
			    System.out.print("The Random Number Key is " + i);
			    
			    String key=String.valueOf(i);
			    
			    String message=message1+","+key;
			    
			  Log.e("!!!!!Important 2 !!!!!!! Msg + Key ", message);
				
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
				 
				   Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
				   Toast.makeText(getApplicationContext(),UserName, Toast.LENGTH_LONG).show();
				   Toast.makeText(getApplicationContext(),UserDeviceIMEI, Toast.LENGTH_LONG).show();
				   String s="Me :";
				  
				   UserData userdata = new UserData(deviceIMEI,s,message);
			       DataBaseAdapter.addUserData(userdata);
			       
			       aController.displayMessageOnScreen(context,s,message,deviceIMEI);
			      Log.i("username","Me : ");
			      Log.d("IMEI",deviceIMEI);
			      Log.e("Message",message);
			      try {
			    	  message = Encryptor.encrypt("my secret text",message);
						
						Log.d("after Encrypt : ",message);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				
		        String serverURL = Configuration.YOUR_SERVER_URL+"sendpush.php";
		        
		      if(!UserDeviceIMEI.equals(""))
		      {	  
		        
		        
		        
		     
		        new LongOperation().execute(serverURL,UserDeviceIMEI,message,deviceIMEI); 
		        
		        txtMessage.setText("");
		      }
		      else
		      {
		    	  Toast.makeText(
	                        getApplicationContext(),"Please select send to user.", Toast.LENGTH_LONG).show();
		    	  
		      }
		   
		      
			}
		});
		
	}		


	private void PromptDialogBox() {
		// TODO Auto-generated method stub// get prompts.xml view
		
        LayoutInflater layoutInflater = LayoutInflater.from(context);



        View promptView = layoutInflater.inflate(R.layout.prompts, null);



        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);



        // set prompts.xml to be the layout file of the alertdialog builder

        alertDialogBuilder.setView(promptView);



        final EditText input = (EditText) promptView.findViewById(R.id.userInput);



        // setup a dialog window

        alertDialogBuilder

                .setCancelable(false)

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {


//                               input.getText().toString();
                               password = input.getText().toString();
                               
                               if (password.equals(key))
                               {
                                

        	                     try {
        	                               //normalTextEnc = Encryptor.encrypt(seedValue, message1);
       	                               String normalTextDec = Encryptor.decrypt(seedValue, msg);
       	                            Toast.makeText(getApplicationContext(), "Key Matched", Toast.LENGTH_SHORT).show();
       	                         Toast.makeText(getApplicationContext(), normalTextDec, Toast.LENGTH_SHORT).show();
       	                               
       	                            AlertDialog alertDialog = new AlertDialog.Builder(
       	                            	    getApplicationContext()).create();
       	                            	    alertDialog.setTitle("Alert Dialog"); //set title
       	                            	    alertDialog.setMessage(normalTextDec); //set Message 
       	                            	    alertDialog.setIcon(R.drawable.a); //set icon/image
       	                            	    alertDialog.setButton("OK",new DialogInterface.OnClickListener() 
       	                            	    {
       	                            	        public void onClick(DialogInterface dialog,
       	                            	        int which)
       	                            	        {
       	                            	 Toast.makeText(getApplicationContext(),"You clicked on OK", Toast.LENGTH_SHORT).show();
       	                            	 Intent myIntent1 = new Intent(getApplicationContext(),DisplayMessage.class);
                                         startActivity(myIntent1);
                                         dialog.cancel();
       	                            	    }
       	                            	    });
       	                            	    // Showing Alert Message
       	                            	 AlertDialog alertdialog = alertDialogBuilder.create();
       	                            	    alertdialog.show();
        	                             
        	                     } catch (Exception e) {
        	                           // TODO Auto-generated catch block
        	                           e.printStackTrace();
        	                     }
                                    
                                  
                               } else 
                               {
                                    Toast.makeText(getApplicationContext(),
                                        "Key Wrong! Try Again", Toast.LENGTH_SHORT).show();
                                    Intent myIntent1 = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(myIntent1);
                                    dialog.cancel();
                               } 
                          	 

                            }

                        });


        // create an alert dialog

        AlertDialog alertD = alertDialogBuilder.create();



        alertD.show();
	
		
	}





	private void AlertDialogBox() {
		// TODO Auto-generated method stub
//		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
//		 alertDialog.setTitle("PASSWORD");
//		 alertDialog.setMessage("Enter Password");
//
//		 final EditText input = new EditText(DisplayMessage.this);
//		 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//		     LinearLayout.LayoutParams.MATCH_PARENT,
//		     LinearLayout.LayoutParams.MATCH_PARENT);
//		 input.setLayoutParams(lp);
//		 alertDialog.setView(input);
//		 alertDialog.setIcon(R.drawable.a);
//
//		 alertDialog.setPositiveButton("Ok",
//		     new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface arg0, int arg1) {
//					// TODO Auto-generated method stub
//					password = input.getText().toString();
//		            
//	                 if (password.equals(key))
//	                 {
//	                     Toast.makeText(getApplicationContext(),
//	                         "Key Matched", Toast.LENGTH_SHORT).show();
//	                     Intent myIntent1 = new Intent(getApplicationContext(),
//	                         DisplayMessage.class);
//	                     startActivity(myIntent1);
//	                 } else {
//	                     Toast.makeText(getApplicationContext(),
//	                         "Key Wrong!", Toast.LENGTH_SHORT).show();
//	                 }
//				}
//			});
//		 alertDialog.show();	
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		{
    	alert.setTitle("Alert Dialog With EditText"); //Set Alert dialog title here
    	alert.setMessage("Enter Your Name Here"); //Message here

        // Set an EditText view to get user input 
        final EditText input = new EditText(context);
        alert.setView(input);

    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() 
    	{
    	public void onClick(DialogInterface dialog, int whichButton)
    	{
    	 
    	 password = input.getEditableText().toString();
        
         if (password.equals(key))
         {
           Toast.makeText(getApplicationContext(), "Key Matched", Toast.LENGTH_SHORT).show();
              Intent myIntent1 = new Intent(getApplicationContext(),DisplayMessage.class);
             startActivity(myIntent1);
            
         } else 
         {
              Toast.makeText(getApplicationContext(),
                  "Key Wrong! Try Again", Toast.LENGTH_SHORT).show();
         } 
    	 
    	 
    	} // End of onClick(DialogInterface dialog, int whichButton)
    }); //End of alert.setPositiveButton
    	
    	AlertDialog alertDialog = alert.create();
    	alertDialog.show();
		}
		 
	}


	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String newMessage = intent.getExtras().getString(Configuration.EXTRA_MESSAGE);
			String newName = intent.getExtras().getString("name");
			String newIMEI = intent.getExtras().getString("imei");
			
			Log.i("GCMBroadcast","Broadcast called."+newIMEI);
			
			
			aController.acquireWakeLock(getApplicationContext());
			
			String msg = lblMessage.getText().toString();
			msg = newName+" : "+newMessage+"\n"+msg;
			
			lblMessage.setText(msg);
			
			
			
			Toast.makeText(getApplicationContext(), 
					"Got Message: " + newMessage, 
					Toast.LENGTH_LONG).show();
			
		
			 int rowCount = DataBaseAdapter.validateNewMessageUserData(newIMEI);
			 Log.i("GCMBroadcast", "rowCount:"+rowCount);
             if(rowCount <= 1 )
             {
		        	final UserData schedSpinner = new UserData();
		            
		          
		        	schedSpinner.setName(newName);
		        	schedSpinner.setIMEI(newIMEI);
		             
		              
		          
		          CustomListViewValuesArr.add(schedSpinner);
		          adapter.notifyDataSetChanged();
		          
		        }
		        
		      
			aController.releaseWakeLock();
		}
	};
	
	
	/*********** Send message *****************/
	
	public class LongOperation  extends AsyncTask<String, Void, String> 
	{
        
    	
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(DisplayMessage.this); 
        String data  = ""; 
        int sizeData = 0;  
        
        
        protected void onPreExecute() 
        {
           
           
            Dialog.setMessage("Please wait..");
            Dialog.show();
            
        }
 
      
        protected String doInBackground(String... params)
        
        {
        	
        	
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
	
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
	@Override
	protected void onDestroy() {
		try {
			
			unregisterReceiver(mHandleMessageReceiver);
			
			
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}

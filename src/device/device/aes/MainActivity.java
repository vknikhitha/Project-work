package device.device.aes;

import android.app.Activity;
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
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	
	TextView lblMessage;
	ControllerApplication aController;
	
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	public static String name;
	public static String email;
	public static String imei;

	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		aController = (ControllerApplication) getApplicationContext();
		
		
		
		if (!aController.isConnectingToInternet()) {
			
		
			aController.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to Internet connection", false);
			
			return;
		}
		
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
		 
		 
		Intent i = getIntent();
		
		name = i.getStringExtra("name");
		email = i.getStringExtra("email");		
		imei  = deviceIMEI;
		
		GCMRegistrar.checkDevice(this);

		
		GCMRegistrar.checkManifest(this);

		lblMessage = (TextView) findViewById(R.id.lblMessage);
		
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Configuration.DISPLAY_REGISTRATION_MESSAGE_ACTION));
		
		
		final String regId = GCMRegistrar.getRegistrationId(this);

		
		if (regId.equals("")) {
			
			 Log.i("GCM K", "--- Regid = ''"+regId);
				
			GCMRegistrar.register(this, Configuration.GOOGLE_SENDER_ID);
			
		} else {
			
			
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				
				final Context context = this;
							
				Toast.makeText(getApplicationContext(), 
						"Already registered with GCM Server", 
						Toast.LENGTH_LONG).show();
				Log.i("GCM K", "Already registered with GCM Server");
				
				
				
			} else {
				
				Log.i("GCM K", "-- gO for registration--");
				
			
				final Context context = this;
				
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						
					
						aController.register(context, name, email, regId,imei); 
						
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
						
						finish();
					}

				};
				
				
				mRegisterTask.execute(null, null, null);
			}
		}
	}		

	 
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String newMessage = intent.getExtras().getString(Configuration.EXTRA_MESSAGE);
			
			
			aController.acquireWakeLock(getApplicationContext());
			
			
			lblMessage.append(newMessage + "\n");			
			
			Toast.makeText(getApplicationContext(), 
					"Got Message: " + newMessage, 
					Toast.LENGTH_LONG).show();
			
			
			aController.releaseWakeLock();
		}
	};
	
	@Override
	protected void onDestroy() {
		
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			
			unregisterReceiver(mHandleMessageReceiver);
			
			
			GCMRegistrar.onDestroy(this);
			
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}

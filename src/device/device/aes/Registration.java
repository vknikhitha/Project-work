package device.device.aes;

import device.device.aes.DataBaseAdapter;
import device.device.aes.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Registration extends Activity {
	
	
	EditText txtName; 
	EditText txtEmail;
	
	
	Button btnRegister;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	
		DataBaseAdapter.init(this);
		
		setContentView(R.layout.activity_register);
		
		
		final ControllerApplication aController = (ControllerApplication) getApplicationContext();
		
		
		if (!aController.isConnectingToInternet()) {
			
			
			aController.showAlertDialog(Registration.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			
			
			return;
		}

		
		if (Configuration.YOUR_SERVER_URL == null || 
			Configuration.GOOGLE_SENDER_ID == null || 
			Configuration.YOUR_SERVER_URL.length() == 0 || 
			Configuration.GOOGLE_SENDER_ID.length() == 0) 
		{
			
			
			aController.showAlertDialog(Registration.this, 
					"Configuration Error!",
					"Please set your Server URL and GCM Sender ID", 
					false);
			
			
			 return;
		}
		
		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
	
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {  
				
				String name = txtName.getText().toString(); 
				String email = txtEmail.getText().toString(); 
				
			
				if(name.trim().length() > 0 && email.trim().length() > 0){
					
					
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					
					
					i.putExtra("name", name);
					i.putExtra("email", email);
					startActivity(i);
					finish();
					
				}else{
					
					
					
					aController.showAlertDialog(Registration.this, 
							"Registration Error!", 
							"Please enter your details", 
							false);
				}
			}
		});
	}

}

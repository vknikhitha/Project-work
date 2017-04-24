package device.device.aes;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import register.login.encrypt.LoginActivity;
import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
 
public class Splash_Activity extends Activity {

	long Delay = 5000;

 
@Override
public void onCreate(Bundle savedInstanceState) 
{
super.onCreate(savedInstanceState);
String eol = System.getProperty("line.separator");
try {
  BufferedWriter writer = 
      new BufferedWriter(new OutputStreamWriter(openFileOutput("myfile", 
          MODE_WORLD_WRITEABLE)));
  writer.write("This is a test1." + eol);
  writer.write("This is a test2." + eol);
  writer.write("This is a test3." + eol);
  writer.write("This is a test4." + eol);
  writer.write("This is a test5." + eol);
  writer.close();
} catch (Exception e) {
  e.printStackTrace();
}

requestWindowFeature(Window.FEATURE_NO_TITLE);
setContentView(R.layout.activity_splash);
//Create a Timer
		Timer RunSplash = new Timer();

		// Task to do when the timer ends
		TimerTask ShowSplash = new TimerTask() {
			@Override
			public void run() {
				// Close SplashScreenActivity.class
				finish();

				// Start MainActivity.class
				Intent myIntent = new Intent(Splash_Activity.this,
						LoginActivity.class);
				startActivity(myIntent);
			}
		};

		// Start the timer
		RunSplash.schedule(ShowSplash, Delay);
	}
}

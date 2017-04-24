package device.device.aes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;
import device.device.aes.R;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	private ControllerApplication aController = null;
	String message = "";

	public GCMIntentService() {

		super(Configuration.GOOGLE_SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {

		if (aController == null)
			aController = (ControllerApplication) getApplicationContext();

		Log.i(TAG, "---------- onRegistered -------------");
		Log.i(TAG, "Device registered: regId = " + registrationId);
		aController.displayRegistrationMessageOnScreen(context,
				"Your device registred with GCM");
		Log.d("NAME", MainActivity.name);

		aController.register(context, MainActivity.name, MainActivity.email,
				registrationId, MainActivity.imei);

		DataBaseAdapter.addDeviceData(MainActivity.name, MainActivity.email,
				registrationId, MainActivity.imei);

	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		if (aController == null)
			aController = (ControllerApplication) getApplicationContext();
		Log.i(TAG, "---------- onUnregistered -------------");
		Log.i(TAG, "Device unregistered");
		aController.displayRegistrationMessageOnScreen(context,
				getString(R.string.gcm_unregistered));
		aController.unregister(context, registrationId, MainActivity.imei);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		if (aController == null)
			aController = (ControllerApplication) getApplicationContext();

		Log.i(TAG, "---------- onMessage -------------");
		message = intent.getExtras().getString("message");
		
		

		Log.i("GCM111", "message123 : " + message);

		String[] StringAll;
		StringAll = message.split("\\^");

		String title = "";
		String imei = "";

		int StringLength = StringAll.length;
		if (StringLength > 0) {

			title = StringAll[0];
			imei = StringAll[1];
			message = StringAll[2];
		}
		try {
			Log.d("before Decrypt : ", message);
			
		//	EAXTest

			message = Encryptor.decrypt("my secret text", message);

			Log.d("After Decrypt : ", message);

		} catch (Exception e) {

			e.printStackTrace();
		}

		aController.displayMessageOnScreen(context, title, message, imei);

		UserData userdata = new UserData(1, imei, title, message);
		DataBaseAdapter.addUserData(userdata);

		generateNotification(context, title, message, imei);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {

		if (aController == null)
			aController = (ControllerApplication) getApplicationContext();

		Log.i(TAG, "---------- onDeletedMessages -------------");
		String message = getString(R.string.gcm_deleted, total);

		String title = "DELETED";

		generateNotification(context, title, message, "");
	}

	@Override
	public void onError(Context context, String errorId) {

		if (aController == null)
			aController = (ControllerApplication) getApplicationContext();

		Log.i(TAG, "---------- onError -------------");
		Log.i(TAG, "Received error: " + errorId);

		aController.displayRegistrationMessageOnScreen(context,
				getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {

		if (aController == null)
			aController = (ControllerApplication) getApplicationContext();

		Log.i(TAG, "---------- onRecoverableError -------------");

		Log.i(TAG, "Received recoverable error: " + errorId);
		aController.displayRegistrationMessageOnScreen(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String title,
			String message, String imei) {
		int icon = R.drawable.user_thumb;

		String msginfo = message;

		String list[] = msginfo.split(",");

		String msg1 = list[0];
		String key1=list[1];
		
		SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE); 
		Editor editor = pref.edit();
		
		editor.putString("msg",msg1);
		editor.putString("key",key1);
		editor.commit(); 

		long when = System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon,msg1, when);

		Intent notificationIntent = new Intent(context, ViewMsg.class);
		// set intent so it does not start a new activity

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.putExtra("name", title);
		notificationIntent.putExtra("message",msg1);
		notificationIntent.putExtra("imei", imei);

		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title,msg1, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notificationManager.notify(0, notification);

	}

}

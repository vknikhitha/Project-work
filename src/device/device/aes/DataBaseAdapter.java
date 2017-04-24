package device.device.aes;

import java.util.ArrayList;
import java.util.List;

import device.device.aes.UserData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBaseAdapter {
	
	
	public static final boolean DEBUG = true;
	
	
	public static final String LOG_TAG = "DBAdapter";
		
	public static final String KEY_ID = "_id";

	public static final String KEY_USER_IMEI    = "user_imei";

	public static final String KEY_USER_NAME    = "user_name";
	
	public static final String KEY_USER_MESSAGE = "user_message";

	public static final String KEY_DEVICE_IMEI  = "device_imei";
	
	public static final String KEY_DEVICE_NAME  = "device_name";

	public static final String KEY_DEVICE_EMAIL = "device_email";
	
	public static final String KEY_DEVICE_REGID = "device_regid";
	
	

	public static final String DATABASE_NAME = "DB_sqllite";
	
	
	public static final int DATABASE_VERSION = 1;// started at 1


	public static final String USER_TABLE = "tbl_user";
	public static final String DEVICE_TABLE = "tbl_device";
	

	private static final String[] ALL_TABLES = { USER_TABLE,DEVICE_TABLE };
	
	
	
	private static final String USER_CREATE = "create table tbl_user(_id integer primary key autoincrement, user_name text not null, user_imei text not null, user_message text not null);";
	private static final String DEVICE_CREATE = "create table tbl_device(_id integer primary key autoincrement, device_name text not null, device_email text not null, device_regid text not null, device_imei text not null);";
	
	private static DataBaseHelper DBHelper = null;

	protected DataBaseAdapter() {
	}
    /******************* Initialize database *************/
	public static void init(Context context) {
		if (DBHelper == null) {
			if (DEBUG)
				Log.i("DBAdapter", context.toString());
			DBHelper = new DataBaseHelper(context);
		}
	}
	
  
	private static class DataBaseHelper extends SQLiteOpenHelper {
		public DataBaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if (DEBUG)
				Log.i(LOG_TAG, "new create");
			try {
				
				db.execSQL(USER_CREATE);
				db.execSQL(DEVICE_CREATE);

			} catch (Exception exception) {
				if (DEBUG)
					Log.i(LOG_TAG, "Exception onCreate() exception");
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (DEBUG)
				Log.w(LOG_TAG, "Upgrading database from version" + oldVersion
						+ "to" + newVersion + "...");

			for (String table : ALL_TABLES) {
				db.execSQL("DROP TABLE IF EXISTS " + table);
			}
			onCreate(db);
		}

	} 
	
	
	
	private static synchronized SQLiteDatabase open() throws SQLException {
		return DBHelper.getWritableDatabase();
	}


	
	
	public static void addDeviceData(String DeviceName, String DeviceEmail,String DeviceRegID,String DeviceIMEI) {
	    try{
			final SQLiteDatabase db = open();
	    	
	    	String imei  = sqlEscapeString(DeviceIMEI);
	    	String name  = sqlEscapeString(DeviceName);
			String email = sqlEscapeString(DeviceEmail);
			String regid = sqlEscapeString(DeviceRegID);
			
			ContentValues cVal = new ContentValues();
			cVal.put(KEY_DEVICE_IMEI, imei);
			cVal.put(KEY_DEVICE_NAME, name);
			cVal.put(KEY_DEVICE_EMAIL, email);
			cVal.put(KEY_DEVICE_REGID, regid);
			
			db.insert(DEVICE_TABLE, null, cVal);
	        db.close(); 
	    } catch (Throwable t) {
			Log.i("Database", "Exception caught: " + t.getMessage(), t);
		}
    }
	
	

 
    public static void addUserData(UserData uData) {
    	try{
		    	final SQLiteDatabase db = open();
		    	
		    	String imei  = sqlEscapeString(uData.getIMEI());
		    	String name  = sqlEscapeString(uData.getName());
		    	String message  = sqlEscapeString(uData.getMessage());
				
				ContentValues cVal = new ContentValues();
				cVal.put(KEY_USER_IMEI, imei);
				cVal.put(KEY_USER_NAME, name);
				cVal.put(KEY_USER_MESSAGE, message);
				db.insert(USER_TABLE, null, cVal);
		        db.close(); 
	    } catch (Throwable t)
	    {
			Log.i("Database Exception", "Exception caught: " + t.getMessage(), t);
		}
    }
 
   
    public static UserData getUserData(int id) {
    	final SQLiteDatabase db = open();
 
        Cursor cursor = db.query(USER_TABLE, new String[] { KEY_ID,
        		KEY_USER_NAME, KEY_USER_IMEI,KEY_USER_MESSAGE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        UserData data = new UserData(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
     
        return data;
    }
 
    
    public static List<UserData> getAllUserData() {
        List<UserData> contactList = new ArrayList<UserData>();
       
        String selectQuery = "SELECT  * FROM " + USER_TABLE+" ORDER BY "+KEY_ID+" desc";
 
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
       
        if (cursor.moveToFirst()) {
            do {
            	UserData data = new UserData();
            	data.setID(Integer.parseInt(cursor.getString(0)));
            	data.setName(cursor.getString(1));
            	data.setIMEI(cursor.getString(2));
            	data.setMessage(cursor.getString(3));
             
                contactList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
       
        return contactList;
    }
    
   
    public static int getUserDataCount() {
        String countQuery = "SELECT  * FROM " + USER_TABLE;
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(countQuery, null);
        
        int count = cursor.getCount();
        cursor.close();
        
     
        return count;
    }
    
  
    public static int validateDevice() {
        String countQuery = "SELECT  * FROM " + DEVICE_TABLE;
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(countQuery, null);
        
        int count = cursor.getCount();
        cursor.close();
        
     
        return count;
    }
    
   
    public static List<UserData> getDistinctUser() {
    	List<UserData> contactList = new ArrayList<UserData>();
     
        String selectQuery = "SELECT  distinct(user_imei),user_name FROM " + USER_TABLE+" ORDER BY "+KEY_ID+" desc";
        
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        
        if (cursor.moveToFirst()) {
            do {
            	UserData data = new UserData();
            	
            	data.setIMEI(cursor.getString(0));
            	data.setName(cursor.getString(1));
            	
                contactList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        return contactList;
    }
    
     
    public static int validateNewMessageUserData(String IMEI) {
    	 int count = 0;
    	try {
	        String countQuery = "SELECT "+KEY_ID+" FROM " + USER_TABLE + " WHERE user_imei='"+IMEI+"'";
	        final SQLiteDatabase db = open();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        
	        count = cursor.getCount();
	        cursor.close();
    	} catch (Throwable t) {
    		count = 10;
			Log.i("Database", "Exception caught: " + t.getMessage(), t);
		}
        return count;
    }

    
	
	private static String sqlEscapeString(String aString) {
		String aReturn = "";
		
		if (null != aString) {
			
			aReturn = DatabaseUtils.sqlEscapeString(aString);
			
			aReturn = aReturn.substring(1, aReturn.length() - 1);
		}
		
		return aReturn;
	}
	
	private static String sqlUnEscapeString(String aString) {
		
		String aReturn = "";
		
		if (null != aString) {
			aReturn = aString.replace("''", "'");
		}
		
		return aReturn;
	}
	
	
	
}

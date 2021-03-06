package de.syncdroid.service;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import de.syncdroid.SyncBroadcastReceiver;
import de.syncdroid.activity.LocationEditActivity;

public class LocationDiscoveryService extends Service {
	private static final String TAG = "SyncDroid.LocationDiscoveryService";
	private static final int POLL_INTERVALL = 5000;
	
	public static final String ACTION_TIMER_TICK               = "de.syncdroid.ACTION_TIMER_TICK";
	public static final String ACTION_COLLECT_CELL_IDS         = "de.syncdroid.ACTION_COLLECT_CELL_IDS";
	public static final String ACTION_STOP_COLLECTING_CELL_IDS = "de.syncdroid.ACTION_STOP_COLLECTING_CELL_IDS";
	public static final String ACTION_CELL_CHANGED             = "de.syncdroid.ACTION_CELL_CHANGED";
	
	private GsmCellLocation currentCellLocation = null;

    private PendingIntent pendingIntent;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    public static final int FOUND_NEW_CELL = 3;
    
    private void sendCellLocation() {
		TelephonyManager tm = (TelephonyManager) 
				getSystemService(Activity.TELEPHONY_SERVICE); 
        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();

        if(location == null) {
            Log.e(TAG, "could not send cell location: telephoneManager returned null");
            return ;
        }

    	Intent cellChangedIntent = new Intent();
    	cellChangedIntent.setAction(ACTION_CELL_CHANGED);
    	cellChangedIntent.putExtra(
    			LocationEditActivity.EXTRA_CELL_CID, location.getCid());
    	cellChangedIntent.putExtra(
    			LocationEditActivity.EXTRA_CELL_LAC, location.getLac());
    	
    	sendBroadcast(cellChangedIntent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
    	super.onStart(intent, startId);
    			
		// handle intents
		if( intent != null && intent.getAction() != null ) {
			Log.i(TAG, "Received intent= " + intent + " with action '" 
					+ intent.getAction() + "'");

			if( intent.getAction().equals(ACTION_TIMER_TICK)  )
			{
				Log.d(TAG, "ACTION_TIMER_TICK");
				
				TelephonyManager tm = (TelephonyManager)
						getSystemService(Activity.TELEPHONY_SERVICE); 
		        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
		        
		        if (currentCellLocation == null || !currentCellLocation.equals(location)) {
		        	Log.i(TAG, "new cell location: " + location);
		        	currentCellLocation = location;
		        	
		        	sendCellLocation();
		        }
		        
			}
			else if(intent.getAction().equals(ACTION_COLLECT_CELL_IDS))
			{
				Log.d(TAG, "enable location timer");
				AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
				Intent i=new Intent(this, SyncBroadcastReceiver.class);
				i.setAction(ACTION_TIMER_TICK);

				// get a Calendar object with current time
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, 4);

				pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
				mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime(), POLL_INTERVALL, pendingIntent);
				sendCellLocation();
			}
			else if(intent.getAction().equals(ACTION_STOP_COLLECTING_CELL_IDS))
			{
				Log.d(TAG, "disable location timer");
				AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.cancel(pendingIntent);
			}
			else if(intent.getAction().equals(ACTION_COLLECT_CELL_IDS))
			{
			} else {
				Log.w(TAG, "unknown intent with action '" + intent.getAction() + "': " + intent);
			}
		}
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

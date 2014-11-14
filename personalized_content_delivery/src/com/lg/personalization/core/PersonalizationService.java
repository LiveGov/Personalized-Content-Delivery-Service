package com.lg.personalization.core;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.lg.personalization.configuration.Constants;
import com.lg.personalization.models.MyPlacesModel;
import com.lg.personalizationservice.R;

public class PersonalizationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	private DatabaseHandler dbHandler;
	private static final String TAG = "PersonalizationService";

	private static final int WIFI_DISABLED = 2614;
	public static final String INTENT_PLACE_ID = "PLACE_ID";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		if(dbHandler == null)
		{
			dbHandler = new DatabaseHandler(this);
		}
//		Calendar calendar = Calendar.getInstance();
//		// 9 AM
//		calendar.set(Calendar.HOUR_OF_DAY, 12);
//		calendar.set(Calendar.MINUTE, 6);
//		calendar.set(Calendar.SECOND, 0);
//		PendingIntent pi = PendingIntent.getBroadcast(this, ALARM_BROADCAST, new Intent(this, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
//		PendingIntent pi = PendingIntent.getService(this, 0,
//		            new Intent(this, AlarmReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
//		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//		                                AlarmManager.INTERVAL_DAY, pi);
//		am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30000, pi);
		if(mLocationRequest == null)
		{
			mLocationRequest = new LocationRequest();
			mLocationRequest.setFastestInterval(Constants.INTERVAL_MINUTES * 60 * 1000);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
		}
		if(mLocationClient == null)
		{
			mLocationClient = new LocationClient(this, this, this);
			mLocationClient.connect();
		}
		if(intent != null)
		{
			Log.i(TAG, "received intent != null");
			if(intent.getBooleanExtra("CHARGER_CONNECTED", false)){
				Log.i(TAG, "received Charger connected");
				dbHandler.addPlaces();
				dbHandler.fixCenters();
				registerAlarms(dbHandler.getPlacesFiltered());
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		WifiManager wf = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if(wf.isWifiEnabled()){
			dbHandler.addPoint(location.getLatitude(), location.getLongitude(), location.getAccuracy(), System.currentTimeMillis()/1000);
		}
		else{
			final NotificationManager mNotifyManager =
	                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
	        Intent intent = new Intent();
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        PendingIntent contentIntent = PendingIntent.getActivity(
	        	    getApplicationContext(),
	        	    0,
	        	    intent, 
	        	    PendingIntent.FLAG_UPDATE_CURRENT);
	        mBuilder.setContentTitle(getString(R.string.wifi_disabled_notification_title))
	                .setContentText(getString(R.string.wifi_disabled_notification_text))
	                .setContentIntent(contentIntent)
	                .setSmallIcon(R.drawable.ic_stat_logo);
	        mNotifyManager.notify(WIFI_DISABLED, mBuilder.build());
		}
	}
	
	public void registerAlarms(List<MyPlacesModel> places)
	{
		for(MyPlacesModel place : places)
		{
			if(!place.startTime.equals("N/A")){
//				int hours = Integer.parseInt(place.startTime.charAt(0) + place.startTime.charAt(1));
//				int minutes = Integer.parseInt(place.startTime.substring(3, 4))
				int hours = Integer.parseInt(place.startTime.substring(0, 2));
				if(hours -2 < 0) hours = 24+(hours-2);
				int minutes = Integer.parseInt(place.startTime.substring(3, 5));
				Log.i(TAG, "hours = " + hours);
				Log.i(TAG, "minutes = " + minutes);
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				calendar.set(Calendar.HOUR_OF_DAY, hours - Constants.TRIP_NOTIFICATION_SHOW_BEFORE_HOURS);
				calendar.set(Calendar.MINUTE, minutes - Constants.TRIP_NOTIFICATION_SHOW_BEFORE_MINUTES);
//				calendar.set(Calendar.HOUR_OF_DAY, 1);
//				calendar.set(Calendar.MINUTE, 1);
				calendar.set(Calendar.SECOND, 0);
				Intent intent = new Intent(this, AlarmReceiver.class);
				intent.putExtra(INTENT_PLACE_ID, place.placeid);
				PendingIntent pi = PendingIntent.getBroadcast(this, (int) place.placeid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//				am.cancel(pi);
				//repeat everyday
				am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400000 , pi);//1 day interval
			}	
		}
	}

}

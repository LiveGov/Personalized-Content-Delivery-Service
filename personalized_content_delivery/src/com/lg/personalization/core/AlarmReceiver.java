package com.lg.personalization.core;

import com.lg.personalization.configuration.Constants;
import com.lg.personalization.models.MyPlacesModel;
import com.lg.personalization.models.PastNotificationModel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "AlarmReceiver";
	public static final String INTENT_EXTRA_PLACE_ID = "PLACE_ID";
	@Override
	public void onReceive(Context context, Intent intent) {
		long placeID = intent.getLongExtra(PersonalizationService.INTENT_PLACE_ID, -1);
		showNotification(context, placeID);
	}
	
	private void showNotification(Context context, final long placeid)
	{
		DatabaseHandler dbHandler = new DatabaseHandler(context);
		MyPlacesModel place = dbHandler.getPlaceById((int)placeid);
		if(place == null) return;
		final NotificationManager mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//        Intent intent = new Intent(context, RouteActivity.class);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.putExtra(INTENT_EXTRA_PLACE_ID, (int) placeid);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent contentIntent = PendingIntent.getActivity(
//        	    context.getApplicationContext(),
//        	    (int) placeid,
//        	    intent, 
//        	    PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle("Trip Update")
                .setContentText("Departure for " + place.title + " (" + place.startTime + ")");
//                .setContentIntent(contentIntent);
        mNotifyManager.notify((int)placeid, mBuilder.build());
        PastNotificationModel pastNot = new PastNotificationModel((int) placeid, System.currentTimeMillis());
        dbHandler.addPastNotification(pastNot);
        new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mNotifyManager.cancel((int) placeid);
			}
		}, Constants.TRIP_NOTIFICATION_CANCEL_AFTER_MILLIS);
	}

}

package com.lg.personalization.configuration;

public class Constants {
	//Interval for collecting location points in minutes
	public static int INTERVAL_MINUTES = 10;
	//Trip update notifications will be canceled after this time has passed
	public static int TRIP_NOTIFICATION_CANCEL_AFTER_MILLIS = 7200000;
	//Minutes to send notification before arrival time (added to hours)
	public static int TRIP_NOTIFICATION_SHOW_BEFORE_MINUTES = 0;
	//Hours to send notification before arrival time 
	public static int TRIP_NOTIFICATION_SHOW_BEFORE_HOURS = 2;
	//Minimum duration spend on a place to be considered as a "Favourite Place"
	public static int MINIMUM_TIME_SPENT_IN_PLACE_SECONDS = 3600;
	//The radius used to form a favourite place
	//The points that fall inside the radius form 1 place
	public static float CLUSTER_RADIUS_METERS = 500.0f;
	//Re-visit time period - if visits distance is higher than 7200 then counted as separate
	public static long REVISIT_TIME_PERIOD_SECONDS = 7200;
	//Maximum s.deviation allowed to accept arrival and departure times in minutes
	public static float DEVIATION_THRESHOLD_MINUTES = 200.0f;
}

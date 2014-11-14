package com.lg.personalization.models;

public class LocationPoint {
	
	public double latitude,longitude,accuracy;
	public long unixTimestamp;
	public long pointid;
	public long placeid;
	
	public LocationPoint(double _latitude, double _longitude, double _accuracy, long _unixTimestamp) {
		latitude = _latitude;
		longitude = _longitude;
		accuracy = _accuracy;
		unixTimestamp = _unixTimestamp;
	}

}

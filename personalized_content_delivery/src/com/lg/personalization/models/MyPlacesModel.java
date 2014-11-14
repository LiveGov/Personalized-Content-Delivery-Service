package com.lg.personalization.models;

import com.google.android.gms.maps.model.LatLng;

public class MyPlacesModel {
	public LatLng centerCoordinate;
	public long durationSpent;
	public String startTime;
	public String endTime;
	public String title;
	public long placeid;
	public boolean hidden;

	public MyPlacesModel(LatLng center, long duration, String _startTime, String _endTime)
	{
		centerCoordinate = center;
		durationSpent = duration;
		startTime = _startTime;
		endTime = _endTime;
	}
}

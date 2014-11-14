package com.lg.personalization.models;

public class TimestampModel {
	
	public long timestampId;
	public long timestampItself;
	public long visitId;
	public long placeId;
	
	public TimestampModel(long _timestampId, long _timestampItself, long _visitId, long _placeId)
	{
		timestampId = _timestampId;
		timestampItself = _timestampItself;
		visitId = _visitId;
		placeId = _placeId;
	}

}

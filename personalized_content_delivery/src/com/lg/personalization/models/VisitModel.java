package com.lg.personalization.models;

public class VisitModel {
	public long visitId;
	public long arriveTimeId;
	public long departureTimeId;
	public long placeId;
	
	public VisitModel( long _visitId, long _arriveTimeId, long _departureTimeId, long _placeId) {
		visitId = _visitId;
		arriveTimeId = _arriveTimeId;
		departureTimeId = _departureTimeId;
		placeId = _placeId;
	}
}

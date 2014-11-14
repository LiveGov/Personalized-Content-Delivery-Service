package com.lg.personalization.models;

public class PastNotificationModel {
	
	public int id;
	public int placeid;
	public long timemillis;
	public String placeName;
	
//	public PastNotificationModel(int _id, int _placeid, long _timemillis)
//	{
//		id = _id;
//		placeid = _placeid;
//		timemillis = _timemillis;
//	}
	
	public PastNotificationModel(int _id, int _placeid, long _timemillis, String _placename)
	{
		id = _id;
		placeid = _placeid;
		timemillis = _timemillis;
		placeName = _placename;
	}
	
	public PastNotificationModel(int _placeid, long _timemillis)
	{
		placeid = _placeid;
		timemillis = _timemillis;
	}

}

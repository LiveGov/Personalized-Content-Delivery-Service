package com.lg.personalization.core;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.lg.personalization.configuration.Constants;
import com.lg.personalization.models.LineModel;
import com.lg.personalization.models.LocationPoint;
import com.lg.personalization.models.MyPlacesModel;
import com.lg.personalization.models.PastNotificationModel;
import com.lg.personalization.models.StaticStopModel;
import com.lg.personalization.models.TimestampModel;
import com.lg.personalization.models.VisitModel;
import com.lg.personalization.util.StatisticalUtils;
import com.lg.personalizationservice.R;

public class DatabaseHandler extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "Mobility.db";
	public static final int DATABASE_VERSION = 1;
	private static final String TAG = "DatabaseHandler";

	public static final String TABLE_STOPS = "stops";
	public static final String TABLE_POINTS = "points";
	public static final String TABLE_PLACES = "places";
	public static final String TABLE_VISITS = "visits";
	public static final String TABLE_TIMESTAMPS = "timestamps";
	public static final String TABLE_LINES = "lines";
	public static final String TABLE_PAST_NOTIFICATIONS = "past_notifications";

	// Stops
	final String KEY_STOP_ID = "stop_id";
	final String KEY_STOP_NAME = "stop_name";
	final String KEY_STOP_LAT = "stop_lat";
	final String KEY_STOP_LON = "stop_lon";

	// Points
	final String KEY_POINT_ID = "point_id";
	final String KEY_POINT_LAT = "latitude";
	final String KEY_POINT_LON = "longitude";
	final String KEY_POINT_ACC = "accuracy";
	final String KEY_POINT_TIMESTAMP = "timestamp_id";
	final String KEY_POINT_PLACE_ID = "place_id";

	// Places - A place should not be deleted from db just hide it
	final String KEY_PLACE_ID = "place_id";
	final String KEY_PLACE_LAT = "center_lat";
	final String KEY_PLACE_LON = "center_lon";
	final String KEY_PLACE_DURATION = "duration";
	final String KEY_PLACE_NAME = "name";
	final String KEY_PLACE_ARR_TIME = "arr_time";
	final String KEY_PLACE_DEP_TIME = "dep_time";
	final String KEY_PLACE_HIDDEN = "hidden";

	// Visits
	final String KEY_VISIT_ID = "visit_id";
	final String KEY_VISIT_ARR_TIME = "arrive_time";
	final String KEY_VISIT_DEP_TIME = "dep_time";
	final String KEY_VISIT_PLACE_ID = "place_id";

	// Timestamps
	final String KEY_TIMESTAMP_ID = "timestamp_id";
	final String KEY_TIMESTAMP = "timestamp";
	final String KEY_TIMETAMP_VISIT_ID = "visit_id";

	// Lines
	final String KEY_LINE_ID_AUTO = "line_id";
	final String KEY_LINE_ID_HSL = "line_id_hsl";
	final String KEY_LINE_SHORT_NAME = "line_shortname";
	final String KEY_LINE_LONG_NAME = "line_longname";
	final String KEY_LINE_START_NAME = "line_startname";
	final String KEY_LINE_STOP_NAME = "line_stopname";
	final String KEY_LINE_TYPE = "line_type";
	
	//Past notifications
	final String KEY_PAST_NOT_ID = "notification_id";
	final String KEY_PAST_NOT_PLACE_ID = "place_id";
	final String KEY_PAST_NOT_TIME_MILLIS = "time_millis";

	Context ctx;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String c = "CREATE TABLE " + TABLE_STOPS + "(" + KEY_STOP_ID
				+ " INTEGER PRIMARY KEY," + KEY_STOP_NAME + " TEXT,"
				+ KEY_STOP_LAT + " REAL," + KEY_STOP_LON + " REAL)";
		db.execSQL(c);

		String d = "CREATE TABLE " + TABLE_POINTS + "(" + KEY_POINT_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_POINT_LAT
				+ " REAL," + KEY_POINT_LON + " REAL," + KEY_POINT_ACC
				+ " REAL," + KEY_POINT_TIMESTAMP + " INTEGER,"
				+ KEY_POINT_PLACE_ID + " INTEGER)";
		db.execSQL(d);

		String e = "CREATE TABLE " + TABLE_PLACES + "(" + KEY_PLACE_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PLACE_LAT
				+ " REAL," + KEY_PLACE_LON + " REAL," + KEY_PLACE_NAME
				+ " TEXT," + KEY_PLACE_DURATION + " INTEGER,"
				+ KEY_PLACE_ARR_TIME + " TEXT," + KEY_PLACE_DEP_TIME + " TEXT,"
				+ KEY_PLACE_HIDDEN + " INTEGER)";
		db.execSQL(e);

		String f = "CREATE TABLE " + TABLE_VISITS + "(" + KEY_VISIT_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_VISIT_ARR_TIME
				+ " INTEGER," + KEY_VISIT_DEP_TIME + " INTEGER,"
				+ KEY_VISIT_PLACE_ID + " INTEGER)";
		db.execSQL(f);

		String g = "CREATE TABLE " + TABLE_TIMESTAMPS + "(" + KEY_TIMESTAMP_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TIMESTAMP
				+ " INTEGER," + KEY_TIMETAMP_VISIT_ID + " INTEGER)";
		db.execSQL(g);

		String h = "CREATE TABLE " + TABLE_LINES + "(" + KEY_LINE_ID_AUTO
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LINE_ID_HSL
				+ " TEXT," + KEY_LINE_LONG_NAME + " TEXT,"
				+ KEY_LINE_SHORT_NAME + " TEXT," + KEY_LINE_START_NAME
				+ " TEXT," + KEY_LINE_STOP_NAME + " TEXT," + KEY_LINE_TYPE + " INTEGER)";

		db.execSQL(h);
		
		String i = "CREATE TABLE " + TABLE_PAST_NOTIFICATIONS + "(" + KEY_PAST_NOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ KEY_PAST_NOT_PLACE_ID + " INTEGER, " 
				+ KEY_PAST_NOT_TIME_MILLIS + " INTEGER)";
		db.execSQL(i);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
		// Create tables again
		onCreate(db);

	}

	public boolean isTableExists(String tableName, boolean openDb) {
		// if(openDb) {
		// if(mDatabase == null || !mDatabase.isOpen()) {
		// mDatabase = getReadableDatabase();
		// }
		//
		// if(!mDatabase.isReadOnly()) {
		// mDatabase.close();
		// mDatabase = getReadableDatabase();
		// }
		// }

		Cursor cursor = getReadableDatabase().rawQuery(
				"select DISTINCT tbl_name from sqlite_master where tbl_name = '"
						+ tableName + "'", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	/*
	 * the approach: get all stops with difference from user user latitude +-
	 * 0.005887 and longitude +- 0.021076 this returns all stops with at least
	 * 500m Distance from user on the returned results use java objects for more
	 * accurate distance and reapply filter to 500m.
	 */

	public static final double TEST_LAT = 60.189025;
	public static final double TEST_LON = 24.955765;
	// public static final float DISTANCE_FILTER = 2600.0f;

	public int getStopsCount() {
		Cursor mCount = getReadableDatabase().rawQuery(
				"select count(*) from " + TABLE_STOPS, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public List<StaticStopModel> getStops() {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<StaticStopModel> stops = new ArrayList<StaticStopModel>();
		Cursor cursor = db.rawQuery("select * from " + TABLE_STOPS + ";", null);
		if (cursor.moveToFirst()) {
			do {
				StaticStopModel stop = new StaticStopModel(cursor.getInt(0),
						cursor.getString(1), cursor.getDouble(2),
						cursor.getDouble(3));
				stops.add(stop);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return stops;
	}

	public void addStops(List<StaticStopModel> stops, Context ctx) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		for (int i = 0; i < stops.size(); i++) {
			db.execSQL("INSERT INTO " + TABLE_STOPS + " VALUES("
					+ stops.get(i).id + ", \'" + stops.get(i).name + "\', "
					+ stops.get(i).coord.latitude + ", "
					+ stops.get(i).coord.longitude + ");");
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public List<LocationPoint> getPoints() {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<LocationPoint> points = new ArrayList<LocationPoint>();
		Cursor cursor = db
				.rawQuery("select * from " + TABLE_POINTS + ";", null);
		if (cursor.moveToFirst()) {
			do {
				LocationPoint point = new LocationPoint(cursor.getDouble(1),
						cursor.getDouble(2), cursor.getDouble(3),
						cursor.getLong(4));
				points.add(point);
				point.placeid = cursor.getLong(5);
				point.pointid = cursor.getLong(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return points;
	}

	public List<LocationPoint> getAssignedPoints() {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<LocationPoint> points = new ArrayList<LocationPoint>();
		Cursor cursor = db.rawQuery("select * from " + TABLE_POINTS + " where "
				+ KEY_POINT_PLACE_ID + "!=0;", null);
		if (cursor.moveToFirst()) {
			do {
				LocationPoint point = new LocationPoint(cursor.getDouble(1),
						cursor.getDouble(2), cursor.getDouble(3),
						cursor.getLong(4));
				points.add(point);
				point.placeid = cursor.getLong(5);
				point.pointid = cursor.getLong(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return points;
	}
	
	public boolean linesParsed()
	{
		SQLiteDatabase db = getWritableDatabase();
		Cursor c;
		try{
			c = db.rawQuery("SELECT * FROM " + TABLE_LINES, null);
		}
		catch(SQLiteException e)
		{
			String sql =  "CREATE TABLE " + TABLE_LINES + "(" + KEY_LINE_ID_AUTO
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LINE_ID_HSL
					+ " TEXT," + KEY_LINE_LONG_NAME + " TEXT,"
					+ KEY_LINE_SHORT_NAME + " TEXT," + KEY_LINE_START_NAME
					+ " TEXT," + KEY_LINE_STOP_NAME + " TEXT," + KEY_LINE_TYPE + " INTEGER)";
			db.execSQL(sql);
			return linesParsed();
		}
		if(c.getCount() > 2)
			return true;
		else
			return false;
	}
	
	
	
	public PastNotificationModel addPastNotification(PastNotificationModel notification)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		cv.put(KEY_PAST_NOT_PLACE_ID, notification.placeid);
		cv.put(KEY_PAST_NOT_TIME_MILLIS, notification.timemillis);
		long notid = db.insert(TABLE_PAST_NOTIFICATIONS, null, cv);
		db.setTransactionSuccessful();
		db.endTransaction();
		notification.id = (int) notid;
		return notification;
	}
	
	public void deletePastNotifications()
	{
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		db.delete(TABLE_PAST_NOTIFICATIONS, null, null);
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public List<PastNotificationModel> getPastNotifications()
	{
		SQLiteDatabase db = getReadableDatabase();
		try{
			Cursor c = db.rawQuery("SELECT " + KEY_PAST_NOT_ID + "," + TABLE_PAST_NOTIFICATIONS + "." +  KEY_PAST_NOT_PLACE_ID + "," + KEY_PAST_NOT_TIME_MILLIS + "," + KEY_PLACE_NAME + " FROM " + TABLE_PAST_NOTIFICATIONS 
					+ " INNER JOIN " + TABLE_PLACES + " ON " + TABLE_PAST_NOTIFICATIONS + "." + KEY_PAST_NOT_PLACE_ID + "=" + TABLE_PLACES + "." + KEY_PLACE_ID, null);
			ArrayList<PastNotificationModel> pastNotifications = new ArrayList<PastNotificationModel>();
			if(c.moveToFirst()) {
				do {
					PastNotificationModel pastNot = new PastNotificationModel(c.getInt(0), c.getInt(1), c.getLong(2), c.getString(3));
					pastNotifications.add(pastNot);
				} while (c.moveToNext());
			}
			c.close();
			return pastNotifications;
		}
		catch (SQLiteException e) {
			String sql = "CREATE TABLE " + TABLE_PAST_NOTIFICATIONS + "(" 
					+ KEY_PAST_NOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ KEY_PAST_NOT_PLACE_ID + " INTEGER,"
					+ KEY_PAST_NOT_TIME_MILLIS + " INTEGER)";
			db.execSQL(sql);
			return getPastNotifications();
		}
	}
	public void addLines(List<LineModel> lines)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		ContentValues cv;
		for(LineModel line : lines)
		{
			cv = new ContentValues();
			cv.put(KEY_LINE_ID_HSL, line.id);
			cv.put(KEY_LINE_LONG_NAME, line.longName);
			cv.put(KEY_LINE_SHORT_NAME, line.shortName);
			cv.put(KEY_LINE_START_NAME, line.startName);
			cv.put(KEY_LINE_STOP_NAME, line.stopName);
			cv.put(KEY_LINE_TYPE, line.type.ordinal());
			db.insert(TABLE_LINES, null, cv);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public List<LineModel> getLines()
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LINES, null);
		ArrayList<LineModel> lines = new ArrayList<LineModel>();
		if(c.moveToFirst()) {
			do {
				LineModel line = new LineModel("" + c.getString(1), c.getString(3), c.getString(2), c.getString(4), c.getString(5), LineModel.Type.values()[c.getInt(6)]);
				lines.add(line);
			} while (c.moveToNext());
		}
		c.close();
		return lines;
	}

	// Step 1 - Add points
	public void addPoint(double latitude, double longitude, double accuracy,
			long timestamp) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		ContentValues tcv = new ContentValues();
		tcv.put(KEY_TIMESTAMP, timestamp);
		tcv.put(KEY_VISIT_ID, 0);
		long timestampID = db.insert(TABLE_TIMESTAMPS, null, tcv);
		ContentValues pointcv = new ContentValues();
		pointcv.put(KEY_POINT_TIMESTAMP, timestampID);
		pointcv.put(KEY_POINT_LAT, latitude);
		pointcv.put(KEY_POINT_LON, longitude);
		pointcv.put(KEY_POINT_PLACE_ID, 0);
		pointcv.put(KEY_POINT_ACC, accuracy);
		long placeID = db.insert(TABLE_POINTS, null, pointcv);
		db.setTransactionSuccessful();
		db.endTransaction();
		// db.insert(table, nullColumnHack, values)
		// db.execSQL("INSERT INTO " + TABLE_POINTS + " VALUES( NULL, " +
		// latitude + ", " + longitude + ", " + accuracy + ", " + timestamp/1000
		// + ");");
		// db.setTransactionSuccessful();
		// db.endTransaction();
	}

	public List<MyPlacesModel> getPlacesFiltered() {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<MyPlacesModel> places = new ArrayList<MyPlacesModel>();
		Cursor cursor = db.rawQuery("select * from " + TABLE_PLACES + " where "
				+ KEY_PLACE_DURATION + ">" + Constants.MINIMUM_TIME_SPENT_IN_PLACE_SECONDS + " AND " + KEY_PLACE_HIDDEN
				+ "=0 ORDER BY " + KEY_PLACE_DURATION + " DESC;", null);
		if (cursor.moveToFirst()) {
			do {
				LatLng center = new LatLng(cursor.getDouble(1),
						cursor.getDouble(2));
				long place_id = cursor.getLong(0);
				String name = cursor.getString(3);
				long duration = cursor.getLong(4);
				String arrTime = cursor.getString(5);
				String depTime = cursor.getString(6);
				MyPlacesModel place = new MyPlacesModel(center, duration,
						arrTime, depTime);
				place.title = name;
				place.placeid = place_id;
				places.add(place);
			} while (cursor.moveToNext());
			cursor.close();
		}
		return places;
	}

	public List<MyPlacesModel> getPlaces() {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<MyPlacesModel> places = new ArrayList<MyPlacesModel>();
		Cursor cursor = db
				.rawQuery("select * from " + TABLE_PLACES + ";", null);
		if (cursor.moveToFirst()) {
			do {
				LatLng center = new LatLng(cursor.getDouble(1),
						cursor.getDouble(2));
				long place_id = cursor.getLong(0);
				String name = cursor.getString(3);
				long duration = cursor.getLong(4);
				String arrTime = cursor.getString(5);
				String depTime = cursor.getString(6);
				int hidden = cursor.getInt(7);
				MyPlacesModel place = new MyPlacesModel(center, duration,
						arrTime, depTime);
				place.title = name;
				place.placeid = place_id;
				place.hidden = (hidden == 1) ? true : false;
				places.add(place);
			} while (cursor.moveToNext());
			cursor.close();
		}
		return places;
	}

	public MyPlacesModel getPlaceById(int placeid) {
		SQLiteDatabase db = getReadableDatabase();
		MyPlacesModel place = null;
		Cursor cursor = db.rawQuery("select * from " + TABLE_PLACES + " where "
				+ KEY_PLACE_ID + "=" + placeid + ";", null);
		if (cursor.moveToFirst()) {
			place = new MyPlacesModel(new LatLng(cursor.getDouble(1),
					cursor.getDouble(2)), cursor.getLong(4),
					cursor.getString(5), cursor.getString(6));
			place.title = cursor.getString(3);
			place.placeid = cursor.getInt(0);
		}
		return place;
	}

	public List<MyPlacesModel> fixCenters() {
		List<LocationPoint> points = getAssignedPoints();
		List<MyPlacesModel> places = getPlaces();
		double[] centersLat = new double[places.size()];
		double[] centersLon = new double[places.size()];
		int[] countersLat = new int[places.size()];
		int[] countersLon = new int[places.size()];
		for (int i = 0; i < points.size(); i++) {
			centersLat[(int) points.get(i).placeid - 1] += points.get(i).latitude;
			centersLon[(int) points.get(i).placeid - 1] += points.get(i).longitude;
			countersLat[(int) points.get(i).placeid - 1]++;
			countersLon[(int) points.get(i).placeid - 1]++;
		}
		for (int i = 0; i < places.size(); i++) {
			updatePlaceCenter(i + 1, (centersLat[i] / countersLat[i]),
					(centersLon[i] / countersLon[i]));
		}
		return places;
	}

	public void updatePlaceCenter(int placeId, double centerLat,
			double centerLon) {
		SQLiteDatabase db = getWritableDatabase();
		// db.beginTransaction();
		ContentValues cv = new ContentValues();
		cv.put(KEY_PLACE_LAT, centerLat);
		cv.put(KEY_PLACE_LON, centerLon);
		db.update(TABLE_PLACES, cv, KEY_PLACE_ID + "=" + placeId, null);
		// db.setTransactionSuccessful();
		db.close();
	}

	public void updatePlaceName(int placeId, String name) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_PLACE_NAME, name);
		db.update(TABLE_PLACES, cv, KEY_PLACE_ID + "=" + placeId, null);
		db.close();
	}

	public void hidePlace(int placeId) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_PLACE_HIDDEN, 1);
		db.update(TABLE_PLACES, cv, KEY_PLACE_ID + "=" + placeId, null);
		db.close();
	}

	// Step 2 - Add places
	public void repairDatabase() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_PLACES, null, null);
		db.delete(TABLE_VISITS, null, null);
		db.delete("sqlite_sequence", "name='" + TABLE_PLACES + "'", null);
		db.delete("sqlite_sequence", "name='" + TABLE_VISITS + "'", null);
		ContentValues cv = new ContentValues();
		cv.put(KEY_POINT_PLACE_ID, 0);
		db.update(TABLE_POINTS, cv, null, null);
		ContentValues cv2 = new ContentValues();
		cv2.put(KEY_TIMETAMP_VISIT_ID, 0);
		db.update(TABLE_TIMESTAMPS, cv2, null, null);
		db.close();

	}

	public void addPlaces() {
		Log.i(TAG, "adding places");
		List<LocationPoint> points = getPoints();
		ArrayList<LocationPoint> unassingedpoints = new ArrayList<LocationPoint>();
		for (LocationPoint point : points) {
			if (point.placeid == 0)
				unassingedpoints.add(point);
		}
		List<MyPlacesModel> places = getPlaces();
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		for (LocationPoint point : unassingedpoints) {
			LatLng pointCoord = new LatLng(point.latitude, point.longitude);
			boolean assigned = false;
			for (MyPlacesModel place : places) {
				if (SphericalUtil.computeDistanceBetween(pointCoord,
						place.centerCoordinate) < Constants.CLUSTER_RADIUS_METERS) {
					point.placeid = place.placeid;
					ContentValues cv = new ContentValues();
					cv.put(KEY_POINT_PLACE_ID, place.placeid);
					db.update(TABLE_POINTS, cv, KEY_POINT_ID + "="
							+ point.pointid, null);
					assigned = true;
					break;
				}
			}
			if (!assigned) {
				ContentValues cv = new ContentValues();
				cv.put(KEY_PLACE_LAT, point.latitude);
				cv.put(KEY_PLACE_LON, point.longitude);
				cv.put(KEY_PLACE_DURATION, 0);
				cv.put(KEY_PLACE_NAME,
						ctx.getString(R.string.my_places_unnamed_place));
				cv.put(KEY_PLACE_ARR_TIME, "N/A");
				cv.put(KEY_PLACE_DEP_TIME, "N/A");
				cv.put(KEY_PLACE_HIDDEN, 0);
				long placeid = db.insert(TABLE_PLACES, null, cv);
				ContentValues cv2 = new ContentValues();
				cv2.put(KEY_PLACE_ID, placeid);
				db.update(TABLE_POINTS, cv2,
						KEY_POINT_ID + "=" + point.pointid, null);
				MyPlacesModel newModel = new MyPlacesModel(new LatLng(
						point.latitude, point.longitude), 0, "N/A", "N/A");
				newModel.title = ctx
						.getString(R.string.my_places_unnamed_place);
				newModel.placeid = placeid;
				places.add(newModel);
			}
		}
		ArrayList<VisitModel> visits = new ArrayList<VisitModel>();
		ArrayList<TimestampModel> timestamps = new ArrayList<TimestampModel>();
		// Cursor cursor2 = db.rawQuery("SELECT  FROM " + TABLE_TIMESTAMPS +
		// " WHERE " + KEY_TIMETAMP_VISIT_ID + "=0;", null);
		Cursor cursor = db.rawQuery("SELECT " + KEY_TIMESTAMP + ", "
				+ KEY_TIMETAMP_VISIT_ID + ", " + TABLE_TIMESTAMPS + "."
				+ KEY_TIMESTAMP_ID + ", " + KEY_POINT_PLACE_ID + " FROM "
				+ TABLE_POINTS + " INNER JOIN " + TABLE_TIMESTAMPS + " ON "
				+ TABLE_POINTS + "." + KEY_POINT_TIMESTAMP + "="
				+ TABLE_TIMESTAMPS + "." + KEY_TIMESTAMP_ID + " WHERE "
				+ KEY_TIMETAMP_VISIT_ID + "=0", null);
		if (cursor.moveToFirst()) {
			do {
				TimestampModel timestamp = new TimestampModel(
						cursor.getLong(2), cursor.getLong(0),
						cursor.getLong(1), cursor.getLong(3));
				timestamps.add(timestamp);
			} while (cursor.moveToNext());
		}
		Cursor cursor2 = db.rawQuery("SELECT * FROM " + TABLE_VISITS, null);
		if (cursor2.moveToFirst()) {
			do {
				VisitModel visit = new VisitModel(cursor2.getLong(0),
						cursor2.getLong(1), cursor2.getLong(2),
						cursor2.getLong(3));
				visits.add(visit);
			} while (cursor2.moveToNext());
		}
		for (TimestampModel timestamp : timestamps) {
			boolean assigned = false;
			for (VisitModel visit : visits) {
				// Log.i(TAG, "ts place id = " + timestamp.placeId);
				// Log.i(TAG, "vt place id = " + visit.placeId);
				if (visit.placeId == timestamp.placeId) {
					// Log.i(TAG, "vt departureTime = " +
					// visit.departureTimeId);
					// Log.i(TAG, "ts timestamp = " +
					// timestamp.timestampItself);
					if ((timestamp.timestampItself - visit.departureTimeId) < Constants.REVISIT_TIME_PERIOD_SECONDS) {
						visit.departureTimeId = timestamp.timestampItself;
						timestamp.visitId = visit.visitId;
						ContentValues tcv = new ContentValues();
						tcv.put(KEY_TIMETAMP_VISIT_ID, visit.visitId);
						db.update(TABLE_TIMESTAMPS, tcv, KEY_TIMESTAMP_ID + "="
								+ timestamp.timestampId, null);
						tcv = new ContentValues();
						tcv.put(KEY_VISIT_DEP_TIME, timestamp.timestampItself);
						db.update(TABLE_VISITS, tcv, KEY_VISIT_ID + "="
								+ visit.visitId, null);
						assigned = true;
					}
				}
			}
			if (!assigned) {
				ContentValues newVisitcv = new ContentValues();
				newVisitcv.put(KEY_VISIT_ARR_TIME, timestamp.timestampItself);
				newVisitcv.put(KEY_VISIT_DEP_TIME, timestamp.timestampItself);
				newVisitcv.put(KEY_VISIT_PLACE_ID, timestamp.placeId);
				long visitId = db.insert(TABLE_VISITS, null, newVisitcv);
				VisitModel newVisitModel = new VisitModel(visitId,
						timestamp.timestampItself, timestamp.timestampItself,
						timestamp.placeId);
				visits.add(newVisitModel);
				ContentValues visitIdCV = new ContentValues();
				visitIdCV.put(KEY_TIMETAMP_VISIT_ID, visitId);
				db.update(TABLE_TIMESTAMPS, visitIdCV, KEY_TIMESTAMP_ID + "="
						+ timestamp.timestampId, null);
			}
		}
		ArrayList<ArrayList<Long>> arrTimes = new ArrayList<ArrayList<Long>>();
		for (int i = 0; i < places.size(); i++)
			arrTimes.add(new ArrayList<Long>());
		ArrayList<ArrayList<Long>> depTimes = new ArrayList<ArrayList<Long>>();
		for (int i = 0; i < places.size(); i++)
			depTimes.add(new ArrayList<Long>());
		long[] durations = new long[places.size()];
		for (VisitModel v : visits) {
			Log.i(TAG, "visit " + v.arriveTimeId);
			Log.i(TAG, "visit place id = " + v.placeId);
			arrTimes.get((int) v.placeId - 1).add(v.arriveTimeId);
			depTimes.get((int) v.placeId - 1).add(v.departureTimeId);
			durations[(int) v.placeId - 1] += v.departureTimeId
					- v.arriveTimeId;
		}
		for (int i = 0; i < arrTimes.size(); i++) {
			int[] arrTimesArr = new int[arrTimes.get(i).size()];
			int[] depTimesArr = new int[depTimes.get(i).size()];
			for (int j = 0; j < arrTimes.get(i).size(); j++) {
				arrTimesArr[j] = StatisticalUtils.converTimeToInt(new Date(
						arrTimes.get(i).get(j) * 1000));
				depTimesArr[j] = StatisticalUtils.converTimeToInt(new Date(
						depTimes.get(i).get(j) * 1000));
			}
			String arrTimeStr;
			String depTimeStr;
			double arrSDev = StatisticalUtils.getHoursStdDeviation(arrTimesArr);
			if (arrSDev > Constants.DEVIATION_THRESHOLD_MINUTES)
				arrTimeStr = "N/A";
			else
				arrTimeStr = StatisticalUtils
						.convertIntToTime((int) StatisticalUtils
								.getMean(arrTimesArr));
			double depSDev = StatisticalUtils.getHoursStdDeviation(depTimesArr);
			if (depSDev > Constants.DEVIATION_THRESHOLD_MINUTES)
				depTimeStr = "N/A";
			else
				depTimeStr = StatisticalUtils
						.convertIntToTime((int) StatisticalUtils
								.getMean(depTimesArr));
			ContentValues placesArrCv = new ContentValues();
			placesArrCv.put(KEY_PLACE_ARR_TIME, arrTimeStr);
			placesArrCv.put(KEY_PLACE_DEP_TIME, depTimeStr);
			placesArrCv.put(KEY_PLACE_DURATION, durations[i]);
			db.update(TABLE_PLACES, placesArrCv, KEY_PLACE_ID + "=" + (i + 1),
					null);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

}

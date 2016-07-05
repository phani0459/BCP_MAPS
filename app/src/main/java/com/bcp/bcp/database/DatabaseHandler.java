package com.bcp.bcp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bcp.bcp.recyclerview.LocationFenceTrackDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by anjup on 3/22/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geofenceManager";

    private static final String TABLE_GEOFENCE = "geofence";
    private static final String KEY_ID = "id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_RADIUS = "radius";
    private static final String KEY_FENCE_NAME = "fencename";

    private static final String TABLE_FENCETIMING = "fencetiming";
    private static final String KEY_TIMINGID = "id";
    private static final String KEY_FENCEADDRESS = "fenceAddress";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DATETIME = "Datetime";


    private static final String TABLE_LOCATION = "locationtable";
    private static final String KEY_LOCATIONID = "id";
    private static final String KEY_LOCATIONADDRESS = "locationAddress";
    private static final String KEY_LOCDATETIME = "LocDatetime";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_GEOFENCE_TABLE = "CREATE TABLE " + TABLE_GEOFENCE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_LAT + " TEXT, " + KEY_LNG + " TEXT," + KEY_RADIUS + " TEXT," + KEY_FENCE_NAME + " TEXT" + ")";
        db.execSQL(CREATE_GEOFENCE_TABLE);

        String CREATE_FENCETIMING_TABLE = "CREATE TABLE " + TABLE_FENCETIMING + "("
                + KEY_TIMINGID + " INTEGER PRIMARY KEY, " + KEY_FENCEADDRESS + " TEXT," + KEY_STATUS + " TEXT," + KEY_DATETIME + " TEXT UNIQUE" + ")";
        db.execSQL(CREATE_FENCETIMING_TABLE);

        String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_LOCATIONID + " INTEGER PRIMARY KEY, " + KEY_LOCATIONADDRESS + " TEXT," + KEY_LOCDATETIME + " TEXT" + ")";
        db.execSQL(CREATE_TABLE_LOCATION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEOFENCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FENCETIMING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        // Create tables again
        onCreate(db);

    }


    public boolean addLocation(LocationData locationData) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean isInserted = false;

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATIONADDRESS, locationData.getLocAddress());
        values.put(KEY_LOCDATETIME, locationData.getLocDatetime());

        if (values != null) {
            // Inserting Row
            sqLiteDatabase.insert(TABLE_LOCATION, null, values);
            isInserted = true;
        }
        //2nd argument is String containing nullColumnHack
        sqLiteDatabase.close(); // Closing database connection

        return isInserted;
    }

    public List<LocationData> getAllLocationData() {
        List<LocationData> locationDataList = new ArrayList<LocationData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocationData locationData = new LocationData();
                locationData.setId(Integer.parseInt(cursor.getString(0)));
                locationData.setLocAddress(cursor.getString(1));
                locationData.setLocDatetime(cursor.getString(2));

                // Adding contact to list
                locationDataList.add(locationData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return locationDataList;
    }


    public boolean deletePastLocationData() {
        boolean isDeleted = false;
        List<LocationData> locationDataList = new ArrayList<LocationData>();
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.getDefault());
        List<LocationData> locationDataToDisplay = new ArrayList<LocationData>();
        long dbmilli = 0;
        long cyurrDatemilli = 0;
        long DAY = 24 * 60 * 60 * 1000;


        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocationData locationData = new LocationData();
                locationData.setId(Integer.parseInt(cursor.getString(0)));
                locationData.setLocAddress(cursor.getString(1));
                locationData.setLocDatetime(cursor.getString(2));

                // Adding contact to list
                locationDataList.add(locationData);
            } while (cursor.moveToNext());
        }

        if (locationDataList != null && locationDataList.size() > 0) {

            Log.e("Before delete size : ", " : " + locationDataList.size());

            for (LocationData locationData : locationDataList) {
                try {
                    Date dateFromDb = format.parse(locationData.getLocDatetime());
                    dbmilli = dateFromDb.getTime();
                    cyurrDatemilli = new Date().getTime();
                    if (dbmilli > cyurrDatemilli - DAY) {

                        locationDataToDisplay.add(locationData);

                    } else {

                        sqLiteDatabase.delete(TABLE_LOCATION, KEY_LOCATIONID + "=" + locationData.getId(), null);
                        isDeleted = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (locationDataToDisplay != null && locationDataToDisplay.size() > 0) {
            Log.e("After delete size : ", " : " + locationDataToDisplay.size());
        }
        return isDeleted;
    }

    public boolean addFenceTiming(FenceTiming fenceTiming) {
        boolean isInserted = false;
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_FENCEADDRESS, fenceTiming.getFenceAddress());
            values.put(KEY_STATUS, fenceTiming.getStatus());
            values.put(KEY_DATETIME, fenceTiming.getDatetime());

            if (values != null) {
                // Inserting Row
                sqLiteDatabase.insert(TABLE_FENCETIMING, null, values);
                isInserted = true;
            }
            //2nd argument is String containing nullColumnHack
            sqLiteDatabase.close(); // Closing database connection

        } catch (Exception e) {

        }
        return isInserted;
    }

    public List<FenceTiming> getAllFenceTiming() {
        List<FenceTiming> fenceTimingList = new ArrayList<FenceTiming>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FENCETIMING;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FenceTiming fenceTiming = new FenceTiming();
                fenceTiming.setId(Integer.parseInt(cursor.getString(0)));
                fenceTiming.setFenceAddress(cursor.getString(1));
                fenceTiming.setStatus(cursor.getString(2));
                fenceTiming.setDatetime(cursor.getString(3));

                // Adding contact to list
                fenceTimingList.add(fenceTiming);
            } while (cursor.moveToNext());
        }

        // return contact list
        return fenceTimingList;
    }

    public boolean deletePastFenceTiming() {
        boolean isDeleted = false;
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.getDefault());
        List<FenceTiming> fenceTimingList = new ArrayList<FenceTiming>();
        List<FenceTiming> fenceTimingListToDisplay = new ArrayList<FenceTiming>();
        long dbmilli = 0;
        long cyurrDatemilli = 0;
        long DAY = 24 * 60 * 60 * 1000;

        String selectQuery = "SELECT  * FROM " + TABLE_FENCETIMING;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FenceTiming fenceTiming = new FenceTiming();
                fenceTiming.setId(Integer.parseInt(cursor.getString(0)));
                fenceTiming.setFenceAddress(cursor.getString(1));
                fenceTiming.setStatus(cursor.getString(2));
                fenceTiming.setDatetime(cursor.getString(3));

                // Adding contact to list
                fenceTimingList.add(fenceTiming);
            } while (cursor.moveToNext());
        }
        if (fenceTimingList != null && fenceTimingList.size() > 0) {

            Log.e("Before delete size : ", " : " + fenceTimingList.size());

            for (FenceTiming fenceTiming : fenceTimingList) {
                try {
                    Date dateFromDb = format.parse(fenceTiming.getDatetime());
                    dbmilli = dateFromDb.getTime();
                    cyurrDatemilli = new Date().getTime();
                    if (dbmilli > cyurrDatemilli - DAY) {

                        fenceTimingListToDisplay.add(fenceTiming);

                    } else {

                        sqLiteDatabase.delete(TABLE_FENCETIMING, KEY_TIMINGID + "=" + fenceTiming.getId(), null);
                        isDeleted = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (fenceTimingListToDisplay != null && fenceTimingListToDisplay.size() > 0) {
            Log.e("After delete size : ", " : " + fenceTimingListToDisplay.size());
        }
        return isDeleted;
    }

    public FenceTiming getFenceTimingByAddress(String address) {
        try {
            FenceTiming fenceTimingToShow = new FenceTiming();
            List<FenceTiming> fenceTimingList = new ArrayList<FenceTiming>();
            String selectQuery = "SELECT  * FROM " + TABLE_FENCETIMING + " WHERE "
                    + KEY_FENCEADDRESS + " = '" + address + "'";
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    FenceTiming fenceTiming = new FenceTiming();
                    fenceTiming.setId(Integer.parseInt(cursor.getString(0)));
                    fenceTiming.setFenceAddress(cursor.getString(1));
                    fenceTiming.setStatus(cursor.getString(2));
                    fenceTiming.setDatetime(cursor.getString(3));

                    // Adding contact to list
                    fenceTimingList.add(fenceTiming);
                } while (cursor.moveToNext());
            }
            if (fenceTimingList != null && fenceTimingList.size() > 0) {
                Collections.reverse(fenceTimingList);
                fenceTimingToShow = fenceTimingList.get(0);
                return fenceTimingToShow;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateFenceEntryStatus(String time, String status) {
        String selectQuery = "UPDATE " + TABLE_FENCETIMING + " SET " + KEY_STATUS + " = '" + status + "' WHERE "
                + KEY_DATETIME + " = '" + time + "'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(selectQuery);
    }

    public void delete_byID(int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_FENCETIMING, KEY_TIMINGID + "=" + id, null);
    }

    public boolean addFence(GeoFence geoFence) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        boolean isInserted = false;

        ContentValues values = new ContentValues();
        values.put(KEY_LAT, geoFence.getLat());
        values.put(KEY_LNG, geoFence.getLng());
        values.put(KEY_RADIUS, geoFence.getRadius());
        values.put(KEY_FENCE_NAME, geoFence.getFenceName());

        if (values != null) {
            // Inserting Row
            sqLiteDatabase.insert(TABLE_GEOFENCE, null, values);
            isInserted = true;
        }
        //2nd argument is String containing nullColumnHack
        sqLiteDatabase.close(); // Closing database connection

        return isInserted;
    }

    public GeoFence getGeoFence(int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_GEOFENCE, new String[]{KEY_ID, KEY_LAT, KEY_LNG, KEY_RADIUS, KEY_FENCE_NAME}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        GeoFence geoFence = new GeoFence(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return contact

        return geoFence;
    }

    public List<GeoFence> getAllGeoFence() {
        List<GeoFence> geoFenceList = new ArrayList<GeoFence>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GEOFENCE;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeoFence geoFence = new GeoFence();
                geoFence.setId(Integer.parseInt(cursor.getString(0)));
                geoFence.setLat(cursor.getString(1));
                geoFence.setLng(cursor.getString(2));
                geoFence.setRadius(cursor.getString(3));
                geoFence.setFenceName(cursor.getString(4));

                // Adding contact to list
                geoFenceList.add(geoFence);
            } while (cursor.moveToNext());
        }

        // return contact list
        return geoFenceList;
    }

    public int updateGeoFence(GeoFence geoFence) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LAT, geoFence.getLat());
        values.put(KEY_LNG, geoFence.getLng());
        values.put(KEY_RADIUS, geoFence.getRadius());
        values.put(KEY_FENCE_NAME, geoFence.getFenceName());

        // updating row
        return sqLiteDatabase.update(TABLE_GEOFENCE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(geoFence.getId())});
    }


    // Deleting single GeoFence
    public void deleteGeoFence(GeoFence geoFence) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GEOFENCE, KEY_ID + " = ?",
                new String[]{String.valueOf(geoFence.getId())});
        db.close();
    }

    // Getting GeoFence Count
    public int getGeoFenceCount() {
        String countQuery = "SELECT  * FROM " + TABLE_GEOFENCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
}
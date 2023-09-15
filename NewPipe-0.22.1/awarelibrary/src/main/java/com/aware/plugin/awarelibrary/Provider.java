package com.aware.plugin.awarelibrary;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

import java.io.File;
import java.util.HashMap;


//Provider is copied from previous AWARE projects. Github: Heppu and Denzil.
public class Provider extends ContentProvider {
    public static String AUTHORITY = "com.aware.provider.plugin.awarelibrary";
    public static final int DATABASE_VERSION = 1;

    public static final class Example_Data implements BaseColumns {
        private Example_Data(){};

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/plugin_awarelibrary");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.plugin.awarelibrary";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.plugin.awarelibrary";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";

        public static final String SURVEY_ID = "survey_id";
        public static final String QUESTION_ID = "question_id";
        public static final String QUESTION = "question";
        public static final String ANSWER= "answer";
        public static final String TRIGGER = "trigger";
        public static final String APPLICATION = "application";
        public static final String PREV_APPLICATION = "previous"; //TODO previous_application
        public static final String DURATION = "duration";
        public static final String APP_TABLE_TIMESTAMP = "double_app_table_timestamp";

    }

    private static final int AWARE_LIBRARY = 1;
    private static final int AWARE_LIBRARY_ID = 2;


    public static final String DATABASE_NAME =  "/storage/emulated/0/AWARE/aware.db";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/plugin_awarelibrary");
    public static final String[] DATABASE_TABLES = {"plugin_awarelibrary"};

    public static final String[] TABLES_FIELDS = {
            Example_Data._ID + " integer primary key autoincrement," +
                    Example_Data.TIMESTAMP + " real default 0," +
                    Example_Data.DEVICE_ID + " text default ''," +
                    Example_Data.SURVEY_ID + " real default 0," +
                    Example_Data.ANSWER + " text default ''," +
                    "UNIQUE (" + Example_Data.TIMESTAMP + "," + Example_Data.DEVICE_ID + ")"
    };

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> tableMap = null;
    private static DatabaseHelper databaseHelper = null;
    private static SQLiteDatabase database = null;


    /**
     * Initialise the ContentProvider
     */
    private boolean initializeDB() {
        if (databaseHelper == null) {

            databaseHelper = new DatabaseHelper( getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );

        }
        if( databaseHelper != null && ( database == null || ! database.isOpen()) ) {
            database = databaseHelper.getWritableDatabase();
        }
        return( database != null && databaseHelper != null);
    }
    /**
     * Allow resetting the ContentProvider when updating/reinstalling AWARE
     */
    public static void resetDB( Context c ) {
        Log.d("AWARE", "Resetting " + DATABASE_NAME + "...");

        File db = new File(DATABASE_NAME);
        db.delete();
        databaseHelper = new DatabaseHelper( c, DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if( databaseHelper != null ) {
            database = databaseHelper.getWritableDatabase();
        }
    }


    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.plugin.awarelibrary";
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], AWARE_LIBRARY); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", AWARE_LIBRARY_ID); //URI for a single record
        tableMap = new HashMap<String, String>();
        tableMap.put(Example_Data._ID, Example_Data._ID);
        tableMap.put(Example_Data.TIMESTAMP, Example_Data.TIMESTAMP);
        tableMap.put(Example_Data.DEVICE_ID, Example_Data.DEVICE_ID);
        tableMap.put(Example_Data.ANSWER, Example_Data.ANSWER);

        return true;


    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

            if( ! initializeDB() ) {
                Log.w(AUTHORITY,"Database unavailable...");
                return null;
            }

            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            switch (sUriMatcher.match(uri)) {
                case AWARE_LIBRARY:
                    qb.setTables(DATABASE_TABLES[0]);
                    qb.setProjectionMap(tableMap);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI jedan" + uri);
            }
            try {
                Cursor c = qb.query(database, strings, s, strings1, null, null, s1);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            } catch (IllegalStateException e) {
                if (Aware.DEBUG) Log.e(Aware.TAG, e.getMessage());
                return null;
            }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case AWARE_LIBRARY:
                return Example_Data.CONTENT_TYPE;
            case AWARE_LIBRARY_ID:
                return Example_Data.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI dva" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert( Uri uri,  ContentValues contentValues) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        ContentValues values = (contentValues != null) ? new ContentValues(contentValues) : new ContentValues();

        switch (sUriMatcher.match(uri)) {
            case AWARE_LIBRARY:
                long _id = database.insert(DATABASE_TABLES[0], Example_Data.DEVICE_ID, values);
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(Example_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI tri" + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case AWARE_LIBRARY:
                count = database.delete(DATABASE_TABLES[0], selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI cetiri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case AWARE_LIBRARY:
                count = database.update(DATABASE_TABLES[0], contentValues, s, strings);
                break;
            default:
                database.close();
                throw new IllegalArgumentException("Unknown URI pet" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

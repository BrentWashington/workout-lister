package com.washington.workoutkeeper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.washington.workoutkeeper.data.WorkoutContract.WorkoutEntry;

/**
 * Created by Brent on 10/2/2017.
 */

public class WorkoutDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = WorkoutDbHelper.class.getSimpleName();

    /**
     * Name of database file
     */
    private static final String DATABASE_NAME = "workout.db";

    /**
     * Database version. If the database schema is changed, the version must be incremented.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link WorkoutDbHelper}
     *
     * @param context of the app
     */
    public WorkoutDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database has been created for the first time.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates a String for the workout table.
        String SQL_CREATE_WORKOUT_TABLE = " CREATE TABLE " + WorkoutEntry.TABLE_NAME + " ("
                // ID is set to increase with the number of entries in the table.
                + WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                // The workout must have a name and cannot be null.
                + WorkoutEntry.COLUMN_WORKOUT_NAME + " TEXT NOT NULL, "
                // The workout must have a number of reps and cannot be null.
                + WorkoutEntry.COLUMN_WORKOUT_REPS + " INTEGER NOT NULL, "
                // The workout must have a number of sets and cannot be null.
                + WorkoutEntry.COLUMN_WORKOUT_SETS + " INTEGER NOT NULL, "
                // The workout must have a rest time and cannot be null.
                + WorkoutEntry.COLUMN_WORKOUT_REST_TIME + " INTEGER NOT NULL " + ");";

        // Executes the SQL database
        db.execSQL(SQL_CREATE_WORKOUT_TABLE);

        // Logs a message telling if the table was created or not.
        Log.v(LOG_TAG, SQL_CREATE_WORKOUT_TABLE);
    }

    /**
     * This method will be called when the database needs to upgraded.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + WorkoutEntry.TABLE_NAME);
        onCreate(db);
    }
}

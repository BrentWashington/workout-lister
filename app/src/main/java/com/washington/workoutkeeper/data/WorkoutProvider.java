package com.washington.workoutkeeper.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.washington.workoutkeeper.data.WorkoutContract.WorkoutEntry;

/**
 * Created by Brent on 10/4/2017.
 */

/**
 * The ContentProvider
 */
public class WorkoutProvider extends ContentProvider{

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = WorkoutProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the table
     */
    private static final int WORKOUT = 100;

    /**
     * URI matcher code for the content URI for a single workout
     */
    private static final int WORKOUT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        /*
        The calls to addURI() go here, for all of the content URI patterns that the provider
        should recognize. All paths added to the UriMatcher have a corresponding code to return
        when a match is found.
         */

        /*
        The content URI of the form "content://com.example.android.workout/workout" will map to
        the integer code {@link #WORKOUT}. This URI is used to provide access to MULTIPLE rows
        of the workout table.
         */
        sUriMatcher.addURI(WorkoutContract.CONTENT_AUTHORITY, WorkoutContract.PATH_WORKOUT,
                WORKOUT);

        /*
        The content URI of the form "content://com.example.android.workout/workout/#" will map
        to the integer code {@link #WORKOUT_ID}. This URI is used to provide access to ONE single
        row of the table.
         */

        /*
        The # can be substituted for a number. A content URI ending without a number
        doesn't match.
         */
        sUriMatcher.addURI(WorkoutContract.CONTENT_AUTHORITY, WorkoutContract.PATH_WORKOUT +
                "/#", WORKOUT_ID);
    }

    // The database helper object
    private WorkoutDbHelper mDbHelper;

    /**
     * Initializes the provider and database helper object
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new WorkoutDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection
     * arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        // Gets a readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the query results.
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUT:
                /*
                For the WORKOUT code, query the workout table directly with the given projection,
                selection, selection arguments, and sort order. Multiple rows of the table
                can be in the cursor.
                 */
                cursor = database.query(WorkoutEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case WORKOUT_ID:
                /*
                For the WORKOUT_ID code, extract out the ID from the URI.
                Example: "content://com.example.android.workout/workout/3" will have the
                selection "_id=?" and the selection argument ID of 3 (String array).
                 */

                /*
                For every "?" in the selection, we need to have an element in the selection
                arguments that will fill in the "?". Since we have 1 question mark in the
                selection, we have 1 String in the selection arguments' String array.
                 */
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                /*
                 Performs a query on the table which will return a row specified by its
                 ID in the workout table.
                  */
                cursor = database.query(
                        WorkoutEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        /*
        Sets a notification URI on the Cursor so we know what the content URI
        was created for. We will also know to update the Cursor if the data at this URI
        changes.
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Inserts new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUT:
                return insertWorkout(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Inserts a workout into the database with the given content values. Returns the new content URI
     * for that specific row in the database.
     */
    private Uri insertWorkout(Uri uri, ContentValues values) {
        // Gets a writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert a new workout with the given values.
        long id = database.insert(WorkoutEntry.TABLE_NAME, null, values);
        // If the ID is -1, the insertion failed. Log the error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notifies all listeners that the data has changed for the workout content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        /*
         Once we know the ID of the new row in the table,
         return the new URI with the ID appended to the end of it
         */
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUT:
                return updateWorkout(uri, values, selection, selectionArgs);
            case WORKOUT_ID:
                /*
                For the WORKOUT_ID code, extract out the ID from the URI,
                so we know which row to update. Selection will be "_id=?" and selection
                arguments will be a String array containing the actual ID.
                */
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateWorkout(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update workouts in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0, 1, or more workouts).
     * Return the number of rows that were successfully updated.
     */
    private int updateWorkout(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get a writable database to update the data.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected.
        int rowsUpdated = database.update(WorkoutEntry.TABLE_NAME,
                values, selection, selectionArgs);

        /*
        If 1 or more rows were updated, then notify all listeners that the data at the
        given URI has changed.
         */
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement.
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Track the number of rows deleted.
        int rowsDeleted;

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUT:
                // Delete all the rows that match the selection and selection args for ITEM.
                rowsDeleted = database.delete(WorkoutEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WORKOUT_ID:
                selection = WorkoutEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Delete the single row for the given ID in the URI.
                rowsDeleted = database.delete(WorkoutEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        /*
        If 1 or more rows were deleted, then notify all listeners that the data at
        the given URI has changed.
         */
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted.
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORKOUT:
                return WorkoutEntry.CONTENT_LIST_TYPE;
            case WORKOUT_ID:
                return WorkoutEntry.CONTENT_WORKOUT_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

package com.washington.workoutkeeper.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Brent on 10/2/2017.
 */

public class WorkoutContract {

    // Made private so the contract class is not accidentally instantiated.
    private WorkoutContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.workout";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.workout/workout/ is a valid path for
     * looking at workout data. content://com.example.android.workout/timer/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "timer".
     */
    public static final String PATH_WORKOUT = "workout";

    public static class WorkoutEntry implements BaseColumns {

        /**
         * The content URI to access the workout data in the provider.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_WORKOUT);

        public static Uri workoutUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Name of the table
        public static final String TABLE_NAME = "Workout";

        // The _ID column
        public static final String _ID = BaseColumns._ID;

        /**
         * The name of the workout
         * <p>
         * Type: Text
         */
        public static final String COLUMN_WORKOUT_NAME = "name";

        /**
         * The reps in the workout
         *
         * Type: Integer
         */
        public static final String COLUMN_WORKOUT_REPS =  "reps";

        /**
         * The sets in the workout
         *
         * Type: Integer
         */
        public static final String COLUMN_WORKOUT_SETS = "sets";

        /**
         * The rest time in the workout
         *
         * Type: Integer
         */
        public static final String COLUMN_WORKOUT_REST_TIME = "rest_time";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of workouts.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_WORKOUT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for one workout.
         */
        public static final String CONTENT_WORKOUT_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_WORKOUT;
    }
}

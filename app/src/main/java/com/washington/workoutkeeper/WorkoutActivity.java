package com.washington.workoutkeeper;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.washington.workoutkeeper.data.WorkoutContract.WorkoutEntry;

public class WorkoutActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int WORKOUT_LOADER = 0;

    // Cursor Adapter for the WorkoutActivity
    private WCursorAdapter wCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // Sets a click listener on the floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            /*
             Intent to take the user to the EditorActivity when the floating action
             button is clicked.
              */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Finds the ListView that contains the workout data
        ListView workoutListView = (ListView) findViewById(R.id.list_view);

        // Sets the empty view on the ListView, but only if the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        workoutListView.setEmptyView(emptyView);

        /*
        Sets up an adapter for the ListView of items. Since there are no entries yet,
        remain as null to the Cursor until the Loader finishes.
         */
        wCursorAdapter = new WCursorAdapter(this, null);
        workoutListView.setAdapter(wCursorAdapter);

        /*
        Sets a click listener on the workout view. When a workout is clicked on, it takes the user
        to the "Edit Workout" screen.
         */
        workoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WorkoutActivity.this, EditorActivity.class);
               /*
               The content Uri which handles attaching a workout to its ID.
               Example: "content://com.example.android.workout/workout/2" would be the URI
               for the workout with an ID of 2 being clicked on.
                */
                Uri currentWorkoutUri = ContentUris.withAppendedId(WorkoutEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent.
                intent.setData(currentWorkoutUri);

                // Launch the {@link EditorActivity} to display the data for the current workout.
                startActivity(intent);
            }
        });

        // Starts the loader
        getSupportLoaderManager().initLoader(WORKOUT_LOADER, null, this);
    }

    /**
     * Helper method to delete all workouts in the database
     */
    private void deleteAllWorkouts() {
        int rowsDeleted = getContentResolver().delete(WorkoutEntry.CONTENT_URI, null, null);
        // Log message for the number of rows deleted
        Log.v("WorkoutActivity", rowsDeleted + " rows deleted from database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        Inflate the menu options from the res/menu/inventory_menu.xml file.
        This adds menu items to the app bar.
        */
        getMenuInflater().inflate(R.menu.workout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Responds to the "Delete All Workouts" menu option being clicked on.
            case R.id.action_delete_all_entries:
                deleteAllWorkouts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a column which specifies the column's row that we care about
        String[] projection = {
                WorkoutEntry._ID,
                WorkoutEntry.COLUMN_WORKOUT_NAME,
                WorkoutEntry.COLUMN_WORKOUT_REPS,
                WorkoutEntry.COLUMN_WORKOUT_SETS,
                WorkoutEntry.COLUMN_WORKOUT_REST_TIME };

        // This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,             // Parent activity context
                WorkoutEntry.CONTENT_URI,         // Provider content with URI to query
                projection,                       // Columns to include in the resulting Cursor
                null,                             // No selection clause
                null,                             // No selection arguments
                null);                            // Default sort order
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        // Swaps in the new cursor with the updated workout data.
        wCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        // Called when the last cursor from onLoadFinished() needs to be deleted.
        wCursorAdapter.swapCursor(null);
    }
}

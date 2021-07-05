package com.washington.workoutkeeper;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.washington.workoutkeeper.data.WorkoutContract.WorkoutEntry;

/**
 * Created by Brent on 10/2/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the data workout loader
    private static final int EXISTING_WORKOUT_LOADER = 0;

    // The content Uri for the current workout
    private Uri mCurrentWorkoutUri;

    // The save button
    private Button mSaveButton;

    // The delete button
    private Button mDeleteButton;

    // The name input
    private EditText mNameInput;

    // The reps input
    private EditText mRepsInput;

    // The sets input
    private EditText mSetsInput;

    // The rest time input
    private EditText mRestTimeInput;

    /**
     * Boolean that tells if the workout was edited/changed or not
     * <p>
     * True(yes)
     * False(no)
     */
    private boolean mWorkoutHasChanged = false;

    /**
     * OnTouchListener makes note if any of the views have been touched, which will turn
     * the mWorkoutHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mWorkoutHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Name input in the editor
        mNameInput = (EditText) findViewById(R.id.input_name);
        mNameInput.setOnTouchListener(touchListener);

        // Reps input in the editor
        mRepsInput = (EditText) findViewById(R.id.input_rep_number);
        mRepsInput.setOnTouchListener(touchListener);

        // Sets input in the editor
        mSetsInput = (EditText) findViewById(R.id.input_sets);
        mSetsInput.setOnTouchListener(touchListener);

        mRestTimeInput = (EditText) findViewById(R.id.input_rest_time);
        mRestTimeInput.setOnTouchListener(touchListener);

        // The save button for the current workout in the editor activity
        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        // The delete button for the current workout in the editor activity
        mDeleteButton = (Button) findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        /*
        Examine the intent that was used to launch this activity in order to figure out
        if we're creating a new workout or editing an existing one.
         */
        Intent intent = getIntent();
        mCurrentWorkoutUri = intent.getData();

        // Statement if the user is adding a new workout
        if (mCurrentWorkoutUri == null) {
            // If the workout is not in the database, set the title to "Add Workout"
            setTitle(getString(R.string.editor_activity_title_add_workout));
        } else {
            // If the workout already exists, set the title to "Edit Workout"
            setTitle(getString(R.string.editor_activity_title_edit_workout));
            /*
            Initializes a loader to read the workout data from the database and display
            the current values in the editor.
             */
            getSupportLoaderManager().initLoader(EXISTING_WORKOUT_LOADER, null, this);
        }
    }

    /*
     * Gets user input and inserts a new workout into the database.
     */
    private void saveWorkout() {
        // Read from the input fields and use trim to eliminate trailing white space.
        String nameString = mNameInput.getText().toString().trim();
        String repsString = mRepsInput.getText().toString().trim();
        String setsString = mSetsInput.getText().toString().trim();
        String restString = mRestTimeInput.getText().toString().trim();

        //Checks if this is a new item and if every editor field is blank.
        if (mCurrentWorkoutUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(repsString) &&
                TextUtils.isEmpty(setsString) &&
                TextUtils.isEmpty(restString)) {
            /*
            Since no fields were modified, we can return early without creating a new workout.
            No need to create ContentValues and no need to do any ContentProvider operations.
             */
            return;
        }

        /*
        Create a ContentValues object where column names are the keys,
        and workout details from the editor are the values.
        */
        ContentValues values = new ContentValues();
        values.put(WorkoutEntry.COLUMN_WORKOUT_NAME, nameString);
        values.put(WorkoutEntry.COLUMN_WORKOUT_REPS, repsString);
        values.put(WorkoutEntry.COLUMN_WORKOUT_SETS, setsString);
        values.put(WorkoutEntry.COLUMN_WORKOUT_REST_TIME, restString);

        // Determines if this is a new or existing workout and if mCurrentWorkoutUri is null.
        if (mCurrentWorkoutUri == null) {
            /*
            Sanity Check: check that the workout's name is not null. If it is, show a Toast
            telling the user that they need to enter a name.
             */
            if (nameString.isEmpty() || nameString.equals("")) {
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
                return;
            }

            /*
            Sanity Check: check that the workout's reps are not null. If it is, show a Toast
            telling the user that they need to enter the number of reps.
             */
            if (repsString.isEmpty() || repsString.equals("")) {
                Toast.makeText(this, "Please enter number of reps", Toast.LENGTH_SHORT).show();
                return;
            }

            /*
            Sanity Check: check that the workout's sets are not null. If it is, show a Toast
            telling the user that they need to enter the number of sets.
             */
            if (setsString.isEmpty() || setsString.equals("")) {
                Toast.makeText(this, "Please enter number of sets", Toast.LENGTH_SHORT).show();
                return;
            }

            /*
            Sanity Check: check that the workout's rest time is not null. If it is, show a Toast
            telling the user that they need to enter a rest time.
             */
            if (restString.isEmpty() || restString.equals("")) {
                Toast.makeText(this, "Please enter a rest time", Toast.LENGTH_SHORT).show();
                return;
            }

           /*
           If this is a new workout, insert a new workout into the provider, returning the
           content URI for the new workout.
            */
            Uri newUri = getContentResolver().insert(WorkoutEntry.CONTENT_URI, values);

            // Show a toast based on the results of the insertion.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_workout_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // If not, then the insertion was successful
                Toast.makeText(this, getString(R.string.editor_insert_workout_successful),
                        Toast.LENGTH_SHORT).show();
                // Launch the WorkoutActivity class after a successful insertion
                Intent successfulIntent = new Intent(this, WorkoutActivity.class);
                startActivity(successfulIntent);
            }
        } else {
             /*
             Otherwise this is an existing workout, so update the item with content URI
           and pass in the new ContentValues. Pass in null for the selection and selection args
           because mCurrentWorkoutUri will already identify the row in the database that is
           being modified.
            */
            int rowsAffected = getContentResolver().update(mCurrentWorkoutUri, values, null, null);

            if (nameString.isEmpty() || nameString.equals("")) {
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
                return;
            }

            /*
            Sanity Check: check that the workout's reps are not null. If it is, show a Toast
            telling the user that they need to enter the number of reps.
             */
            if (repsString.isEmpty() || repsString.equals("")) {
                Toast.makeText(this, "Please enter number of reps", Toast.LENGTH_SHORT).show();
                return;
            }

            /*
            Sanity Check: check that the workout's sets are not null. If it is, show a Toast
            telling the user that they need to enter the number of sets.
             */
            if (setsString.isEmpty() || setsString.equals("")) {
                Toast.makeText(this, "Please enter number of sets", Toast.LENGTH_SHORT).show();
                return;
            }

            /*
            Sanity Check: check that the workout's rest time is not null. If it is, show a Toast
            telling the user that they need to enter a rest time.
             */
            if (restString.isEmpty() || restString.equals("")) {
                Toast.makeText(this, "Please enter a rest time", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_workout_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // If rows WERE affected, then the update was successful
                Toast.makeText(this, getString(R.string.editor_update_workout_successful),
                        Toast.LENGTH_SHORT).show();
                // Launch the WorkoutActivity class after a successful update
                Intent successfulIntent = new Intent(this, WorkoutActivity.class);
                startActivity(successfulIntent);
            }
        }
    }

    /**
     * Performs the deletion of the workout in the database.
     */
    private void deleteWorkout() {
        // Only perform the delete if this is an existing workout.
        if (mCurrentWorkoutUri != null) {
            /*
            Call the ContentResolver to delete the workout at the given content URI.
            Pass in null for the selection and selection args because the mCurrentWorkoutUri
            content URI already identifies the workout that is wanted.
             */
            int rowsDeleted = getContentResolver().delete(mCurrentWorkoutUri, null, null);

            // Show a toast message depending on the results of the deletion.
            if (rowsDeleted == 0) {
                // If now rows were deleted, then there was an error with the deletion.
                Toast.makeText(this, getString(R.string.editor_delete_workout_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the deletion was successful.
                Toast.makeText(this, getString(R.string.editor_delete_workout_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {
            case android.R.id.home:
                /*
                 If the user has not changed anything, then navigate back to the
                 parent activity (WorkoutActivity).
                  */
                if (!mWorkoutHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                /*
                If the user has made some changes but decides to discard it, show
                an unsaved changes dialog.
                 */
                DialogInterface.OnClickListener discardClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // The unsaved changes dialog
                showUnsavedChangesDialog(discardClickListener);
                return true;
        }

        return super.onOptionsItemSelected(mi);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the workout hasn't been changed then proceed with the back button press.
        if (!mWorkoutHasChanged) {
            super.onBackPressed();
            return;
        }
        /*
        Otherwise if there are unsaved changes, setup a dialog to warn the user. Create
        a click listener to handle the user confirming that changes should be discarded.
         */
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost if
     * they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when the user
     *                                   confirms they want to discard their changes.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        /*
        Create an AlertDialog.Builder and set the message and click listeners for
        the positive and negative buttons on the dialog.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                /*
                User clicked on "Keep editing" button, so dismiss the dialog and
                continue editing the workout.
                 */
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        /*
        Create an AlertDialog.Builder and set the message and click listeners
        for the positive and negative buttons on the dialog.
        */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // The user clicked the "Delete" button, so delete the workout.
                deleteWorkout();
            }
        });
        // The user clicked "cancel," so dismiss the dialog.
        builder.setNegativeButton(R.string.cancel, null);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        // The query projection
        String[] projection = {
             /*
             Since the editor shows all workout details, define a projection that contains
             all the columns in the workout table.
              */
                WorkoutEntry._ID,
                WorkoutEntry.COLUMN_WORKOUT_NAME,
                WorkoutEntry.COLUMN_WORKOUT_REPS,
                WorkoutEntry.COLUMN_WORKOUT_SETS,
                WorkoutEntry.COLUMN_WORKOUT_REST_TIME
        };

        // This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,
                mCurrentWorkoutUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
            /*
            Continue moving to the first row of the cursor and reading data from it.
            (This should be the only row in the cursor)
             */
        if (cursor.moveToFirst()) {
            // The order in which the contents in the data rows are arranged
            int nameColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_NAME);
            int repsColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_REPS);
            int setsColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_SETS);
            int restColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_REST_TIME);

            // Extract out the value from the Cursor for the given column
            String workoutName = cursor.getString(nameColumnIndex);
            int workoutReps = cursor.getInt(repsColumnIndex);
            int workoutSets = cursor.getInt(setsColumnIndex);
            int workoutRestTime = cursor.getInt(restColumnIndex);

            mNameInput.setText(workoutName);
            mRepsInput.setText(String.valueOf(workoutReps));
            mSetsInput.setText(String.valueOf(workoutSets));
            mRestTimeInput.setText(String.valueOf(workoutRestTime));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameInput.setText("");
        mRepsInput.setText("");
        mSetsInput.setText("");
        mRestTimeInput.setText("");
    }
}

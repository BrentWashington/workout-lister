package com.washington.workoutkeeper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.washington.workoutkeeper.data.WorkoutContract.WorkoutEntry;

/**
 * Created by Brent on 10/2/2017.
 */

public class WCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link WCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public WCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // The ViewHolder class
    static class ViewHolder {
        TextView name;
        TextView reps;
        TextView sets;
        TextView rest;
    }

    /**
     * ViewHolder to stop the frequent calling of findViewById() when scrolling. Repeated use
     * of findViewById() slows down app performance.
     */
    public View getView(Context context, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.workout_name);
            holder.reps = (TextView) convertView.findViewById(R.id.workout_reps);
            holder.sets = (TextView) convertView.findViewById(R.id.workout_sets);
            holder.rest = (TextView) convertView.findViewById(R.id.workout_rest_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /*
     * This method binds the workout data (in the current row pointed to by cursor) to the given
     * list item layout. Example: The workout name binds to the name TextView.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        TextView workout_name = (TextView) view.findViewById(R.id.workout_name);
        TextView workout_reps = (TextView) view.findViewById(R.id.workout_reps);
        TextView workout_sets = (TextView) view.findViewById(R.id.workout_sets);
        TextView workout_rest = (TextView) view.findViewById(R.id.workout_rest_time);

        // Finds the columns of the workout details
        int nameColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_NAME);
        int repsColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_REPS);
        int setsColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_SETS);
        int restColumnIndex = cursor.getColumnIndex(WorkoutEntry.COLUMN_WORKOUT_REST_TIME);

        // Read the workout details from the cursor
        String workoutName = cursor.getString(nameColumnIndex);
        int workoutReps = cursor.getInt(repsColumnIndex);
        int workoutSets = cursor.getInt(setsColumnIndex);
        int workoutRest = cursor.getInt(restColumnIndex);

        // Display the name of the workout.
        workout_name.setText(workoutName);
        /*
         Display the reps in the workout with the value of a String so the program knows
         to display an integer.
          */
        workout_reps.setText(String.valueOf(workoutReps));
        // Display the sets of the workout with the value of a String.
        workout_sets.setText(String.valueOf(workoutSets));
        // Display the rest time in the workout with the value of a String.
        workout_rest.setText(String.valueOf(workoutRest));
    }
}
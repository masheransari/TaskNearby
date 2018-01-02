package app.tasknearby.yashcreations.com.tasknearby;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalTime;

import app.tasknearby.yashcreations.com.tasknearby.models.Location;
import app.tasknearby.yashcreations.com.tasknearby.models.Task;
import app.tasknearby.yashcreations.com.tasknearby.utils.AppUtils;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Constants.
     */
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String EXTRA_TASK_ID = "taskIdForDetail";

    /**
     * Views.
     */
    private Button doneButton;
    private ActionBar mActionBar;

    private Task mTask;
    private TaskRepository mTaskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setActionBar();
        // Passing application context to avoid memory leaks. 
        mTaskRepository = new TaskRepository(getApplicationContext());
        // Get the taskId passed to this activity whose details are to be shown.
        long taskId = this.getIntent().getLongExtra(EXTRA_TASK_ID, -1);
        if (taskId == -1) {
            Log.e(TAG, "No taskId has been passed to DetailActivity.");
            // TODO: uncomment return statment when main activity has been made.
//            return;
        }
        mTask = mTaskRepository.getTaskWithId(taskId);
        setData(mTask);
    }

    private void setActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }
    }

    private void setData(Task task) {
        // Set task title on actionBar
        mActionBar.setTitle(task.getTaskName());
        showLocationDetails(task);
        showCoverImage(task);
        showTimeDetails(task);
        showDateInterval(task);
        showAlarmStatus(task);
        showRepeatType(task);
        showNote(task);
        // Also set the mark as done button.
        doneButton = findViewById(R.id.btn_action_done);
        setDoneButton(task);
        // Set the directions FAB.
        findViewById(R.id.fab_directions).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_action_done:
                invertDoneStatus();
                break;
            case R.id.fab_directions:
                showDirections();
                break;
        }
    }

    /**
     * Sets the data to reminder range and location name textViews.
     */
    private void showLocationDetails(Task task) {
        // Reminder range.
        TextView reminderRangeTv = findViewById(R.id.text_reminder_range);
        reminderRangeTv.setText(String.format(getString(R.string.detail_format_reminder_range),
                task.getReminderRange()));
        // Set locationName.
        Location location = mTaskRepository.getLocationById(task.getLocationId());
        TextView locationNameTv = findViewById(R.id.text_location_name);
        locationNameTv.setText(location.getPlaceName());
    }

    /**
     * Shows the task's cover image on detail layout.
     */
    private void showCoverImage(Task task) {
        if (task.getImageUri() != null) {
            ((ImageView) findViewById(R.id.image_task_cover))
                    .setImageURI(Uri.parse(task.getImageUri()));
        }
    }

    /**
     * Sets the user visible time string to the textView.
     */
    private void showTimeDetails(Task task) {
        // Time range set.
        String timeDisplayString;
        LocalTime startTime = task.getStartTime();
        LocalTime endTime = task.getEndTime();
        if (startTime.getHourOfDay() == 0 && startTime.getMinuteOfHour() == 0
                && endTime.getHourOfDay() == 23 && endTime.getMinuteOfHour() == 59) {
            timeDisplayString = getString(R.string.detail_time_anytime);
        } else {
            timeDisplayString = String.format(getString(R.string.detail_time_format),
                    AppUtils.getReadableTime(startTime),
                    AppUtils.getReadableTime(endTime));
        }
        TextView timeTv = findViewById(R.id.text_time_range);
        timeTv.setText(timeDisplayString);
    }

    /**
     * Sets the user visible date interval to the textView.
     *
     * @param task The task for which details are being set.
     */
    private void showDateInterval(Task task) {
        // Date range set.
        String dateIntervalString = String.format(getString(R.string.detail_date_format),
                AppUtils.getReadableDate(this, task.getStartDate()),
                AppUtils.getReadableDate(this, task.getEndDate()));
        TextView dateIntervalTv = findViewById(R.id.text_date_interval);
        dateIntervalTv.setText(dateIntervalString);
    }

    /**
     * Sets the alarm status to UI.
     *
     * @param task Task for which details are being set.
     */
    private void showAlarmStatus(Task task) {
        String alarmStatus = getString(R.string.detail_alarm_on);
        if (task.getIsAlarmSet() == 0) {
            alarmStatus = getString(R.string.detail_alarm_off);
        }
        TextView alarmStatusTv = findViewById(R.id.text_alarm);
        alarmStatusTv.setText(String.format(getString(R.string.detail_alarm_format), alarmStatus));
    }

    /**
     * Sets the repeat type of the reminder to the UI.
     */
    private void showRepeatType(Task task) {
        String[] repeatArray = getResources().getStringArray(R.array.creator_repeat_options);
        TextView repeatStatusTv = findViewById(R.id.text_repeat);
        repeatStatusTv.setText(repeatArray[task.getRepeatType()]);
    }

    /**
     * Sets the note to UI.
     */
    private void showNote(Task task) {
        TextView noteView = findViewById(R.id.text_note);
        if (task.getNote() == null || TextUtils.isEmpty(task.getNote())) {
            findViewById(R.id.icon_note).setVisibility(View.GONE);
            noteView.setVisibility(View.GONE);
        }
        noteView.setText(task.getNote());
    }

    /**
     * Sets the text shown on done button.
     */
    private void setDoneButton(Task task) {
        if (task.getIsDone() == 0) {
            Log.i(TAG, "Task has been set as not done.");
            doneButton.setText(R.string.detail_button_mark_done);
        } else {
            Log.i(TAG, "Task has been set as done.");
            doneButton.setText(R.string.detail_button_reset);
        }
        doneButton.setOnClickListener(this);
    }

    /**
     * Updates the task when "mark done" or "reset" is pressed.
     */
    private void invertDoneStatus() {
        mTask.setIsDone(1 - mTask.getIsDone());
        mTaskRepository.updateTask(mTask);
        setDoneButton(mTask);
    }

    /**
     * Shows the directions to the location on Google maps.
     */
    private void showDirections() {
        Location location = mTaskRepository.getLocationById(mTask.getLocationId());
        Uri uri = Uri.parse("google.navigation:q=" + location.getLatitude() + ","
                + location.getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteTask();
                break;
            case R.id.action_edit:
                editTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Deletes the task after user's confirmation.
     */
    private void deleteTask() {
        // Show an alert dialog for confirmation.
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.detail_delete_dialog_message)
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setPositiveButton("Delete", (dialog, which) -> {
                    mTaskRepository.removeTask(mTask);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .create();
        alertDialog.show();
    }

    /**
     * Opens the TaskCreatorActivity for editing the task. Note that the taskId will be passed as
     * an extra args and {@link TaskCreatorActivity} will need to fill in the details initially.
     * Edit is a simplification of delete and add new. This will be the responsibility of
     * TaskCreatorActivity.
     */
    private void editTask() {
        Intent editIntent = TaskCreatorActivity.getEditModeIntent(this, mTask.getId());
        startActivity(editIntent);
        // Ideally we should not finish here and call the activity for result.
        // If successful, update the views. This has been left as a final touch.
        finish();
    }
}

// TODO: Also show the state of reminder, i.e. Snoozed etc.
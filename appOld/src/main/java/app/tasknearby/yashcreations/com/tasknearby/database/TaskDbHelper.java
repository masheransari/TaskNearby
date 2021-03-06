package app.tasknearby.yashcreations.com.tasknearby.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * DEPENDENCY - TasksContract
 *
 * Creates the database using contract (Schema) that is used.
 */

/**
 * Created by Yash on 22/04/15.
 */
public class TaskDbHelper extends SQLiteOpenHelper {
    static public TaskDbHelper mInstace = null;

    static public final int DATABASE_VERSION = 2;
    static public String DATABASE_NAME = "task_database.db";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static TaskDbHelper getInstance(Context context) {
        if (mInstace == null)
            mInstace = new TaskDbHelper(context.getApplicationContext());
        return mInstace;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * Statement for creating location table.
         */
        final String CREATE_LOCATION_TABLE = " CREATE TABLE " + TasksContract.LocationEntry
                .TABLE_NAME + "(" +
                TasksContract.LocationEntry._ID + " INTEGER PRIMARY KEY, " +
                TasksContract.LocationEntry.COLUMN_PLACE_NAME + " TEXT UNIQUE NOT NULL, " +
                TasksContract.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                TasksContract.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                TasksContract.LocationEntry.COLUMN_COUNT + " INTEGER DEFAULT 1 ," +
                TasksContract.LocationEntry.COLUMN_HIDDEN + " INTEGER DEFAULT 0 " + ");";

        sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);

        final String CREATE_TASKS_TABLE = " CREATE TABLE " + TasksContract.TaskEntry.TABLE_NAME +
                "(" +
                TasksContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TasksContract.TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_LOCATION_NAME + " TEXT NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_LOCATION_ALARM + " TEXT NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_LOCATION_COLOR + " TEXT NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_MIN_DISTANCE + " INTEGER NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_DONE_STATUS + " TEXT NOT NULL DEFAULT 'false', " +
                TasksContract.TaskEntry.COLUMN_REMIND_DISTANCE + " INTEGER NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_SNOOZE_TIME + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + TasksContract.TaskEntry.COLUMN_LOCATION_NAME + ") REFERENCES " +
                TasksContract.LocationEntry.TABLE_NAME + "(" + TasksContract.LocationEntry
                .COLUMN_PLACE_NAME + ")" +
                ");";

        sqLiteDatabase.execSQL(CREATE_TASKS_TABLE);

    }

    /**
     * Called when database version is changed. So, that we can provide appropriate migrations
     * from old schema to new schema.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        switch (oldVersion) {
            case 1:
                // earlier in DB version 1 there was no "count" column (for sorting saved locations)
                // so, when we moved to version 2, we need to change.
                Log.e("DbHelper", "Upgrading....");
                String addCountHidden = "ALTER TABLE "
                        + TasksContract.LocationEntry.TABLE_NAME
                        + " ADD COLUMN " + TasksContract.LocationEntry.COLUMN_COUNT + " INTEGER " +
                        "DEFAULT 1"
                        + ";";
                sqLiteDatabase.execSQL(addCountHidden);

                addCountHidden = "ALTER TABLE "
                        + TasksContract.LocationEntry.TABLE_NAME
                        + " ADD COLUMN " + TasksContract.LocationEntry.COLUMN_HIDDEN + " INTEGER " +
                        "DEFAULT 0"
                        + ";";
                sqLiteDatabase.execSQL(addCountHidden);

                break;
            default:
                Log.e("DbHelperOnUpgrade", "No version matched");
        }
    }
}

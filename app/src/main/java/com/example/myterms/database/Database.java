package com.example.myterms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myterms.application.App;

import java.util.Map;

public class Database extends SQLiteOpenHelper {
    private static final String TAG = "app: Database";
    private static final String SQL = "SQL";
    private static boolean built = false;
    public final SQLiteDatabase CORE;

    public Database(@Nullable Context context) {
        super(context, "TermTrackerDB", null, 1);
        CORE = this.getWritableDatabase();
        if (App.BUILD_DB && !built) {
            createTables(CORE);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        //////////////////////////////////////
        ///       CREATE TERM TABLE        ///
        //////////////////////////////////////
        String tableName = "Term";
        String sql = "DROP TABLE IF EXISTS '" + tableName + "';'";
        Log.i(SQL, "Drop " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE '" + tableName + "' (\n" +
                "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\ttitle TEXT UNIQUE,\n" +
                "\tstart_date TEXT,\n" +
                "\tend_date TEXT\n" +
                ");";
        Log.i(SQL, "Creating " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        //////////////////////////////////////
        ///      CREATE COURSE TABLE       ///
        //////////////////////////////////////
        tableName = "Course";
        sql = "DROP TABLE IF EXISTS '" + tableName + "';'";
        Log.i(SQL, "Drop " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE '" + tableName + "' (\n" +
                "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tterm_id INTEGER,\n" +
                "\ttitle TEXT,\n" +
                "\tcredits INTEGER,\n" +
                "\tstart_date TEXT,\n" +
                "\tend_date TEXT,\n" +
                "\tstart_alarm TEXT,\n" +
                "\tend_alarm TEXT,\n" +
                "\tstatus INTEGER\n" +
                ");";
        Log.i(SQL, "Creating " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        //////////////////////////////////////
        ///      CREATE MENTOR TABLE       ///
        //////////////////////////////////////
        tableName = "Mentor";
        sql = "DROP TABLE IF EXISTS '" + tableName + "';'";
        Log.i(SQL, "Drop " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE '" + tableName + "' (\n" +
                "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tname TEXT UNIQUE,\n" +
                "\tphone TEXT,\n" +
                "\temail TEXT\n" +
                ");";
        Log.i(SQL, "Creating " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        //////////////////////////////////////
        ///   CREATE COURSE MENTOR TABLE   ///
        //////////////////////////////////////
        tableName = "CourseMentor";
        sql = "DROP TABLE IF EXISTS '" + tableName + "';'";
        Log.i(SQL, "Drop " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE '" + tableName + "' (\n" +
                "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tcourse_id INTEGER,\n" +
                "\tmentor_id INTEGER\n" +
                ");";
        Log.i(SQL, "Creating " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        //////////////////////////////////////
        ///    CREATE ASSESSMENT TABLE     ///
        //////////////////////////////////////
        tableName = "Assessment";
        sql = "DROP TABLE IF EXISTS '" + tableName + "';'";
        Log.i(SQL, "Drop " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE '" + tableName + "' (\n" +
                "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tcourse_id INTEGER,\n" +
                "\ttype INTEGER,\n" +
                "\tname TEXT,\n" +
                "\tdescription BLOB,\n" +
                "\tcompletion_date TEXT,\n" +
                "\talarm TEXT,\n" +
                "\tcomplete INTEGER\n" +
                ");";
        Log.i(SQL, "Creating " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        //////////////////////////////////////
        ///       CREATE NOTE TABLE        ///
        //////////////////////////////////////
        tableName = "Note";
        sql = "DROP TABLE IF EXISTS '" + tableName + "';'";
        Log.i(SQL, "Drop " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        sql = "CREATE TABLE '" + tableName + "' (\n" +
                "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tcourse_id INTEGER,\n" +
                "\tmessage TEXT\n" +
                ");";
        Log.i(SQL, "Creating " + tableName + " table: \n" + sql);
        db.execSQL(sql);

        built = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createTables(db);
    }

    public long insert(String tableName, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();

        long id = db.insertOrThrow(tableName, null, values);

        String log = "Added record to " + tableName + " table:";

        log += "\nid: " + id;

        for (Map.Entry<String, Object> entry : values.valueSet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            log += "\n" + key + ": " + value.toString();
        }
        Log.i(SQL, log);

        return id;
    }

    public Cursor getData(String tableName, String conditions) {
        String query = String.format("SELECT * FROM %s%s;", tableName,
                (conditions.isEmpty()) ? "" : "\n" + conditions);
        return getData(query);
    }
    public Cursor getData(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery(query, null);
        Log.i(SQL, "Get select data:\n" + query);
        return data;
    }

    public void update(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i(SQL, "Update row:\n" + sql);
        db.execSQL(sql);
    }

    public void delete(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i(SQL, "Delete row:\n" + sql);
        db.execSQL(sql);
    }

    public boolean tablesAlreadyBuilt(SQLiteDatabase db) {
        try {
            if (db == null) db = this.getWritableDatabase();

            String sql = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'";
            Cursor data = db.rawQuery(sql, null);
            data.moveToFirst();
            int count = data.getInt(0);
            if (count == 0) return false;

            sql = "SELECT count(*) FROM Term";
            data = db.rawQuery(sql, null);
            data.moveToFirst();
            count = data.getInt(0);
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, "tablesAlreadyBuilt: error getting database", e);
            return false;
        }
    }

    public boolean tablesAlreadyPopulated(SQLiteDatabase db) {
        try {
            if (db == null) db = this.getWritableDatabase();

            if (!tablesAlreadyBuilt(db)) return false;

            return tableSize("Term") > 0;
        } catch (Exception e) {
            Log.e(TAG, "tablesAlreadyPopulated: error getting table", e);
            return false;
        }
    }

    public int tableSize(String tableName) {
        return tableSize(tableName, "");
    }
    public int tableSize(String tableName, String conditions) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String sql = "SELECT count(*) FROM " + tableName +
                    (conditions.isEmpty() ? "" : "\n" + conditions);
            Cursor data = db.rawQuery(sql, null);
            data.moveToFirst();
            int count = data.getInt(0);
            return count;
        } catch (Exception e) {
            Log.e(TAG, "tablesAlreadyPopulated: error getting table", e);
            return -1;
        }
    }
}

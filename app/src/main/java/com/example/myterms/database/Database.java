package com.example.myterms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myterms.application.App;
import com.example.myterms.application.Date;
import com.example.myterms.application.MyFunctions;
import com.example.myterms.assessment.Assessment;
import com.example.myterms.course.Course;
import com.example.myterms.course.Course.Status;
import com.example.myterms.mentor.Mentor;
import com.example.myterms.term.Term;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.myterms.application.MyFunctions.alignCenter;
import static com.example.myterms.application.MyFunctions.alignLeft;
import static com.example.myterms.application.MyFunctions.alignRight;
import static com.example.myterms.application.MyFunctions.repeat;

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
                "\tstart_date INTEGER,\n" +
                "\tend_date INTEGER\n" +
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
                "\tstart_date INTEGER,\n" +
                "\tend_date INTEGER,\n" +
                "\tstart_alarm INTEGER,\n" +
                "\tend_alarm INTEGER,\n" +
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
                "\tcompletion_date INTEGER,\n" +
                "\talarm INTEGER,\n" +
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
    
    private String getTable(String tableName, LinkedHashMap<String, String> colInfo) {
        // get database rows
        Cursor data = getData(tableName, "");
        
        // set the column count
        final int colCount = colInfo.size();
        
        // create the column information arrays
        String[] headers = new String[colCount];
        String[] separators = new String[colCount];
        Integer[] widths = new Integer[colCount];
        String[] classes = new String[colCount];
        
        int i = 0;
        for (Map.Entry<String, String> entry : colInfo.entrySet()) {
            String header = entry.getKey();
            String colClass = entry.getValue();
            
            headers[i] = header;
            separators[i] = "";
            widths[i] = header.length();
            classes[i] = colClass;
            
            i++;
        }
    
        // create the table the will hold all of the values
        ArrayList<String[]> table = new ArrayList<>();
    
        // add the values to the table
        while (data.moveToNext()) {
            String[] row = new String[colCount];
    
            for (i = 0; i < colCount; i++) {
                String value;
                
                switch (classes[i]) {
                    case "String":
                    case "Blob":
                        value = data.getString(i);
                        break;
                    case "Integer":
                        value = String.valueOf(data.getInt(i));
                        break;
                    case "Long":
                        value = String.valueOf(data.getLong(i));
                        break;
                    case "Date":
                        value = Date.parseLong(data.getLong(i)).getDateDisplay();
                        break;
                    case "DateTime":
                        value = Date.parseLong(data.getLong(i)).toSQL();
                        break;
                    case "Time":
                        value = Date.parseLong(data.getLong(i)).getTimeDisplay();
                        break;
                    case "Boolean":
                        value = data.getInt(i) == 1 ? "true" : "false";
                        break;
                    case "Double":
                        value = String.valueOf(data.getDouble(i));
                        break;
                    case "Phone":
                        value = MyFunctions.getPhoneDisplay(String.valueOf(data.getString(i)), '.');
                        break;
                    case "Term":
                        Term term = Term.findByID(data.getLong(i));
                        value = term != null ? term.getTitle() : "null";
                        break;
                    case "Course":
                        Course course = Course.findByID(data.getLong(i));
                        value = course != null ? course.getTitle() : "null";
                        break;
                    case "Mentor":
                        Mentor mentor = Mentor.findByID(data.getLong(i));
                        value = mentor != null ? mentor.getName() : "null";
                        break;
                    case "Course Status":
                        Status status = Status.get(data.getInt(i));
                        value = status != null ? status.getName() : "null";
                        break;
                    case "Assessment Type":
                        Assessment.Type type = Assessment.Type.get(data.getInt(i));
                        value = type != null ? type.name() : "null";
                        break;
                    default:
                        value = "UNDEFINED";
                }
    
                row[i] = value;
    
                // set max width for each column
                int len = value.length();
                if (len > widths[i])
                    widths[i] = len;
            }
            
            table.add(row);
        }
    
        // limit width to max width
        for (i = 0; i < colCount; i++) {
            int maxWidth = 50;
            if (widths[i] > maxWidth) widths[i] = maxWidth;
        }
        
        // add padding to all of the values
        table.forEach(row -> {
            for (int j = 0; j < colCount; j++) {
                int width = widths[j];
                String value = row[j];
                switch (classes[j]) {
                    case "Integer":
                    case "Long":
                    case "Double":
                        value = " " + alignRight(value, width) + " ";
                        break;
                    case "Boolean":
                    case "Date":
                    case "DateTime":
                    case "Time":
                        value = " " + alignCenter(value, width) + " ";
                        break;
                    default:
                        value = " " + alignLeft(value, width) + " ";
                }
                row[j] = value;
            }
        });
    
        // add padding to the headers and create separators
        for (i = 0; i < colCount; i++) {
            headers[i] = alignCenter(headers[i], widths[i] + 2);
            separators[i] = repeat("-", widths[i] + 2);
        }
        
        // add headers and separators to the table
        table.add(0, headers);
        table.add(1, separators);
        
        // create the output string
        String output = tableName.toUpperCase() + " TABLE";
        
        for (String[] row : table) {
            output += "\n|";
    
            for (i = 0; i < colCount; i++) {
                output += row[i] + "|";
            }
        }
        
        // return output
        return output;
    }
    public String getTermTable() {
        LinkedHashMap<String, String> colInfo = new LinkedHashMap<>();
        colInfo.put("ID", "Long");
        colInfo.put("TITLE", "String");
        colInfo.put("START DATE", "Date");
        colInfo.put("END DATE", "Date");
        
        return getTable("Term", colInfo);
    }
    public String getCourseTable() {
        LinkedHashMap<String, String> colInfo = new LinkedHashMap<>();
        colInfo.put("ID", "Long");
        colInfo.put("TERM", "Term");
        colInfo.put("TITLE", "String");
        colInfo.put("CREDITS", "Integer");
        colInfo.put("START DATE", "Date");
        colInfo.put("END DATE", "Date");
        colInfo.put("START ALARM", "DateTime");
        colInfo.put("END ALARM", "DateTime");
        colInfo.put("STATUS", "Course Status");
    
        return getTable("Course", colInfo);
    }
    public String getMentorTable() {
        LinkedHashMap<String, String> colInfo = new LinkedHashMap<>();
        colInfo.put("ID", "Long");
        colInfo.put("NAME", "String");
        colInfo.put("PHONE NUMBER", "Phone");
        colInfo.put("EMAIL ADDRESS", "String");
    
        return getTable("Mentor", colInfo);
    }
    public String getCourseMentorTable() {
        LinkedHashMap<String, String> colInfo = new LinkedHashMap<>();
        colInfo.put("ID", "Long");
        colInfo.put("COURSE", "Course");
        colInfo.put("MENTOR", "Mentor");
    
        return getTable("CourseMentor", colInfo);
    }
    public String getAssessmentTable() {
        LinkedHashMap<String, String> colInfo = new LinkedHashMap<>();
        colInfo.put("ID", "Long");
        colInfo.put("COURSE", "Course");
        colInfo.put("TYPE", "Assessment Type");
        colInfo.put("NAME", "String");
        colInfo.put("DESCRIPTION", "Blob");
        colInfo.put("COMPLETION DATE", "Date");
        colInfo.put("ALARM", "DateTime");
        colInfo.put("COMPLETE", "Boolean");
    
        return getTable("Assessment", colInfo);
    }
    public String getNoteTable() {
        LinkedHashMap<String, String> colInfo = new LinkedHashMap<>();
        colInfo.put("ID", "Long");
        colInfo.put("COURSE", "Course");
        colInfo.put("MESSAGE", "Blob");
    
        return getTable("Note", colInfo);
    }
    
    public void printTermTable() {
        printTable(getTermTable());
    }
    public void printCourseTable() {
        printTable(getCourseTable());
    }
    public void printMentorTable() {
        printTable(getMentorTable());
    }
    public void printCourseMentorTable() {
        printTable(getCourseMentorTable());
    }
    public void printAssessmentTable() {
        printTable(getAssessmentTable());
    }
    public void printNoteTable() {
        printTable(getNoteTable());
    }
    private void printTable(String tableOutput) {
        Log.i(TAG, "printTable: \n" + tableOutput);
    }
    public void printAllTables() {
        printTermTable();
        printCourseTable();
        printMentorTable();
        printCourseMentorTable();
        printAssessmentTable();
        printNoteTable();
    }
}

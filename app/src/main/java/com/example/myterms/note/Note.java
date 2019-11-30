package com.example.myterms.note;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.example.myterms.course.Course;

import java.util.ArrayList;

import static com.example.myterms.application.App.DATABASE;

public class Note implements Comparable<Note>, Parcelable {
    private long id;
    private Course course;
    private String message;

    private Note(long id, Course course, String message) {
        this.id = id;
        this.course = course;
        this.message = message;
    }
    
    protected Note(Parcel in) {
        id = in.readLong();
        course = in.readParcelable(Course.class.getClassLoader());
        message = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(course, flags);
        dest.writeString(message);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }
        
        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
    
    public long getId() {
        return id;
    }
    public Course getCourse() {
        return course;
    }
    public String getMessage() {
        return message;
    }
    
    public static boolean nameIsUnique(Course course, String name, Note except) {
        if (course == null || name == "") return true;
        
        ArrayList<Note> list = findAll("WHERE course_id = " + course.getId() + "\nAND name = '" + name + "'");
        
        int size = list.size();
        return size == 0 || (size == 1 && list.get(0).id == except.id);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", course=" + course.getTitle() +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public int compareTo(Note other) {
        int compared = this.course.compareTo(other.course);
        if (compared != 0) return compared;

        return (int)(this.id - other.id);
    }

    public static Note parseSQL(Cursor data) {
        long id = data.getLong(0);
        Course course = Course.findByID(data.getInt(1));
        String message = data.getString(2);

        return new Note(id, course, message);
    }
    public static Note create(Course course, String message) {
        ContentValues values = new ContentValues();

        values.put("course_id", course.getId());
        values.put("message", message);
    
        long id = DATABASE.insert("Note", values);

        return (id >= 0) ? findByID(id) : null;
    }

    public static ArrayList<Note> findAll() {
        return findAll("");
    }
    public static ArrayList<Note> findAll(String conditions) {
        Cursor data = DATABASE.getData("Note", conditions);
        ArrayList<Note> list = new ArrayList<>();
        while (data.moveToNext()) {
            list.add(parseSQL(data));
        }
        return list;
    }
    public static Note findByID(long id) {
        ArrayList<Note> list = findAll("WHERE id = " + id);

        if (list.size() == 1) return list.get(0);
        else return null;
    }

    public static ArrayList<String> getMessages(ArrayList<Note> records) {
        ArrayList<String> list = new ArrayList<>();

        for (Note record : records) {
            list.add(record.message);
        }

        return list;
    }

    public void update(String message) {
        this.message = message;

        String sql = "UPDATE Note\n" +
                "SET message = '" + message + "' \n" +
                "WHERE id = " + id + ";";
        DATABASE.update(sql);
    }

    public void delete() {
        String sql = "DELETE FROM Note\n" +
                "WHERE id = " + id + ";";
        DATABASE.delete(sql);
    }
    
    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof Note)) return false;
        
        return this.id == ((Note) other).id;
    }
}

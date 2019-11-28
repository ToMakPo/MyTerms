package com.example.myterms.mentor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.example.myterms.application.MyFunctions;
import com.example.myterms.course.Course;

import java.util.ArrayList;

import static com.example.myterms.application.App.HELPER;

public class Mentor implements Comparable<Mentor>, Parcelable {
    private long id;
    private String name;
    private String phoneNumber;
    private String emailAddress;

    private Mentor(long id, String name, String phoneNumber, String emailAddress) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }
    
    protected Mentor(Parcel in) {
        id = in.readLong();
        name = in.readString();
        phoneNumber = in.readString();
        emailAddress = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(emailAddress);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Mentor> CREATOR = new Creator<Mentor>() {
        @Override
        public Mentor createFromParcel(Parcel in) {
            return new Mentor(in);
        }
        
        @Override
        public Mentor[] newArray(int size) {
            return new Mentor[size];
        }
    };
    
    public static boolean nameIsUnique(String name, Mentor mentor) {
        String conditions = "WHERE name = '" + name + "'" + (mentor != null ? "\nAND id != " + mentor.id : "");
        
        ArrayList<Mentor> mentors = findAll(conditions);
        
        return mentors.size() == 0;
    }
    
    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public ArrayList<Course> getCourses() {
        Cursor data = HELPER.getData("CourseMentor", "WHERE mentor_id = " + id);
        ArrayList<Course> list = new ArrayList<>();
        while (data.moveToNext()) {
            list.add(Course.findByID(data.getInt(1)));
        }
        return list;
    }

    @Override
    public String toString() {
        return "Mentor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }

    @Override
    public int compareTo(Mentor other) {
        return name.compareToIgnoreCase(other.name);
    }

    public static Mentor parseSQL(Cursor data) {
        long id = data.getLong(0);
        String name = data.getString(1);
        String phoneNumber = data.getString(2);
        String emailAddress = data.getString(3);

        return new Mentor(id, name, phoneNumber, emailAddress);
    }
    public static Mentor create(String name, String phoneNumber, String emailAddress) {
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("phone", stripPhoneNumber(phoneNumber));
        values.put("email", emailAddress);

        long id = HELPER.insert("Mentor", values);

        return (id >= 0) ? findByID(id) : null;
    }

    private static String stripPhoneNumber(String phoneNumber) {
        String output = "";

        for (Character c : phoneNumber.toCharArray()) {
            if (Character.isDigit(c)) output += c;
        }

        return output;
    }
    public String getPhoneDisplay() {
        return getPhoneDisplay(null);
    }
    public String getPhoneDisplay(Character delimiter) {
        return MyFunctions.getPhoneDisplay(phoneNumber, delimiter);
    }

    public static ArrayList<Mentor> findAll() {
        return findAll("");
    }
    public static ArrayList<Mentor> findAll(String conditions) {
        Cursor data = HELPER.getData("Mentor", conditions);
        ArrayList<Mentor> list = new ArrayList<>();
        while (data.moveToNext()) {
            list.add(parseSQL(data));
        }
        return list;
    }
    public static ArrayList<Mentor> findAllByIDs(long[] ids) {
        String idString = "";

        for (long id : ids) {
            if (!idString.isEmpty()) idString += ", ";
            idString += id;
        }

        return findAll("WHERE id IN (" + idString + ")");
    }
    public static Mentor findByID(long id) {
        ArrayList<Mentor> list = findAll("WHERE id = " + id);

        if (list.size() == 1) return list.get(0);
        else return null;
    }
    public static Mentor findByName(String name) {
        ArrayList<Mentor> list = findAll("WHERE name = '" + name + "'");

        if (list.size() == 1) return list.get(0);
        else return null;
    }

    public static ArrayList<String> getNames(ArrayList<Mentor> records) {
        ArrayList<String> list = new ArrayList<>();

        for (Mentor record : records) {
            list.add(record.name);
        }

        return list;
    }

    public void update(String name, String phoneNumber, String emailAddress) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;

        String sql = "UPDATE Mentor\n" +
                "SET name = '" + name + "', \n" +
                "\tphone = '" + phoneNumber + "', \n" +
                "\temail = '" + emailAddress + "'\n" +
                "WHERE id = " + id + ";";
        HELPER.update(sql);
    }

    public void delete() {
        ArrayList<Course> courses = getCourses();

        String sql = "DELETE FROM Mentor\n" +
                "WHERE id = " + id + ";";
        HELPER.delete(sql);

        sql = "DELETE FROM CourseMentor\n" +
                "WHERE mentor_id = " + id + ";";
        HELPER.delete(sql);

        for (Course course : courses) {
            if (course.getMentors().isEmpty())
                throw new RuntimeException("The course '" + course.getTitle() + "' has no mentors."); // TODO: 10/24/2019 require new mentor
        }
    }
    
    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof Mentor)) return false;
    
        return this.id == ((Mentor) other).id;
    }
}

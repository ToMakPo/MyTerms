package com.example.myterms.assessment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myterms.application.App;
import com.example.myterms.application.Date;
import com.example.myterms.course.Course;

import java.util.ArrayList;

import static com.example.myterms.application.App.DATABASE;
import static com.example.myterms.notifications.Alarm.ASSESSMENT_COMPLETE_CHANNEL;

public class Assessment implements Comparable<Assessment>, Parcelable {
    private long id;
    private Course course;
    private Type type;
    private String name;
    private String description;
    private Date completionDate;
    private Date alarm;
    private boolean complete;
    
    private Assessment(long id, Course course, Type type, String name, String description, Date completionDate, Date alarm, boolean complete) {
        this.id = id;
        this.course = course;
        this.type = type;
        this.name = name;
        this.description = description;
        this.completionDate = completionDate.setTime(23, 59, 59, 999);
        this.alarm = alarm;
        this.complete = complete;
    }
    
    protected Assessment(Parcel in) {
        id = in.readLong();
        course = in.readParcelable(Course.class.getClassLoader());
        type = Type.get(in.readInt());
        name = in.readString();
        description = in.readString();
        completionDate = in.readParcelable(Date.class.getClassLoader());
        alarm = in.readParcelable(Date.class.getClassLoader());
        complete = in.readByte() != 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(course, flags);
        dest.writeInt(type.index);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeParcelable(completionDate, flags);
        dest.writeParcelable(alarm, flags);
        dest.writeByte((byte) (complete ? 1 : 0));
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Assessment> CREATOR = new Creator<Assessment>() {
        @Override
        public Assessment createFromParcel(Parcel in) {
            return new Assessment(in);
        }
        
        @Override
        public Assessment[] newArray(int size) {
            return new Assessment[size];
        }
    };
    
    public long getId() {
        return id;
    }
    public Course getCourse() {
        return course;
    }
    public Type getType() { return type; }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Date getCompletionDate() {
        return completionDate;
    }
    public Date getAlarm() {
        return alarm;
    }
    public boolean isComplete() {
        return complete;
    }
    
    public static boolean nameIsUnique(Course course, String name, Assessment except) {
        if (course == null || name == "") return true;
        
        ArrayList<Assessment> list = findAll("WHERE course_id = " + course.getId() + "\nAND name = '" + name + "'");
        
        int size = list.size();
        return size == 0 || (size == 1 && list.get(0).id == except.id);
    }

    @Override
    public String toString() {
        return "Assessment{" +
                "id=" + id +
                ", course=" + course.getTitle() +
                ", type=" + type.name().toLowerCase() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", completion_date='" + completionDate.getLongDateDisplay() + '\'' +
                ", complete=" + complete +
                '}';
    }

    @Override
    public int compareTo(Assessment other) {
        int compared = this.course.compareTo(other.course);
        if (compared != 0) return compared;
        
        compared = this.type.index - other.type.index;
        if (compared != 0) return compared;

        compared = this.completionDate.compareTo(other.completionDate);
        if (compared != 0) return compared;

        return (int)(this.id - other.id);
    }

    public static Assessment parseSQL(Cursor data) {
        long id = data.getLong(0);
        Course course = Course.findByID(data.getInt(1));
        Type type = Type.get(data.getInt(2));
        String name = data.getString(3);
        String description = data.getString(4);
        Date completionDate = Date.parseLong(data.getLong(5));
        Date alarm = Date.parseLong(data.getLong(6));
        boolean complete = data.getInt(7) == 1;

        return new Assessment(id, course, type, name, description, completionDate, alarm, complete);
    }
    public static Assessment create(Course course, Type type, String name, String description) {
        return create(course, type, name, description, false);
    }
    public static Assessment create(Course course, Type type, String name, String description, boolean complete) {
        return create(course, type, name, description, course.getEndDate(), complete);
    }
    public static Assessment create(Course course, Type type, String name, String description, Date completionDate) {
        return create(course, type, name, description, completionDate, false);
    }
    public static Assessment create(Course course, Type type, String name, String description, Date completionDate, boolean complete) {
        return create(course, type, name, description, completionDate, Date.of(completionDate).setTime(9, 0), complete);
    }
    public static Assessment create(Course course, Type type, String name, String description, Date completionDate, Date alarm) {
        return create(course, type, name, description, completionDate, alarm, false);
    }
    public static Assessment create(Course course, Type type, String name, String description, Date completionDate, Date alarm, boolean complete) {
        ContentValues values = new ContentValues();
        
        values.put("course_id", course.getId());
        values.put("type", type.index);
        values.put("name", name);
        values.put("description", description);
        values.put("completion_date", completionDate.toLong());
        values.put("alarm", alarm.toLong());
        values.put("complete", complete ? 1 : 0);
    
        long id = DATABASE.insert("Assessment", values);
        
        Assessment assessment = (id >= 0) ? findByID(id) : null;
        if (assessment != null) {
            assessment.setAlarm();
        }
        
        return assessment;
    }
    
    public void setAlarm(Date alarm) {
        this.alarm = alarm;
        setAlarm();
    }
    private void setAlarm() {
        Date now = Date.now();
        if (alarm.isAfter(now)) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("assessment", this);
            bundle.putParcelable("course", course);
            ASSESSMENT_COMPLETE_CHANNEL.setAlarm(App.MAIN, "Expected Completion Date",
                    "The " + (type.equals(Type.ALL) ? "" : type.name().toLowerCase() + " ") + "assessment for course '" + course.getTitle() + "' called '" +
                            name + "' is expected to be completed " + (completionDate.equals(Date.today()) ? "today" : "on " + completionDate.getFormatted("%tb %<te")) + ".", alarm, (int) id, bundle);
        }
    }
    public void cancelAlarm() {
        ASSESSMENT_COMPLETE_CHANNEL.cancelAlarm(App.MAIN, (int) id);
    }
    
    public static ArrayList<Assessment> findAll() {
        return findAll("");
    }
    public static ArrayList<Assessment> findAll(String conditions) {
        Cursor data = DATABASE.getData("Assessment", conditions);
        ArrayList<Assessment> list = new ArrayList<>();
        while (data.moveToNext()) {
            list.add(parseSQL(data));
        }
        return list;
    }
    public static ArrayList<Assessment> findAllByType(Course course, Type type) {
        if (type.equals(Type.ALL)) return findAllByCourse(course);
        return findAll("WHERE course_id = " + course.getId() + "\nAND type = " + type.index);
    }
    public static ArrayList<Assessment> findAllByCourse(Course course) {
        return findAll("WHERE course_id = " + course.getId());
    }
    public static ArrayList<Assessment> findAllUpcoming() {
        long sLong = Date.today().toLong();
        long eLong = Date.today().addDays(30).toLong();
    
        return findAll("WHERE complete = 0\nAND completion_date BETWEEN " + sLong + " AND " + eLong);
    }
    public static Assessment findByID(long id) {
        ArrayList<Assessment> list = findAll("WHERE id = " + id);

        if (list.size() == 1) return list.get(0);
        else return null;
    }
    public static Assessment findByName(Course course, String name) {
        ArrayList<Assessment> list = findAll("WHERE course_id = " + course.getId() + " AND name = '" + name + "'");

        if (list.size() == 1) return list.get(0);
        else return null;
    }

    public void update(String name, String description, Date completionDate, Date alarm, boolean complete) {
        this.name = name;
        this.description = description;
        this.completionDate = completionDate;
        this.alarm = alarm;
        this.complete = complete;

        String sql = "UPDATE Assessment\n" +
                "SET name = '" + name + "', \n" +
                "\tdescription = '" + description + "', \n" +
                "\tcompletion_date = '" + completionDate.toLong() + "', \n" +
                "\talarm = '" + alarm.toLong() + "', \n" +
                "\tcomplete = " + (complete ? 1 : 0) + "\n" +
                "WHERE id = " + id + ";";
        DATABASE.update(sql);
    
        setAlarm();
    }

    public void markAsComplete() {
        complete = true;
        String sql = "UPDATE Assessment\n" +
                "SET complete = 1\n" +
                "WHERE id = " + id + ";";
        DATABASE.update(sql);
        cancelAlarm();
    }
    public void markAsIncomplete() {
        complete = false;
        String sql = "UPDATE Assessment\n" +
                "SET complete = 0\n" +
                "WHERE id = " + id + ";";
        DATABASE.update(sql);
        setAlarm();
    }

    public void delete() {
        cancelAlarm();
        String sql = "DELETE FROM Assessment\n" +
                "WHERE id = " + id + ";";
        DATABASE.delete(sql);
    }
    
    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof Assessment)) return false;
        
        return this.id == ((Assessment) other).id;
    }
    
    public enum Type {
        ALL (0),
        OBJECTIVE (1),
        PERFORMANCE (2);
        
        int index;
    
        Type(int index) {
            this.index = index;
        }
    
        public int getIndex() {
            return index;
        }
        
        public static Type get(int index) {
            switch (index){
                case 0: return ALL;
                case 1: return OBJECTIVE;
                case 2: return PERFORMANCE;
                default: throw new IndexOutOfBoundsException(index + " in not one of the options for Assessment Type.");
            }
        }
        
        public boolean equals(Type other) {
            return this.index == other.index;
        }
        
        @NonNull
        @Override
        public String toString() {
            return name();
        }
    }
}

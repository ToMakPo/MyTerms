package com.example.myterms.term;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myterms.application.Date;
import com.example.myterms.course.Course;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.myterms.application.App.HELPER;

public class Term implements Comparable<Term>, Parcelable {
    private static final String TAG = "app: Term";

    private long id;
    private String title;
    private Date startDate;
    private Date endDate;

    private Term(long id, String title, Date startDate, Date endDate) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    protected Term(Parcel in) {
        id = in.readLong();
        title = in.readString();
        startDate = in.readParcelable(Date.class.getClassLoader());
        endDate = in.readParcelable(Date.class.getClassLoader());
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeParcelable(startDate, flags);
        dest.writeParcelable(endDate, flags);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<Term> CREATOR = new Creator<Term>() {
        @Override
        public Term createFromParcel(Parcel in) {
            return new Term(in);
        }
        
        @Override
        public Term[] newArray(int size) {
            return new Term[size];
        }
    };
    
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    public Date getStartDate() {
        return startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public ArrayList<Course> getCourses() {
        return Course.findAll("WHERE term_id = " + id);
    }
    public ArrayList<Course> getActiveCourses() {
        return Course.findAll("WHERE term_id = " + id + " AND status >= 0");
    }

    public boolean hasPassed() {
        ArrayList<Course> courses = getCourses();

        if (courses.isEmpty()) return false;

        for (Course course : courses) {
            if (course.getStatus() != Course.Status.DROPPED && !course.allAssessmentsCompleted())
                return false;
        }

        return true;
    }
    public boolean isActive() {
        ArrayList<Course> courses = getCourses();

        if (courses.isEmpty()) return true;

        for (Course course : courses) {
            if (course.isActive()) return true;
        }

        return false;
    }

    @NotNull
    @Override
    public String toString() {
        return "Term{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
    public Integer[] getCredits() {
        int count = 0;
        int total = 0;

        for (Course course : getCourses()) {
            if (course.getStatus().getIndex() >= 0) {
                int credits = course.getCredits();
                if (course.allAssessmentsCompleted()) count += credits;
                total += credits;
            }
        }

        return new Integer[] {count, total};
    }
    public String getDateRangeDisplay() {
        return Date.getDateRangeDisplay(startDate, endDate);
    }
    public String getCreditsDisplay() {
        Integer[] credits = getCredits();
        return "Credits: " + credits[0] + "/" + credits[1];
    }
    public boolean hasCourseWithTitle(String otherTitle) {
        for (Course course : getCourses()) {
            if (course.getTitle().equals(otherTitle)) return true;
        }

        return false;
    }

    @Override
    public int compareTo(@NotNull Term other) {
        int compared = this.startDate.compareTo(other.startDate);
        if (compared != 0) return compared;

        compared = this.endDate.compareTo(other.endDate);
        if (compared != 0) return compared;

        compared = this.title.compareToIgnoreCase(other.title);
        if (compared != 0) return compared;

        compared = (int)(this.id - other.id);
        return compared;
    }

    public static Term parseSQL(Cursor data) {
        long id = data.getLong(0);
        String title = data.getString(1);
        Date startDate = Date.parseSQL(data.getString(2));
        Date endDate = Date.parseSQL(data.getString(3));

        return new Term(id, title, startDate, endDate);
    }
    public static Term create(String title, Date startDate, Date endDate) {
        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("start_date", startDate.toSQL());
        values.put("end_date", endDate.toSQL());

        long id = HELPER.insert("Term", values);

        return (id >= 0) ? findByID(id) : null;
    }

    public static ArrayList<Term> findAll() {
        return findAll("");
    }
    public static ArrayList<Term> findAll(String conditions) {
        Cursor data = HELPER.getData("Term", conditions);
        ArrayList<Term> list = new ArrayList<>();
        while (data.moveToNext()) {
            list.add(parseSQL(data));
        }
        return list;
    }
    public static Term findByID(long id) {
        ArrayList<Term> list = findAll("WHERE id = " + id);

        if (list.size() == 1) return list.get(0);
        else return null;
    }
    public static Term findByTitle(String title) {
        ArrayList<Term> list = findAll("WHERE title = '" + title + "'");

        if (list.size() == 1) return list.get(0);
        else return null;
    }
    public static ArrayList<Term> findAllActive() {
        ArrayList<Term> output = new ArrayList<>();

        for (Term term : findAll()) {
            if (term.isActive()) output.add(term);
        }

        return output;
    }

    public static ArrayList<String> getTitles() {
        return getTitles(findAll());
    }
    public static ArrayList<String> getTitles(ArrayList<Term> records) {
        ArrayList<String> list = new ArrayList<>();

        for (Term record : records) {
            list.add(record.title);
        }

        return list;
    }
    public static boolean titleIsUnique(String title) {
        ArrayList<Term> list = findAll("WHERE title = '" + title + "'");
        
        return list.isEmpty();
    }
    public static boolean titleIsUnique(String title, Term except) {
        if (except == null) return titleIsUnique(title);
        
        if (title.isEmpty()) return true;
        
        ArrayList<Term> list = findAll("WHERE title = '" + title + "'\nAND id != " + except.id);
        
        return list.isEmpty();
    }

    public void update(String title, Date startDate, Date endDate ) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    
        // TODO: 11/20/2019 update course dates and alarms when term dates change

        String sql = "UPDATE Term\n" +
                "SET title = '" + title + "', \n" +
                "\tstart_date = '" + startDate.toSQL() + "', \n" +
                "\tend_date = '" + endDate.toSQL() + "'\n" +
                "WHERE id = " + id + ";";
        HELPER.update(sql);
    }

    public void delete() {
        if (getCourses().isEmpty()) {
            String sql = "DELETE FROM Term\n" +
                    "WHERE id = " + id + ";";
            HELPER.delete(sql);
        } else {
            Log.e(TAG, "delete: term could not delete term because it still has courses");
            // TODO: 10/24/2019 ask user for next steps
        }
    }

    public void moveCoursesAndDelete(Term newTerm) {
        long newTermID = newTerm.id;

        String sql = "UPDATE Course\n" +
                "SET term_id = " + newTermID + "\n" +
                "WHERE term_id = " + id + ";";
        HELPER.update(sql);

        sql = "DELETE FROM Term\n" +
                "WHERE id = " + id + ";";
        HELPER.delete(sql);
    }

    public void deleteCoursesAndDelete() {
        for (Course course : getCourses()) {
            course.delete();
        }

        String sql = "DELETE FROM Term\n" +
                "WHERE id = " + id + ";";
        HELPER.delete(sql);
    }
    
    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof Term)) return false;
        
        return this.id == ((Term) other).id;
    }
}

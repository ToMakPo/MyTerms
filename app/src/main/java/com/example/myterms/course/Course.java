package com.example.myterms.course;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myterms.application.App;
import com.example.myterms.application.Date;
import com.example.myterms.assessment.Assessment;
import com.example.myterms.mentor.Mentor;
import com.example.myterms.note.Note;
import com.example.myterms.term.Term;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.myterms.application.App.HELPER;
import static com.example.myterms.notifications.Alarm.COURSE_END_CHANNEL;
import static com.example.myterms.notifications.Alarm.COURSE_START_CHANNEL;

public class Course implements Comparable<Course>, Parcelable {
    private static final String TAG = "app: Course";

    private long id;
    private Term term;
    private String title;
    private int credits;
    private Date startDate;
    private Date endDate;
    private Date startAlarm;
    private Date endAlarm;
    private Status status;

    private Course(long id, Term term, String title, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm, Status status) {
        this.id = id;
        this.term = term;
        this.title = title;
        this.credits = credits;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startAlarm = startAlarm;
        this.endAlarm = endAlarm;
        this.status = status;
    }
    
    protected Course(Parcel in) {
        id = in.readLong();
        term = in.readParcelable(Term.class.getClassLoader());
        title = in.readString();
        credits = in.readInt();
        startDate = in.readParcelable(Date.class.getClassLoader());
        endDate = in.readParcelable(Date.class.getClassLoader());
        startAlarm = in.readParcelable(Date.class.getClassLoader());
        endAlarm = in.readParcelable(Date.class.getClassLoader());
        status = Status.get(in.readInt());
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(term, flags);
        dest.writeString(title);
        dest.writeInt(credits);
        dest.writeParcelable(startDate, flags);
        dest.writeParcelable(endDate, flags);
        dest.writeParcelable(startAlarm, flags);
        dest.writeParcelable(endAlarm, flags);
        dest.writeInt(status.index);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }
        
        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
    
    public long getId() {
        return id;
    }
    public Term getTerm() {
        return term;
    }
    public String getTitle() {
        return title;
    }
    public int getCredits() {
        return credits;
    }
    public Date getStartDate() {
        return startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public Date getStartAlarm() {
        return startAlarm;
    }
    public Date getEndAlarm() {
        return endAlarm;
    }
    public Status getStatus() {
        if (status.equals(Status.DROPPED)) return Status.DROPPED;
        
        if (allAssessmentsCompleted()) return updateStatus(Status.COMPLETE);
    
        if (Date.today().isBefore(startDate)) return updateStatus(Status.PLAN_TO_TAKE);
        
        return updateStatus(Status.IN_PROGRESS);
    }
    public ArrayList<Mentor> getMentors() {
        Cursor data = HELPER.getData("CourseMentor", "WHERE course_id = " + id);
        ArrayList<Mentor> list = new ArrayList<>();
        while (data.moveToNext()) {
            list.add(Mentor.findByID(data.getInt(2)));
        }
        return list;
    }
    public ArrayList<Assessment> getObjectives() {
        return Assessment.findAllByType(this, Assessment.TYPE_OBJECTIVE);
    }
    public ArrayList<Assessment> getPerformances() {
        return Assessment.findAllByType(this, Assessment.TYPE_PERFORMANCE);
    }
    public ArrayList<Assessment> getAssessments() {
        return Assessment.findAll("WHERE course_id = " + id);
    }
    public ArrayList<Assessment> getAssessments(int type) {
        return Assessment.findAllByType(this, type);
    }
    public ArrayList<Note> getNotes() {
        return Note.findAll("WHERE course_id = " + id);
    }
    
    public void setStartAlarm(Date alarm) {
        this.startAlarm = alarm;
        setStartAlarm();
    }
    private void setStartAlarm() {
        Date now = Date.now();
        if (startAlarm.isAfter(now)) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("course", this);
            bundle.putParcelable("term", term);
            COURSE_START_CHANNEL.setAlarm(App.MAIN, "Course Start Date",
                    "The course called '" + title + "' is expected to be start " +
                            (startDate.equals(Date.today()) ? "today" : "on " +
                                    startDate.getFormatted("%tb %<te")) + ".",
                    startAlarm, (int) id, bundle);
        }
    }
    public void cancelStartAlarm() {
        COURSE_START_CHANNEL.cancelAlarm(App.MAIN, (int) id);
    }
    public void setEndAlarm(Date alarm) {
        this.endAlarm = alarm;
        setEndAlarm();
    }
    private void setEndAlarm() {
        Date now = Date.now();
        if (endAlarm.isAfter(now)) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("course", this);
            bundle.putParcelable("term", term);
            COURSE_END_CHANNEL.setAlarm(App.MAIN, "Course End Date",
                    "The course called '" + title + "' is expected to be end " +
                            (endDate.equals(Date.today()) ? "today" : "on " +
                                    endDate.getFormatted("%tb %<te")) + ".",
                    endAlarm, (int) id, bundle);
        }
    }
    public void cancelEndAlarm() {
        COURSE_END_CHANNEL.cancelAlarm(App.MAIN, (int) id);
    }
    
    public void drop() {
        updateStatus(Status.DROPPED);
    }
    public Status restore() {
        if (allAssessmentsCompleted()) return updateStatus(Status.COMPLETE);
    
        if (Date.today().isBefore(startDate)) return updateStatus(Status.PLAN_TO_TAKE);
    
        return updateStatus(Status.IN_PROGRESS);
    }
    
    public Mentor addMentor(String name, String phoneNumber, String emailAddress) {
        return addMentor(Mentor.create(name, phoneNumber, emailAddress));
    }
    public Mentor addMentor(Mentor mentor) {
        if (mentor == null) return null;

        ContentValues values = new ContentValues();

        values.put("course_id", id);
        values.put("mentor_id", mentor.getId());

        long id = HELPER.insert("CourseMentor", values);

        return (id >= 0) ? mentor : null;
    }
    public ArrayList<Mentor> addMentors(Mentor ...mentors) {
        return addMentors(new ArrayList<>(Arrays.asList(mentors)));
    }
    public ArrayList<Mentor> addMentors(ArrayList<Mentor> mentors) {
        ArrayList<Mentor> list = new ArrayList<>();

        for (Mentor mentor : mentors) {
            if (addMentor(mentor) != null) list.add(mentor);
        }

        return list;
    }
    public boolean allAssessmentsCompleted() {
        ArrayList<Assessment> assessments = getAssessments();

        if (assessments.isEmpty()) return false;

        for (Assessment assessment : assessments) {
            if (!assessment.isComplete()) return false;
        }
        return true;
    }
    public boolean isActive() {
        return status.index > 0;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", term=" + term.getTitle() +
                ", title='" + title + '\'' +
                ", credits=" + credits +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status.name +
                '}';
    }
    public String getDateRangeDisplay() {
        return Date.getDateRangeDisplay(startDate, endDate);
    }
    public String getMentorDisplay() {
        ArrayList<Mentor> mentors = getMentors();

        if (mentors.size() == 1) {
            return "Mentor: " + mentors.get(0).getName();
        } else {
            String display = "";
            for (Mentor mentor : mentors) {
                if (!display.isEmpty()) display += ", ";
                display += mentor.getName();
            }
            return "Mentors: " + display;
        }
    }
    public String getCreditsDisplay() {
        return "Credits: " + credits;
    }

    @Override
    public int compareTo(Course other) {
        int compared = this.term.compareTo(other.term);
        if (compared != 0) return compared;

        compared = this.status.index - other.status.index;
        if (compared != 0) return compared;

        compared = this.startDate.compareTo(other.startDate);
        if (compared != 0) return compared;

        compared = this.endDate.compareTo(other.endDate);
        if (compared != 0) return compared;

        compared = this.title.compareToIgnoreCase(other.title);
        if (compared != 0) return compared;

        compared = (int)(this.id - other.id);
        return compared;
    }

    public static Course parseSQL(Cursor data) {
        long id = data.getLong(0);
        Term term = Term.findByID(data.getInt(1));
        String title = data.getString(2);
        int credits = data.getInt(3);
        Date startDate = Date.parseSQL(data.getString(4));
        Date endDate = Date.parseSQL(data.getString(5));
        Date startAlarm = Date.parseSQL(data.getString(6));
        Date endAlarm = Date.parseSQL(data.getString(7));
        Status status = Status.get(data.getInt(8));

        return new Course(id, term, title, credits, startDate, endDate, startAlarm, endAlarm, status);
    }
    
    public static Course create(Term term, String title, int credits, Mentor ...mentors) {
        return create(term, title, credits, term.getStartDate(), term.getEndDate(), mentors);
    }
    public static Course create(Term term, String title, int credits, Status status, Mentor ...mentors) {
        return create(term, title, credits, term.getStartDate(), term.getEndDate(), mentors);
    }
    public static Course create(Term term, String title, int credits, Date startDate, Date endDate, Mentor ...mentors) {
        Date startAlarm = Date.of(startDate).setTime(9,0);
        Date endAlarm = Date.of(endDate).setTime(9,0);
        
        return create(term, title, credits, startDate, endDate, startAlarm, endAlarm, mentors);
    }
    public static Course create(Term term, String title, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm, Mentor ...mentors) {
        return create(term, title, credits, startDate, endDate, startAlarm, endAlarm, Status.PLAN_TO_TAKE, mentors);
    }
    public static Course create(Term term, String title, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm, Status status, Mentor ...mentors) {
        Course course = create(term, title, credits, startDate, endDate, startAlarm, endAlarm, status);

        if (course != null) {
            course.addMentors(mentors);
        }

        return course;
    }
    public static Course create(Term term, String title, ArrayList<Mentor> mentors, int credits) {
        return create(term, title, mentors, credits, term.getStartDate(), term.getEndDate());
    }
    public static Course create(Term term, String title, ArrayList<Mentor> mentors, int credits, Date startDate, Date endDate) {
        Date startAlarm = Date.of(startDate).setTime(9,0);
        Date endAlarm = Date.of(endDate).setTime(9,0);
        
        return create(term, title, mentors, credits, startDate, endDate, startAlarm, endAlarm);
    }
    public static Course create(Term term, String title, ArrayList<Mentor> mentors, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm) {
        return create(term, title, mentors, credits, startDate, endDate, startAlarm, endAlarm, Status.PLAN_TO_TAKE);
    }
    public static Course create(Term term, String title, ArrayList<Mentor> mentors, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm, Status status) {
        Course course = create(term, title, credits, startDate, endDate, startAlarm, endAlarm, status);

        if (course != null) {
            course.addMentors(mentors);
        }

        return course;
    }
    private static Course create(Term term, String title, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm, Status status) {
        if (!term.hasCourseWithTitle(title)) {
            ContentValues values = new ContentValues();

            values.put("term_id", term.getId());
            values.put("title", title);
            values.put("credits", credits);
            values.put("start_date", startDate.toSQL());
            values.put("end_date", endDate.toSQL());
            values.put("start_alarm", startAlarm.toSQL());
            values.put("end_alarm", endAlarm.toSQL());
            values.put("status", status.index);

            long id = HELPER.insert("Course", values);
    
            Course course = (id >= 0) ? findByID(id) : null;
            if (course != null) {
                course.setStartAlarm();
                course.setEndAlarm();
            }
    
            return course;
        } else {
            Log.e(TAG, "create: Could not create this course because the term already has a course with this title.");

            return null;
        }
    }

    public static ArrayList<Course> findAll() {
        return findAll("");
    }
    public static ArrayList<Course> findAll(String conditions) {
        Cursor data = HELPER.getData("Course", conditions);
        ArrayList<Course> list = new ArrayList<>();
        while (data.moveToNext()) {
            list.add(parseSQL(data));
        }
        return list;
    }
    public static ArrayList<Course> findAllByTitle(String title) {
        return findAll("WHERE title = '" + title + "'");
    }
    public static Course findByTermAndTitle(Term term, String title) {
        ArrayList<Course> list = findAll("WHERE term_id = " + term.getId() + "\nAND title = '" + title + "'");

        if (list.size() == 1) return list.get(0);
        else return null;
    }
    public static Course findByID(long id) {
        ArrayList<Course> list = findAll("WHERE id = " + id);

        if (list.size() == 1) return list.get(0);
        else return null;
    }
    public static ArrayList<Course> findAllByStatus(Term term, @NotNull Status ...statuses) {
        String indexes = "";

        for (Status status : statuses) {
            if (!indexes.isEmpty()) indexes += ", ";
            indexes += status.index;
        }

        String conditions = "WHERE term_id = " + term.getId() + "\nAND status IN (" + indexes + ")";
        return findAll(conditions);
    }
    public static boolean titleIsUnique(Term term, String title) {
        if (term == null || title.isEmpty()) return true;
    
        ArrayList<Course> list = findAll("WHERE term_id = " + term.getId() + "\nAND title = '" + title + "'");
    
        return list.isEmpty();
    }
    public static boolean titleIsUnique(Term term, String title, Course except) {
        if (except == null) return titleIsUnique(term, title);
        
        if (term == null || title.isEmpty()) return true;
        
        ArrayList<Course> list = findAll("WHERE term_id = " + term.getId() + "\nAND title = '" + title + "'\nAND id != " + except.id);
    
        return list.isEmpty();
    }

    public void updateTerm(Term newTerm) {
        if (!term.equals(newTerm)) {
            if (!newTerm.hasCourseWithTitle(title)) {
                this.term = newTerm;
                String sql = "UPDATE Course\n" +
                        "SET term = " + term.getId() + ", \n" +
                        "WHERE id = " + id + ";";
                HELPER.update(sql);
            } else {
                Log.e(TAG, "create: Could not move this course because the new term already has a course with this title.");
            }
        }
    }
    public Status updateStatus(Status status) {
        this.status = status;

        String sql = "UPDATE Course\n" +
                "SET status = " + status.index + "\n" +
                "WHERE id = " + id + ";";
        HELPER.update(sql);
        
        if (isActive()) {
            setStartAlarm();
            setEndAlarm();
        } else {
            cancelStartAlarm();
            cancelEndAlarm();
        }
        
        return this.status;
    }
    public void updateMentors(ArrayList<Mentor> mentors) {
        String sql = "DELETE FROM CourseMentor\n" +
                "WHERE course_id = " + id + ";";
        HELPER.delete(sql);

        addMentors(mentors);
    }
    public void update(Term term, String title, ArrayList<Mentor> mentors, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm, Status status) {
        update(term, title, credits, startDate, endDate, startAlarm, endAlarm, status);
        updateMentors(mentors);
    }
    public void update(String title, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm, Status status) {
        update(term, title, credits, startDate, endDate, startAlarm, endAlarm, status);
    }
    public void update(Term term, String title, int credits, Date startDate, Date endDate, Date startAlarm, Date endAlarm, Status status) {
        this.term = term;
        this.title = title;
        this.credits = credits;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startAlarm = startAlarm;
        this.endAlarm = endAlarm;
        this.status = status;
        
        // TODO: 11/20/2019 update assessment dates and alarms when course dates change

        String sql = "UPDATE Course\n" +
                "SET term_id = " + term.getId() + ", \n" +
                "\ttitle = '" + title + "', \n" +
                "\tcredits = " + credits + ", \n" +
                "\tstart_date = '" + startDate.toSQL() + "', \n" +
                "\tend_date = '" + endDate.toSQL() + "', \n" +
                "\tstatus = " + status.index + "\n" +
                "WHERE id = " + id + ";";
        HELPER.update(sql);
    
        if (isActive()) {
            setStartAlarm();
            setEndAlarm();
        } else {
            cancelStartAlarm();
            cancelEndAlarm();
        }
    }

    public void delete() {
        String sql = "DELETE FROM Assessment\n" +
                "WHERE course_id = " + id + ";";
        HELPER.delete(sql);
        sql = "DELETE FROM Note\n" +
                "WHERE course_id = " + id + ";";
        HELPER.delete(sql);
        sql = "DELETE FROM CourseMentor\n" +
                "WHERE course_id = " + id + ";";
        HELPER.delete(sql);
        sql = "DELETE FROM Course\n" +
                "WHERE id = " + id + ";";
        HELPER.delete(sql);
    
        cancelStartAlarm();
        cancelEndAlarm();
    }
    
    public Course addAssessment(int type, String name, String description) {
        Assessment.create(this, type, name, description);
        updateStatus(getStatus());
        return this;
    }
    public Course addAssessment(int type, String name, String description, boolean complete) {
        Assessment.create(this, type, name, description, complete);
        updateStatus(getStatus());
        return this;
    }
    public Course addAssessment(int type, String name, String description, Date completionDate) {
        Assessment.create(this, type, name, description, completionDate);
        updateStatus(getStatus());
        return this;
    }
    public Course addAssessment(int type, String name, String description, Date completionDate, boolean complete) {
        Assessment.create(this, type, name, description, completionDate, complete);
        updateStatus(getStatus());
        return this;
    }
    public Course addAssessment(int type, String name, String description, Date completionDate, Date alarm) {
        Assessment.create(this, type, name, description, completionDate, alarm);
        updateStatus(getStatus());
        return this;
    }
    public Course addAssessment(int type, String name, String description, Date completionDate, Date alarm, boolean complete) {
        Assessment.create(this, type, name, description, completionDate, alarm, complete);
        updateStatus(getStatus());
        return this;
    }
    
    public Course addNote(String message) {
        Note.create(this, message);
        return this;
    }
    
    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof Course)) return false;
        
        return this.id == ((Course) other).id;
    }
    
    public enum Status {
        DROPPED ("Dropped", -1),
        COMPLETE ("Complete", 0),
        IN_PROGRESS ("In Progress", 1),
        PLAN_TO_TAKE ("Plan to Take", 2);

        private String name;
        private int index;

        Status(String name, int index) {
            this.name = name;
            this.index = index;
        }
    
        @Override
        public String toString() {
            return "Status: " + name;
        }

        public String getName() {
            return name;
        }
        public int getIndex() {
            return index;
        }

        public static Status get(int index) {
            switch (index) {
                case -1: return DROPPED;
                case 0: return COMPLETE;
                case 1: return IN_PROGRESS;
                case 2: return PLAN_TO_TAKE;
                default: return null;
            }
        }
    }
}

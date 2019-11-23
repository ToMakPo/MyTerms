package com.example.myterms.application;

import android.app.Application;
import android.content.Context;

import com.example.myterms.course.Course;
import com.example.myterms.database.Database;
import com.example.myterms.mentor.Mentor;
import com.example.myterms.term.Term;

import static com.example.myterms.assessment.Assessment.TYPE_OBJECTIVE;
import static com.example.myterms.assessment.Assessment.TYPE_PERFORMANCE;

public class App extends Application {
    public static Context MAIN;
    
    private static final boolean DEBUG = false;
    public static boolean BUILD_DB = false;
    public static Database HELPER;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        MAIN = App.this;
        
        buildDatabase();
    }
    
    private void buildDatabase() {
        HELPER = new Database(this);
    
        if (DEBUG && BUILD_DB) {
            Mentor juliaHuang = Mentor.create("Julia Huang", "2535557489", "huang.juliy@sru.edu");
            Mentor adamLock = Mentor.create("Adam Lock", "2535556587", "lock.adam@sru.edu");
            Mentor emmettBrown = Mentor.create("Emmett Brown", "2535557896", "brown.emmett@sru.edu");
            Mentor katelynQuill = Mentor.create("Katelyn Quill", "2535553486", "quill.katelyn@sru.edu");
            Mentor pollyNomial = Mentor.create("Polly Nomial", "2535553158", "nomial.polly@sru.edu");
            Mentor calCulus = Mentor.create("Cal Culus", "2535554587", "culus.cal@sru.edu");
        
            Term summer2019 = Term.create("Summer 2019", Date.of(2019, 4, 1), Date.of(2019, 6, 30));
            Course.create(summer2019, "Science 102 - Molecular Bonds", 2, emmettBrown)
                .addAssessment(TYPE_OBJECTIVE, "Obj 1", "this is an objective.", true);
        
            Term fall2019 = Term.create("Fall 2019", Date.of(2019, 10, 1), Date.of(2019, 12, 31));
            Course.create(fall2019, "Math 101 - Math Basics", 3, pollyNomial)
				.addAssessment(TYPE_OBJECTIVE, "High School Math Competency", "The student can demonstrate at least high school graduate level competency in math.", true)
				.addAssessment(TYPE_OBJECTIVE, "Basic Understanding", "The student can demonstrate a basic understanding of Calculus, Trigonometry, and Statistics.", true)
				.addAssessment(TYPE_PERFORMANCE, "Pre-assessment", "Checks to see where the students understanding is at the create of the course.", true)
				.addAssessment(TYPE_PERFORMANCE, "Algebra Competency", "Tests the students understanding of Algebra.", true)
				.addAssessment(TYPE_PERFORMANCE,"Calculus Competency", "Tests the students understanding of Calculus.", true)
				.addAssessment(TYPE_PERFORMANCE,"Trigonometry Competency", "Tests the students understanding of Trigonometry.", true)
				.addAssessment(TYPE_PERFORMANCE,"Statistics Competency", "Tests the students understanding of Statistics.", true)
				.addNote("This course should be quite easy. I did really well in high school.")
                .addNote("Stats might give me some problems. I should really put more focus in that section.");
            Course.create(fall2019, "Art 101 - Basic Color Theory", 2, juliaHuang)
				.addAssessment(TYPE_OBJECTIVE,"Understanding Colors", "The student should understand what the basic colors are and how they blend.")
				.addAssessment(TYPE_OBJECTIVE,"Color Theory", "The student should understand color theory and how to create compelling color pallets.")
				.addAssessment(TYPE_PERFORMANCE,"Pre-assessment", "Checks to see where the students understanding is at the create of the course.", true)
				.addAssessment(TYPE_PERFORMANCE,"Create a Color Pallet", "The student will be assigned a theme to build a color pallet around.");
            Course.create(fall2019,"English 103 - Sighting Sources", 4, katelynQuill)
				.addAssessment(TYPE_OBJECTIVE,"Sighting Sources", "The student will have a full understanding of how to sight sources.")
				.addAssessment(TYPE_OBJECTIVE,"Plagiarism", "The student will have a full understanding of what plagiarism is and how it is different then sighting a source.")
				.addAssessment(TYPE_PERFORMANCE,"Pre-assessment", "Checks to see where the students understanding is at the create of the course.")
				.addAssessment(TYPE_PERFORMANCE,"Final Project", "The student will write a 15 page paper on a provided subject, using at least 10 sources.");
            Course.create(fall2019, "Science 101 - Intro to Science", 3, Course.Status.DROPPED, adamLock, emmettBrown);
        
            Term winter2020 = Term.create("Winter 2020", Date.of(2020, 1, 1), Date.of(2020, 3, 31));
            Course.create(winter2020, "Math 102 - Basic Calculus", 4, pollyNomial, calCulus);
            Course.create(winter2020, "Science 101 - Intro to Science", 3, adamLock, emmettBrown);
        
            Term spring2020 = Term.create("Spring 2020", Date.of(2020, 4, 1), Date.of(2020, 6, 30));
            Term summer2020 = Term.create("Summer 2020", Date.of(2020, 7, 1), Date.of(2020, 9, 30));
        }
    }
}
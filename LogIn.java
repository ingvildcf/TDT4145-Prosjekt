import java.sql.*;
import java.util.*;

public class LogIn extends DataBaseConnection {
    /* Class for LogIn. The user must enter an
    email and a password in order to log in into Piazza.
    When logged in, the user must also enter which course
    and term the user wants to enter in Piazza */

    // LogIn user information
    public String email;
    private String password;

    // LogIn Piazza information
    public String courseCode;
    public String term;
    public String userType;

    // LogIn database information
    private String dataBaseEmail;
    private String dataBasePassword;
    private String dataBaseUserType;

    private List<ArrayList<String>> dataBaseEnrolledCourses = new ArrayList<ArrayList<String>>();

    Scanner inputFromUser;

    public LogIn(Scanner scanner) {
        /* Constructor for a LogIn */
        this.inputFromUser = scanner;
        // Connecting with the database
        connect();
    }

    public void logIn(){
        /* Method that handles the user log in */
        boolean logInIsValid = false;

        // Asks for log in until a correct log in is entered
        while (!logInIsValid){
            // Log in starts
            System.out.println("--- LOGIN ---");

            // Asks the user to enter email and stores the input in the LogIn object
            System.out.println("Enter email: ");
            this.email = inputFromUser.nextLine();

            // Asks the user to enter password and stores the input in the LogIn object
            System.out.println("Enter password: ");
            this.password = inputFromUser.nextLine();

            // Check if the input is valid
            logInIsValid = this.isLogInValid();
        }
    }

    public void getLogInFromDataBase() {
        /* Method that gets data from the database and stores it within a
         LogIn object */

        try {
            // Getting data from the database that corresponds to LogIn from user
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Email, UserPassword FROM User WHERE Email ='" + email + "';");

            // Saving data from database in the LogIn object
            while (resultSet.next()) {
                this.dataBaseEmail = resultSet.getString("Email");
                this.dataBasePassword = resultSet.getString("UserPassword");
            }

        } catch (Exception e) {
            // Catching error when try fails and prints the error message
            System.out.println("Database error during selecting of user=" + e);
        }
    }

    public boolean isLogInValid() {
        /* Method that verifies the LogIn by checking if the entered password
        corresponds to the password from the database for the email given by LogIn */

        // Fetches the correct log in from the database and stores the result within the object
        this.getLogInFromDataBase();

        // Passwords is matching
        if (this.password.equals(this.dataBasePassword)) {
            System.out.println("Log in succeeded! ");
            return true;
        }
        // Password does not match
        else {
            System.out.println("Email or password incorrect");
            return false;
        }
    }

    public int getCourseFromDataBase() {
        /* Method that gets all the course the current user is invited to
         from the database and asks the user to choose which Piazza Course to enter */

        try {
            // Getting data from the database
            String SQLQuery = "SELECT ParticipatesInCourse.CourseCode, ParticipatesInCourse.Term, ParticipatesInCourse.UserType, Course.CourseName FROM ParticipatesInCourse INNER JOIN Course ON (ParticipatesInCourse.CourseCode = Course.CourseCode AND ParticipatesInCourse.Term = Course.Term) WHERE ParticipatesInCourse.Email='" + email + "';";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLQuery);

            // Saving data from database in the LogIn object
            while (resultSet.next()) {
                String dataBaseCourseCode = resultSet.getString("CourseCode");
                String dataBaseCourseName = resultSet.getString("CourseName");
                String dataBaseTerm = resultSet.getString("Term");
                this.dataBaseUserType = resultSet.getString("UserType");
                ArrayList<String> dataBaseEnrolledCours = new ArrayList<String>();
                dataBaseEnrolledCours.add(dataBaseCourseCode);
                dataBaseEnrolledCours.add(dataBaseCourseName);
                dataBaseEnrolledCours.add(dataBaseTerm);
                dataBaseEnrolledCours.add(dataBaseUserType);

                // Handling redundancy
                if (!this.dataBaseEnrolledCourses.contains(dataBaseEnrolledCours)){
                    this.dataBaseEnrolledCourses.add(dataBaseEnrolledCours);
                }
            }

            // User is not enrolled in any courses
            if (this.dataBaseEnrolledCourses.isEmpty()){
                return 0;
            }

            // User is enrolled in courses
            else {
                return 1;
            }

        } catch (Exception e) {
            // Catching error when try fails and prints the error message
            System.out.println("Database error during selecting of user=" + e);
            return 0;
        }
    }

    public boolean inputIsValid(String courseCode, String term){
        /* Method that checks if entered input is valid,
         and saves the usertype corresponding to the entered
         input when input is valid */
        for(List course : this.dataBaseEnrolledCourses){
           if (course.contains(courseCode) && course.contains(term)){
               System.out.println("Course chosen: " + course);

               // Setting userType that belongs to the chosen course
               this.userType = course.get(3).toString();
               return true;
           }
        }
        System.out.println("Not valid input. Try again");
        return false;
    }

    public void chooseCourse(){
        /* Method that makes the user choose which course to log into piazza with */
        int isEnrolledInCourses = this.getCourseFromDataBase();

        // Lists of course user is invited to is empty
        if (isEnrolledInCourses == 0) {
            System.out.println("You cannot choose a course to log into on piazza because you are not enrolled in any courses");

        } else {
            boolean inputIsValid = false;

            // Asking user for input until valid input is received
            while(!inputIsValid) {

                // Choose course starts and print the possible courses
                System.out.println("--- CHOOSE COURSE ON PIAZZA ---");
                System.out.println(this.dataBaseEnrolledCourses.toString().replace("[[", " * ").replace("],", "\n").replace("[", "* ").replace("]]", "\n"));

                // Asks the user to enter a courseCode and term and stores the input
                System.out.println("Enter course code: ");
                this.courseCode = inputFromUser.nextLine();
                System.out.println("Enter term: ");
                this.term = inputFromUser.nextLine();

                // Checking if the input is valid
                inputIsValid = this.inputIsValid(courseCode, term);
            }
        }
    }

    public String getUserEmail(){
        return (this.email);
    }

    public String getCourseCode(){
        return (this.courseCode);
    }

    public String getTerm(){
        return (this.term);
    }

    public String getUserType(){
        return (this.userType);
    }
}
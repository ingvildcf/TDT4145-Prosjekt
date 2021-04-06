import java.sql.*;
import java.util.*;

public class ReplyToPost extends CreatePost {
    /* Class for reply to post that inheritance from the CreatePost class */

    private String email;
    private String courseCode;
    private String term;
    private String colorCode;
    private int postID;
    private int originalPostID;
    private int folderID;
    private String postText;
    private List<Integer> postIDs;

    Scanner scanner;

    public ReplyToPost(String authorEmail, String courseCode, String term, Scanner scanner) {
        /* Constructor for the ReplyToPost class that calls
        the super constructor from the parent class */
        super(authorEmail, courseCode, term, scanner);
        this.email = authorEmail;
        this.courseCode = courseCode;
        this.term = term;
        this.scanner = scanner;
    }

    public void replyToPost(){
        this.postID = this.generatePostID(connection);
        this.setColorCode(connection);
        this.whichPostReplyTo();
        this.addReplyText();
        this.addReplyToDatabase(connection);
        this.tagPost(connection);
    }
    public void setColorCode(Connection connection) {
        /* Method to set color code of a post based on the user type. It returns
        colorCode that is 'dark blue' if user is a student, 'dark red' if user is
        an instructor and null for all other unknown usertypes */

        try {
            // Getting data from the database
            String queryGetUserType = "SELECT UserType FROM ParticipatesInCourse WHERE (Email = '" + this.email + "' AND CourseCode = '" + this.courseCode + "' AND Term = '" + this.term + "');";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryGetUserType);

            // Setting color code
            while (resultSet.next()) {
                String userType = resultSet.getString("UserType");
                // User is of type student
                if (userType.equalsIgnoreCase("Student")) {
                    this.colorCode = "dark blue";
                }
                // User is of type instructor
                else if (userType.equalsIgnoreCase("Instructor")) {
                    this.colorCode = "dark red";
                }
                // If user type is anything other than Student or Instructor print error message
                else {
                    System.out.println("Unknown user type for " + this.email + ".");
                    this.colorCode = null;
                }
            }
        }
        // Catching error when collecting data from the database fails
        catch (Exception e) {
            System.out.println("Failed to set color code: " + e);
            this.colorCode = null;
        }
        System.out.println("Color code set to " + this.colorCode);
    }

    public boolean isPostIDValid(int postID){
        /* Method that checks if postID is valid */
            if(postIDs.contains(postID)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isInputInteger(String userInput){
        /* Method that checks if input is integer */
        try {
            //Checks if input is integer
            int x = Integer.parseInt(userInput);
            return true;
        }catch(NumberFormatException e) {
            System.out.println("Input has to be an integer.");
            return false;

        }
    }


    public void whichPostReplyTo() {
        /* Method that prints all posts within a folder and asks a
        user which post it wants to reply to and sets OriginalPostID */
        System.out.print("First, which folder you you want to reply to? ");
        this.folderID = this.setFolderID(connection);
        this.postIDs = new ArrayList<>();
        try {
            // Finds possible posts in the database for given FolderID
            String SQLQuery = "SELECT * FROM Post WHERE FolderID = '" + this.folderID + "';";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQLQuery);
            System.out.println("Possible posts");

            // Prints the possible posts with information to the screen
            while (resultSet.next()) {
                int dataBasePostID = resultSet.getInt("PostID");
                String dataBaseAuthor = resultSet.getString("Author");
                String dataBasePostText = resultSet.getString("PostText");
                String dataBaseColorCode = resultSet.getString("ColorCode");

                System.out.print("PostID: " + dataBasePostID + "\n " + "Author: " + dataBaseAuthor + "\n " +
                        "PostText: " + dataBasePostText + "\n " + "ColorCode: " + dataBaseColorCode + "\n");

                // Adding possible postID to a list of possible postIDs
                this.postIDs.add(dataBasePostID);
            }
            System.out.println("Enter PostID for post you wish to reply to: ");
            //this.originalPostID = scanner.nextInt();
            boolean postIDIsValid = false;
            String trialPostID;
            boolean inputIsInteger = false;
            while (!inputIsInteger) {
                postIDIsValid = false;
                System.out.println("Valid postIDs: " + Arrays.toString(postIDs.toArray()));
                trialPostID = scanner.nextLine();
                if (isInputInteger(trialPostID)) {
                    inputIsInteger = true;
                    int intTrialPostID = Integer.parseInt(trialPostID);
                    while (!postIDIsValid) {
                        postIDIsValid = isPostIDValid(intTrialPostID);
                        if (postIDIsValid) {
                            this.originalPostID = intTrialPostID;
                            postIDIsValid = true;
                        } else {
                            System.out.println("Invalid postID.");
                            postIDIsValid = true;
                            inputIsInteger = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to get postIDID for posts: " + e);
        }
    }

    public void addReplyText(){
        /* Method to ask user for post text and return this as postText*/
        System.out.println("Write post text: ");
        this.postText = scanner.nextLine();
    }

    public void addReplyToDatabase(Connection connection){
        /* Method that adds reply to database */
            try{
                String SQLQuery1 = "INSERT INTO Post VALUES ('" + this.postID + "', '" + this.email + "', '" + this.postText +"', '" + this.colorCode + "', '" + this.folderID + "', '" + this.originalPostID + "');";
                Statement statement1 = connection.createStatement();
                statement1.execute(SQLQuery1);

                System.out.println("Reply was successfully added!");
            }
            catch(Exception e){
                System.out.println("Error when inserting values into replyToPost" + e);
            }
    }
}
import java.util.*;
import java.sql.*;

public class CreatePost extends DataBaseConnection{
    /* Class for creating a post in the Piazza database */

    private int postID;
    private String author;
    private String courseCode;
    private String term;

    private String postText;
    private String colorCode;

    private String folderName;
    private int folderID;

    private int originalPostID;
    private String tag;
    private List<String> tagsInDatabase;
    private List<String> tagsTaggedToPost;

    Scanner inputFromUser;

    public CreatePost(String email, String courseCode, String term, Scanner scanner){
        /* Constructor for Post object */
        // Connecting with the database
        connect();

        this.author = email;
        this.courseCode = courseCode;
        this.term = term;
        this.inputFromUser = scanner;

    }

    public void createPost(){
        generatePostID(this.connection);
        addPostText();
        setColorCode(this.connection);
        setFolderID(this.connection);
        addPostToDatabase(this.connection);
        tagPost(this.connection);
    }

    public int generatePostID(Connection connection){
        /* Function that generates an ID for the created post and saves it as postID.
        * Sets postID to 0 if en exception occurs*/
        try{
            // Finding the largest post ID used
            String queryFindPostIDs = "SELECT MAX(PostID) AS MaxPostID FROM Post;";
            Statement statement = connection.createStatement();
            ResultSet largestPostsID = statement.executeQuery(queryFindPostIDs);

            /*Stores the largest postID increased by one as the postID for the post created ensuring
            * no posts have the same postID even if a post is deleted*/
            while (largestPostsID.next()){
                this.postID = largestPostsID.getInt("MaxPostID") + 1 ;
                System.out.println("Post ID for post is " + this.postID);
            }
        }
        catch (Exception e){
            // Catching error when SQLQuery fails and prints error message
            System.out.println("Database error during selecting largest post ID: " + e);
            this.postID = 0;
        }
        // This is the first post in a thread, it points to itself
        this.originalPostID = this.postID;
        return this.postID;
    }

    public void addPostText(){
        /* Method to ask user for post text and return this as postText*/
        System.out.println("Write post text: ");
        this.postText = inputFromUser.nextLine();

        // Handles possible empty postText
        if(postText.equals("")){
            System.out.println("Write post text: ");
            this.postText = inputFromUser.nextLine();
        }
    }

    public void setColorCode(Connection connection){
        /* Method to set color code of a post based on user type. Color code is "blue" if
        user is a student, "red" if user is an instructor and null if the method somehow fails. */

        try{
            // Find user type
            String queryGetUserType = "SELECT UserType FROM ParticipatesInCourse WHERE (Email = '"+author+"' AND CourseCode = '"+courseCode+"' AND Term = '"+term+"');";
            Statement statement = connection.createStatement();
            ResultSet userTypeFromDatabase = statement.executeQuery(queryGetUserType);
            // Set color code
            while (userTypeFromDatabase.next()) {
                String userType = userTypeFromDatabase.getString("UserType");
                // User is of type student
                if (userType.equalsIgnoreCase("Student")){
                    this.colorCode = "blue";
                }
                // User is of type instructor
                else if (userType.equalsIgnoreCase("Instructor")){
                    this.colorCode = "red";
                }
                // If user type is anything other than Student or Instructor print error message
                else{
                    System.out.println("Unknown user type for "+this.author+".");
                    this.colorCode = null;
                }
            }
        }
        catch(Exception e){
            System.out.println(e + "Failed to set color code");
            this.colorCode = null;
        }
        System.out.println("Color code set to " + this.colorCode);
    }

    public int setFolderID(Connection connection){
        /* Method to find and set ID of folder from the name of the folder.
        * Sets folder ID to 0 if an exception occurs. */
        try{
            // Finding possible folder names within a course and term
            String queryFindPossibleFolderNames = "SELECT FolderName from Folder WHERE (CourseCode = '"+courseCode+"' AND Term = '"+term+"');";
            PreparedStatement statement = connection.prepareStatement(queryFindPossibleFolderNames);
            ResultSet possibleFolderNames = statement.executeQuery();

            // Creating a list of the possible folder names
            List<String> folderNameInDatabase = new ArrayList<>();
            while (possibleFolderNames.next()){
                folderNameInDatabase.add(possibleFolderNames.getString("FolderName"));
            }
            boolean folderExists = false;

            // Keep asking for user input until input is valid
            while(!folderExists){
                System.out.println("Choose one of these folders: " + folderNameInDatabase.toString().replace("[","").replace("]",""));
                this.folderName = inputFromUser.nextLine();

                // Check if folder name from user input matches a folder in the right course and term
                for (int i = 0; i<folderNameInDatabase.size(); i++){
                    if (folderName.equalsIgnoreCase(folderNameInDatabase.get(i))){
                        folderExists = true;
                        try{
                            // Finding the folder ID of the folder with name equal to the user input folderName
                            String queryFindFolderID = "SELECT FolderID from Folder WHERE (FolderName = '"+folderName+"' AND CourseCode = '"+courseCode+"' AND Term = '"+term+"');";
                            PreparedStatement statement2 = connection.prepareStatement(queryFindFolderID);
                            ResultSet folderIDFromFolderName = statement2.executeQuery();
                            while (folderIDFromFolderName.next()) {
                                this.folderID = folderIDFromFolderName.getInt("FolderID");
                            }
                        }
                        catch (Exception e){
                            System.out.println(e + "Failed to retrieve folderID.");
                            this.folderID = 0;
                        }
                    }
                }
                if (!folderExists){
                    System.out.println("The folder "+this.folderName+" does not exist in the course "+courseCode+" for term "+term);
                }
            }
            System.out.println("Folder ID for " + this.folderName + " is " + this.folderID);
        }
        catch (Exception e){
            System.out.println("Failed to retrieve folder names from database" + e);
            this.folderID = 0;
        }
        return this.folderID;
    }

    public void getValidTags(Connection connection) {
        // Finding possible post tags from database
        try{
            String queryFindPossiblePostTags = "SELECT * from Tags;";
            Statement statement = connection.createStatement();
            ResultSet possibleTags = statement.executeQuery(queryFindPossiblePostTags);

            // Create list of possible tags
            this.tagsInDatabase = new ArrayList<>();
            while (possibleTags.next()) {
                this.tagsInDatabase.add(possibleTags.getString("TagName"));
            }
        }
        catch(Exception e){
            System.out.println("Failed to retrieve tags from database" + e);
            this.tag = null;
        }

    }


    public boolean isTagValid(String tag){
        System.out.println("Tag: " + tag);
        // Handles a post's first tag
        if ((this.tagsInDatabase.contains(tag)) && (this.tagsTaggedToPost.isEmpty())){
            return true;
        }
        // Handles problem with post being tagged with same tag
        else if ((this.tagsInDatabase.contains(tag)) && (!this.tagsTaggedToPost.contains(tag))){
            return true;
        }
        else{
            return false;
        }
    }

    public void saveTagToDataBase(Connection connection, String tag){
        // Connect tag with post if user wanted to add a tag
        try{
            // Inserting an element into the PostTags table connecting tag to the post
            String queryTagPost = "INSERT INTO PostTags VALUES('"+ this.postID +"','"+ tag +"')";
            PreparedStatement tagPost = connection.prepareStatement(queryTagPost);
            tagPost.executeUpdate();
            System.out.println("Post successfully tagged with " + tag);
        }
        catch (Exception e){
            System.out.println("Failed to insert postID and tag to PostTags" + e);
        }
    }

    public void tagPost(Connection connection){
        /* Method to tag the post by inserting into the PostTags table if the user wishes to do so.
        * Set tag to null user do not wish to add tag or if an exception occurs.*/

        // Getting valid tags from the database
        this.getValidTags(connection);

        this.tagsTaggedToPost = new ArrayList<>();

        System.out.println("Do you wish to add a tag? (y/n)");
        String wishToAddTag = inputFromUser.nextLine();

        // User wants to add tag
        while (wishToAddTag.equals("y")){
            System.out.println("Enter one of these tags: " + tagsInDatabase.toString().replace("[","").replace("]",""));
            this.tag = inputFromUser.nextLine();

            // Check if tag is valid
             boolean tagIsValid = this.isTagValid(this.tag);
             while(!tagIsValid){
                 System.out.println("Invalid input. Try again. Enter one of these tags: " + tagsInDatabase.toString().replace("[","").replace("]",""));
                 this.tag = inputFromUser.nextLine();
                 tagIsValid = this.isTagValid(this.tag);
             }
             // Saves the tag to the post in the database
             this.saveTagToDataBase(connection, this.tag);
             this.tagsTaggedToPost.add(this.tag);

             // Ask the user if more tags are wanted
            System.out.println("Do you wish to add another tag? (y/n)");
            wishToAddTag = inputFromUser.nextLine();
        }
    }

    public void addPostToDatabase(Connection connection){
        try{
            // Adding the post into the table Post
            String queryAddPost = "INSERT INTO Post VALUES ('"+ postID +"','"+ author +"','"+ postText + "','"+ colorCode + "','"+ folderID +"','"+ originalPostID +"');";
            Statement addPostToDatabase = connection.createStatement();
            addPostToDatabase.execute(queryAddPost);
            System.out.println("Post successfully added!");
        }
        catch (Exception e){
            System.out.println("Failed to insert post to database" + e);
        }
    }
}

import java.sql.*;
import java.util.*;

public class SearchPost extends DataBaseConnection {
    /* Class for SearchPost */
    private String keyword;
    private List <Integer> postIdListFromDatabase = new ArrayList<>();
    Scanner inputWordFromUser;

    public SearchPost(Scanner scanner) {
        /* Constructor for SearchPost object that ask user
        to enter keyword to search for. */

        this.inputWordFromUser = scanner;

        // Connecting with the database
        connect();

        // Input starts, first some informative text to user
        System.out.println("--- SEARCH POST ---");
        System.out.println("You can search for posts with a specific keyword.");
        System.out.println("The return value is a list of ids of posts matching the keyword. ");

        // Asks the user to enter keyword and stores the input in the SearchPost object
        System.out.println("Enter keyword: ");
        this.keyword = inputWordFromUser.nextLine();
    }

    public void getDataFromDatabase(Connection connection){

        try {
            // Getting data from the database that corresponds to SearchPost
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT PostID FROM Post WHERE PostText LIKE '%" + keyword +"%';");

            // Saving data from database in the SearchPost object
            while (resultSet.next()){
                this.postIdListFromDatabase.add(resultSet.getInt("PostID"));
            }
        } catch (Exception e) {
            // Catching error when try fails and prints error message
            System.out.println("Database error during selecting PostID=" + e);
            }
    }

    public void printPostID(){
        /* Method that prints out postID from posts
        where keyword if found. */
        // Prints postIDs if postIdListFromDatabase not empty
        if (postIdListFromDatabase!= null && !postIdListFromDatabase.isEmpty()){
            System.out.println("Here are postIDs of the posts containing the keyword: " +keyword+"");
            System.out.println(postIdListFromDatabase.toString().replace("[","").replace("]",""));
        }
        else {
            // Prints message if postIdListFromDatabase is empty
            System.out.println("No posts containing keyword: " +keyword+ "");

        }
    }
}
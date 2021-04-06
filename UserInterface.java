import java.util.*;

public class UserInterface extends DataBaseConnection{

    private String term;
    private String email;
    private String userType;
    private String courseCode;

    Scanner scanner;

    public UserInterface(Scanner scanner) {
        /* Constructor for UserInterface that welcomes user to Piazza
         and asks user about which UseCase he/she wants to select
         */
        this.scanner = scanner;
        System.out.println("---  WELCOME TO PIAZZA  --- \n" +
                "Before using Piazza you have to log in and enroll in a course.");

        // Calling the LogIn class to check login and get user to enroll in course
        this.logIn();
    }

    public void logIn() {
        // Creating a logIn object
        LogIn testUserLogIn = new LogIn(this.scanner);
        testUserLogIn.logIn();
        testUserLogIn.chooseCourse();

        // Saves data from user that login
        this.courseCode = testUserLogIn.getCourseCode();
        this.term = testUserLogIn.getTerm();
        this.userType = testUserLogIn.getUserType();
        this.email = testUserLogIn.getUserEmail();

        // Calling the menu method
        this.menu();
    }

    public void menu() {
        // Printing out the menu with all possible options
        System.out.println("Choose action \n" +
                "1  -   Make a post \n" +
                "2  -   Reply to a post \n" +
                "3  -   Search for post with a specific keyword \n" +
                "4  -   View statistics for users and how many posts they have read/created \n" +
                "5  -   Log out \n" +
                "6  -   Exit \n");

        boolean menuChoiceIsValid = false;
        String validInput = "123456";
        String menuChoice = "0";

        // Asking for input as lang as invalid input is entered
        while (!menuChoiceIsValid){
            // Getting input from the user
            menuChoice = scanner.nextLine();

            // Not valid input
            if (!validInput.contains(menuChoice)){
                System.out.println("Invalid input. You must enter a number from 1 to 6. \n Try again: ");
            }
            else{
                // Valid input
                menuChoiceIsValid = true;
            }
        }
        System.out.println("You chose: " + menuChoice);

        switch (menuChoice) {
            case "1":
                // CreatePost
                this.createPost();
                break;
            case "2":
                // ReplyToPost
                this.replyToPost();
                break;
            case "3":
                // SearchForKeywordInPost
                this.searchForKeywordInPost();
                break;
            case "4":
                // ViewStatistic
                this.viewStatistics();
                break;
            case "5":
                // LogOut
                this.logOut();
                break;
            case "6":
                // Exit system
                this.exitSystem();
        }
    }

    public void createPost() {
        // Creating a CreatePost object and calls the constructor
        CreatePost testUserCreatePost = new CreatePost(this.email, this.courseCode, this.term, this.scanner);
        testUserCreatePost.createPost();
        // Go back to menu
        this.menu();
    }

    public void replyToPost() {
        // Creating a ReplyToPost object and calls the constructor
        ReplyToPost testUserReplyToPost = new ReplyToPost(this.email, this.courseCode, this.term, this.scanner);
        testUserReplyToPost.replyToPost();
        // Go back to menu
        this.menu();
    }

    public void searchForKeywordInPost() {
        // Creating a SearchPost object
        SearchPost testUserSearchPost = new SearchPost(this.scanner);
        testUserSearchPost.getDataFromDatabase(testUserSearchPost.connection);
        testUserSearchPost.printPostID();
        // Go back to menu
        this.menu();
    }

    public void viewStatistics() {
        if (this.userType.equalsIgnoreCase("instructor")){
            Statistics statistics = new Statistics();
            statistics.getDataFromDatabase(statistics.connection);
            System.out.println(userType);
        }
        else{
            System.out.println("You have to be an instructor to have access to statistics.");
        }
        // Go back to menu
        this.menu();
    }

    public void logOut() {
        // Deleting stored values from user
        this.term = null;
        this.email = null;
        this.userType = null;
        this.courseCode = null;
        System.out.print("You are now logged out of piazza! \n---  MENU OPTIONS  ---\nEnter one of the following actions: \n");
        System.out.println("  * 'exit'     if you want to quit\n" +
                "  * 'login'    if you want to log in on piazza ");

        String userInput = "empty";
        boolean userInputIsValid = false;

        // Asking for input until valid input is entered
        while (!userInputIsValid) {
            userInput = scanner.nextLine();
            if (userInput.equals("exit") || userInput.equals("login")){
                userInputIsValid = true;
            }
            else{
                System.out.println("Invalid input. You must enter: 'exit' og 'login'. Try again: ");
            }
        }
        // Handles the two different input and their action
        if (userInput.equals("exit")) {
            System.out.println("You entered exit");
            this.exitSystem();
        }
        else{
            System.out.print("You entered 'login'\n");
            this.logIn();
        }
    }

    public void exitSystem(){
        /* Exits system and closes connection to database */
        System.out.println("Exiting piazza ...");
        //connection.closeConnection();
        try{
            if (this.connection!=null){
                    this.connection.close();
                    System.out.println("Database connection closed.");
            }
        } catch(Exception e){
            System.out.println("Failed to close connection to database= " + e);
        }
    }


}
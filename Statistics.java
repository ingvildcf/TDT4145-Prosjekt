import java.sql.*;
import java.util.*;
public class Statistics extends DataBaseConnection{
    /* Class for Statistics */

    public Statistics(){
    /* Constructor for Statistics that gets connection
    and prints out info to user
     */

        // Connecting with the database
        connect();

        // Printing information to user
        System.out.println("--- STATISTICS --- \n" +
                "Statistics will describe how many numbers of posts \n" +
                "each user has read and how many numbers of posts \n" +
                "each user has created. The output is sorted on \n" +
                "highest read posting numbers. \n ");
    }

    // Function for getting data from database and printing out results
    public void getDataFromDatabase(Connection connection){
        String email;
        Integer NBOPR; // NumberOfPostsRead
        Integer NBOPC; // NumberOfPostsCreated

        /* SQL Query which gives us a table with rows Email, NumbersOfPostsRead
         and NumberOfPostsCreated, sorting on NumbersOfPostsRead
         */
        String SQLQueryToGetStatistics = "SELECT email_NBOPR_Table.email, email_NBOPR_Table.NumbersOfPostsRead, email_NBOPC_Table.NumberOfPostsCreated FROM \n" +
                "(SELECT User.Email, COUNT(PostID) AS NumbersOfPostsRead \n" +
                "    FROM User \n" +
                "    LEFT OUTER JOIN ReadPost ON (User.Email=ReadPost.Email) \n" +
                "    GROUP BY Email) AS email_NBOPR_Table\n" +
                "JOIN \n" +
                "(SELECT User.Email, COUNT(PostID) AS NumberOfPostsCreated \n" +
                "\tFROM User\n" +
                "    LEFT OUTER JOIN Post ON (User.Email=Post.Author)\n" +
                "\tGROUP BY Email) AS email_NBOPC_Table\n" +
                "ON email_NBOPR_Table.email=email_NBOPC_Table.email\n" +
                "ORDER BY NumbersOfPostsRead DESC;";
        try {
            Statement getStatistics = connection.createStatement();
            ResultSet resultSetStatistics = getStatistics.executeQuery(SQLQueryToGetStatistics);

            // Output to user, describing each column. Made beautiful with string.format()
            System.out.println(String.format("%-25s %-25s %s", "Email", "NumbersOfPostsRead", "NumbersOfPostsCreated"));

            // Saving data from database in the Statistics object and printing the results
            while (resultSetStatistics.next()){
                email = resultSetStatistics.getString(1);
                NBOPR = resultSetStatistics.getInt(2);
                NBOPC = resultSetStatistics.getInt(3);

                // Printing the values from the database . Made beautiful with string.format()
                System.out.println(String.format("%-25s %-25s %s", email, NBOPR, NBOPC));
            }
        }
        catch (Exception e) {
            // Catching error when try fails and prints error message
            System.out.println("Database error during statistics=" + e);
        }
    }
}
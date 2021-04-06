import java.sql.*;
import java.util.Properties;

public abstract class DataBaseConnection {
    protected Connection connection;

    public DataBaseConnection () {
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Properties for user and password.
            Properties p = new Properties();
            p.put("user", "fs_tdt4145_1_gruppe300");
            p.put("password", "LoveLace");

            // Establishing a connection
            connection = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no/fs_tdt4145_1_gruppe300_piazza?autoReconnect=true&useSSL=false",p);        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to connect", e);
        }
    }
}

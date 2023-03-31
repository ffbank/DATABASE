import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Connector {
    public static Connection connector(String ip, String username, String password){
        String csvFilePath = "uploads.csv";
        String jdbcURL = "jdbc:mysql:// " + ip + "/mydatabase";
        try {
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connection successfull");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

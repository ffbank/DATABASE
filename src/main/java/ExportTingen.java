import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class ExportTingen {

    public static void main(String[] args) {
        String csvFilePath = "uploads.csv";
        String jdbcURL = "jdbc:mysql://localhost:3306/mydatabase";
        String username = "myusername";
        String password = "mypassword";

        try {
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            String line;
            String[] fields;
            BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
            while ((line = br.readLine()) != null) {
                fields = line.split(",");
                String query = "INSERT INTO mytable (field1, field2, field3) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                statement.executeUpdate();
            }
            br.close();
            connection.close();
            System.out.println("CSV file imported to MySQL database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

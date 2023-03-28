import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Data1 {
    public static void main(String[] args) {
        // Update the connection string, username, and password as needed
        String connectionString = "jdbc:mysql://localhost:3306/TvNewsDB?useSSL=false";
        String username = "root";
        String password = "vores kodeord skal stå her";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(connectionString, username, password);

            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM Journalists";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String cprNumber = resultSet.getString("cpr_number");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String streetName = resultSet.getString("street_name");
                int civicNumber = resultSet.getInt("civic_number");
                String city = resultSet.getString("city");
                String zipCode = resultSet.getString("zip_code");
                String country = resultSet.getString("country");
                String phoneNumber = resultSet.getString("phone_number");
                String emailAddress = resultSet.getString("email_address");

                System.out.printf("CPR Number: %s, First Name: %s, Last Name: %s, Street Name: %s, Civic Number: %d, City: %s, Zip Code: %s, Country: %s, Phone Number: %s, Email Address: %s%n",
                        cprNumber, firstName, lastName, streetName, civicNumber, city, zipCode, country, phoneNumber, emailAddress);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("hejsa:");
            e.printStackTrace();
        }
    }
}
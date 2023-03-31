import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Date;

public class Menu {
    public static void main(String[] args) throws IOException, SQLException, ParseException {
        int counter = 0;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please type in the ip and port of the database you wish to connect to int the format of IP:PORT");
        String ip = scan.nextLine();
        System.out.println("Please type in your database username: ");
        String username = scan.nextLine();
        System.out.println("Please type in your database password: ");
        String password = scan.nextLine();
        Connection con = Connector.connector(ip, username, password);
        System.out.println("Reading data, please wait");
        List<FootageAndReporter> far = Reader(scan, counter);
        System.out.println(counter +" Lines have been read");
        System.out.println("Splitting list");
        List<Footage> foot = splitter(far);
        List<Reporter> rep = splitter2(far);
        System.out.println("Sending output to sql server");
        sender(con, foot, rep);
    }
    public static List<FootageAndReporter> Reader(Scanner scan, int counter) throws IOException, ParseException {
        List<FootageAndReporter> ListOfFootagesAndReporters = new ArrayList<FootageAndReporter>();
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd");
        System.out.println("please type in the file path for the file you wish to read");
        String filename = scan.nextLine();
        String line;
        String[] fields = new String[10];
        BufferedReader br = new BufferedReader(new FileReader(filename));
        while ((line = br.readLine()) != null){
            fields = line.split(";");
            String title = fields[0];
            java.util.Date parseddate = dateParser.parse(fields[1]);
            java.sql.Date date = new java.sql.Date(parseddate.getTime());
            Integer duration = Integer.parseInt(fields[2]);
            Integer cpr = Integer.parseInt(fields[3]);
            String firstName = fields[4];
            String lastName = fields[5];
            String streetName = fields[6];
            Integer civicNumber = Integer.parseInt(fields[7]);
            Integer zipCode = Integer.parseInt(fields[8]);
            String country = fields[9];
            FootageAndReporter temp = new FootageAndReporter(title, date, duration, cpr, firstName, lastName, streetName, civicNumber, zipCode, country);
            ListOfFootagesAndReporters.add(temp);
            counter++;
        }
        return ListOfFootagesAndReporters;
    }
    public static void sender(Connection con, List<Footage> foot, List<Reporter> rep) throws SQLException {
        String notexists = "The following names did not exist in the database and were automatically added: ";
        for(int i = 0; i < rep.size(); i++){
            String CheckifExists = "SELECT cpr_number FROM Journalists WHERE cpr_number = ?";
            PreparedStatement ExistCheck = con.prepareStatement(CheckifExists);
            ExistCheck.setString(1, Integer.toString(rep.get(i).getCPR()));
            ResultSet r = ExistCheck.executeQuery();
            if(r.next()){
                //
            }else{
                notexists += rep.get(i).getFirstName();
                String query = "INSERT INTO Journalists (cpr_number, first_name, last_name, street_name, civic_number, city, zip_code, country, phone_number, email_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, Integer.toString(rep.get(i).getCPR()));
                stmt.setString(2, rep.get(i).getFirstName());
                stmt.setString(3, rep.get(i).getLastName());
                stmt.setString(4, rep.get(i).getStreetName());
                stmt.setInt(5, rep.get(i).getCivicNumber());
                stmt.setString(6, rep.get(i).getCountry());
                stmt.setString(7, Integer.toString(rep.get(i).getZIPCode()));
                stmt.setString(8, "Denmark");
                stmt.setString(9, "Undefined");
                stmt.setString(10, "Undefined");
                int rows = stmt.executeUpdate();
                System.out.println(rows + " Rows inserted for reporters");
            }
        }
        for (int i = 0; i < foot.size(); i++){
            String sql = "INSERT INTO Footages (title, shot_date, duration_seconds, reporter_journalist_cpr_number) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);

            statement.setString(1, foot.get(i).getTitle());
            statement.setDate(2, foot.get(i).getDate());
            statement.setString(3, Integer.toString(foot.get(i).getDuration()));
            statement.setString(4, Integer.toString(rep.get(i).getCPR()));

            int rows = statement.executeUpdate();
            System.out.println(rows + " Rows inserted for footages");
        }

        System.out.println(notexists);
    }
    public static List<Footage> splitter(List<FootageAndReporter> list){
        List<Footage> split = new ArrayList<Footage>();
        for (int i = 0; i < list.size(); i++){
            split.add(list.get(i).getFootage());
        }
        return split;
    }

    public static List<Reporter> splitter2(List<FootageAndReporter> list){
        List<Reporter> split = new ArrayList<Reporter>();
        for (int i = 0; i < list.size(); i++){
            split.add(list.get(i).getReporter());
        }
        return split;
    }
}

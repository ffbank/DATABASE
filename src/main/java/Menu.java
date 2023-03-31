import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
public class Menu {
    public static void main(String[] args) throws IOException {
        int counter = 0;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please type in the ip and port of the database you wish to connect to");
        String ip = scan.nextLine();
        System.out.println("Please type in your database username: ");
        String username = scan.nextLine();
        System.out.println("Please type in your database password: ");
        String password = scan.nextLine();
        Connection con = Connector.connector(ip, username, password);
        System.out.println("Reading data, please wait");
        Reader(scan, counter);
        System.out.println(counter +" Lines have been read");

    }
    public static List<FootageAndReporter> Reader(Scanner scan, int counter) throws IOException {
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
            Date date = null;
            try {
                date = dateParser.parse(fields[1]);
            } catch (ParseException e) {
                throw new NumberFormatException(
                        "Invalid value (" + fields[0] + ") ");
            }
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
}

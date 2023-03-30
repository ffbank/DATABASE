import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class FootagesAndReportersLoader {

	public static final String SEMICOLON_DELIMITER = ";";
	public static final String COMMA_DELIMITER = ",";
	private static final int NUMBER_OF_FIELDS_EXPECTED = 10;
	private final String delimiter = SEMICOLON_DELIMITER;
	SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMdd");

	private final String jdbcURL;
	private final String username;
	private final String password;

	public FootagesAndReportersLoader(String jdbcURL, String username, String password) {
		this.jdbcURL = jdbcURL;
		this.username = username;
		this.password = password;
	}

	public List<FootageAndReporter> loadFootagesAndReporters(String filename) throws FileNotFoundException, IOException, ParseException {
		List<FootageAndReporter> farList = new ArrayList<FootageAndReporter>();

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));

			String line;
			int lineNbr = 0;
			while ((line = in.readLine()) != null) {
				lineNbr++;
				List<String> values = new ArrayList<String>();
				try (Scanner rowScanner = new Scanner(line)) {
					rowScanner.useDelimiter(delimiter);
					while (rowScanner.hasNext()) {
						values.add(rowScanner.next());
					}
					if (values.size() == 0)
						continue;
					if (values.size() == NUMBER_OF_FIELDS_EXPECTED) {
						String title = values.get(0);
						Date date = null;
						try {
							date = dateParser.parse(values.get(1));
						} catch (ParseException e) {
							throw new NumberFormatException(
									"Invalid value (" + values.get(1) + ") for date at line " + lineNbr);
						}
						Integer duration = Integer.valueOf(values.get(2));
						Integer cpr = Integer.valueOf(values.get(3));
						String firstName = values.get(4);
						String lastName = values.get(5);
						String streetName = values.get(6);
						Integer civicNumber = Integer.valueOf(values.get(7));
						Integer zipCode = Integer.valueOf(values.get(8));
						String country = values.get(9);
						FootageAndReporter far = new FootageAndReporter(title, date, duration, cpr, firstName, lastName,
								streetName, civicNumber, zipCode, country);
						farList.add(far);
					} else
						throw new IOException("Invalid number of values on line " + lineNbr + ". expected "
								+ NUMBER_OF_FIELDS_EXPECTED + " values, found " + values.size());
				}
			}
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e) {
					/* Ignore */
				}
			;
		}

		return farList;
	}

	public void exportToMySQL(List<FootageAndReporter> footagesAndReporters) throws SQLException {
		Connection connection = null;
		PreparedStatement footageStatement = null;
		PreparedStatement reporterStatement = null;
		String footageQuery = "INSERT INTO footage (title, date, duration) VALUES (?, ?, ?)";
		String reporterQuery = "INSERT INTO reporter (cpr, first_name, last_name, street_name, civic_number, zip_code, country) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try {
			connection = DriverManager.getConnection(jdbcURL, username, password);
			connection.setAutoCommit(false);

			footageStatement = connection.prepareStatement(footageQuery);
			reporterStatement = connection.prepareStatement(reporterQuery);

			for (FootageAndReporter far : footagesAndReporters) {
				Footage footage = far.getFootage();
				Reporter reporter = far.getReporter();

				footageStatement.setString(1, footage.getTitle());
				footageStatement.setDate(2, new java.sql.Date(footage.getDate().getTime()));
				footageStatement.setInt(3, footage.getDuration());
				footageStatement.executeUpdate();

				reporterStatement.setInt(1, reporter.getCPR());
				reporterStatement.setString(2, reporter.getFirstName());
				reporterStatement.setString(3, reporter.getLastName());
				reporterStatement.setString(4, reporter.getStreetName());
				reporterStatement.setInt(5, reporter.getCivicNumber());
				reporterStatement.setInt(6, reporter.getZIPCode());
				reporterStatement.setString(7, reporter.getCountry());
				reporterStatement.executeUpdate();
			}

			connection.commit();
			System.out.println("Data exported to MySQL database.");
		} catch (SQLException e) {
			if (connection != null)
				connection.rollback();
			throw e;
		} finally {
			if (footageStatement != null)
				footageStatement.close();
			if (reporterStatement != null)
				reporterStatement.close();
			if (connection != null)
				connection.close();
		}
	}
}


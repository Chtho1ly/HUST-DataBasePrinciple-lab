import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class test {
    // Connect to your database.
    // Replace server name, username, and password with your credentials
    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;" + "database=covid19mon;";
        String user = "sa";
        String pw = "1234";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(connectionUrl, user, pw);
            Statement statement = connection.createStatement();
            System.out.println(connection);
            String selectSql = "SELECT * FROM person";
            ResultSet resultSet = statement.executeQuery(selectSql);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
            }
        }
        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
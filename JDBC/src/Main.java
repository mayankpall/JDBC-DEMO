import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public class Main {
    public static void main(String[] args) {
        //root@localhost:3306
        //jdbc:mysql://localhost:3306/?user=root
        String url = "jdbc:mysql://localhost:3306/testingJDBC";
        String user = "root";
        String password = "Saksham@22";

        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection
            Connection conn = DriverManager.getConnection(url, user, password);

            // Create Statement
            Statement stmt = conn.createStatement();

            // Execute Query
            ResultSet rs = stmt.executeQuery("SELECT * FROM employees");

            // Process Results
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Salary: " + rs.getDouble("salary"));
            }

            // Close Resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

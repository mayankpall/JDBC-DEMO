import java.sql.*;

public class practice {
    public final static  String user = "root";
    public final static String url = "";
    public final static String password = "";

    public static void viewTable() throws SQLException {
        try(Connection connection = DriverManager.getConnection(url,user,password)) {
            String sql = "";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.getResultSet();

        }
    }



}

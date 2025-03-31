//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PayrollService {
//    private static final String URL = "jdbc:mysql://localhost:3306/payroll_service";
//    private static final String USER = "root";
//    private static final String PASSWORD = "Saksham@22";
//
//    public static List<EmployeePayroll> getEmployeePayrollData() throws PayrollException  {
//        List<EmployeePayroll> employeeList = new ArrayList<>();
//
//        try {
//
//
//            // Step 1: Establish connection
//            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
//            System.out.println("Connected to database.");
//
//            // Step 2: Create a Statement
//            Statement statement = connection.createStatement();
//
//            // Step 3: Execute Query
//            String query = "SELECT * FROM employee_payroll" ;
//            ResultSet resultSet = statement.executeQuery(query);
//
//
//            // Step 4: Process ResultSet
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//                String gender = resultSet.getString("gender");
//                double salary = resultSet.getDouble("salary");
//
//                employeeList.add(new EmployeePayroll(id, name, gender, salary));
//            }
//
//            // Step 5: Close Resources
//            resultSet.close();
//            statement.close();
//            connection.close();
//
//        } catch (SQLException e) {
//            throw new PayrollException("Error retrieving data: " + e.getMessage());
//        }
//        return employeeList;
//    }
//
//    public static void updateEmployeeSalaryUC3(String name, double newSalary) throws PayrollException {
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
//             Statement statement = connection.createStatement()) {
//
//            System.out.println("Connected to database.");
//
//            String updateQuery = "UPDATE employee_payroll SET salary = " + newSalary + " WHERE name = '" + name + "'";
//            int rowsAffected = statement.executeUpdate(updateQuery);
//
//            if (rowsAffected > 0) {
//                System.out.println("Salary updated successfully for " + name);
//            } else {
//                System.out.println("Employee " + name + " not found.");
//            }
//
//        } catch (SQLException e) {
//            throw new PayrollException("Error updating salary: " + e.getMessage());
//        }
//    }
//
//    public static void updateEmployeeSalary(String name, double newSalary) throws PayrollException {
//        String updateQuery = "UPDATE employee_payroll SET salary = ? WHERE name = ?";
//
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
//
//            System.out.println("Connected to database.");
//
//            // Set parameters in the query
//            preparedStatement.setDouble(1, newSalary);
//            preparedStatement.setString(2, name);
//
//            // Execute update
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            if (rowsAffected > 0) {
//                System.out.println("Salary updated successfully for " + name);
//            } else {
//                System.out.println("Employee " + name + " not found.");
//            }
//
//        } catch (SQLException e) {
//            throw new PayrollException("Error updating salary: " + e.getMessage());
//        }
//    }
//
//
//    public static void main(String[] args) throws PayrollException {
//        // Retrieve Employee Payroll Data (UC 2)
//        System.out.println("Fetching Employee Payroll Data...");
//        List<EmployeePayroll> employees = getEmployeePayrollData();
//        for (EmployeePayroll emp : employees) {
//            System.out.println(emp);
//        }
//
//        // Update Salary for Terisa (UC 3)
//        System.out.println("\nUpdating Salary for Terisa...");
//        updateEmployeeSalary("Terisa", 3000000.00);
//
//        // Fetch Updated Employee Payroll Data
//        System.out.println("\nFetching Updated Employee Payroll Data...");
//        employees = getEmployeePayrollData();
//        for (EmployeePayroll emp : employees) {
//            System.out.println(emp);
//        }
//    }
//}


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollService {
    private static final String URL = "jdbc:mysql://localhost:3306/payroll_service";
    private static final String USER = "root";
    private static final String PASSWORD = "Saksham@22";

    private static PayrollService instance; // Singleton Instance
    private Connection connection;
    private PreparedStatement updateSalaryStatement;
    private PreparedStatement getEmployeeByNameStatement;
    private PreparedStatement getEmployeesByDateRangeStatement;
    private PreparedStatement payrollStatsStatement;

    // Private Constructor (For Singleton)
    private PayrollService() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Database connection established.");

        // Caching Prepared Statements
        updateSalaryStatement = connection.prepareStatement("UPDATE employee_payroll SET salary = ? WHERE name = ?");
        getEmployeeByNameStatement = connection.prepareStatement("SELECT * FROM employee_payroll WHERE name = ?");

        getEmployeesByDateRangeStatement = connection.prepareStatement(
                "SELECT * FROM employee_payroll WHERE start_date BETWEEN ? AND ?");

        payrollStatsStatement = connection.prepareStatement(
                "SELECT gender, SUM(salary) AS total_salary, AVG(salary) AS avg_salary, " +
                        "MIN(salary) AS min_salary, MAX(salary) AS max_salary, COUNT(*) AS employee_count " +
                        "FROM employee_payroll GROUP BY gender");
    }

    // Singleton Instance Getter
    public static PayrollService getInstance() throws SQLException {
        if (instance == null) {
            instance = new PayrollService();
        }
        return instance;
    }

    // UC 2: Retrieve Employee Payroll Data
    public List<EmployeePayroll> getEmployeePayrollData() throws PayrollException {
        List<EmployeePayroll> employeeList = new ArrayList<>();
        String query = "SELECT * FROM employee_payroll";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                employeeList.add(mapResultSetToEmployee(resultSet));
            }

        } catch (SQLException e) {
            throw new PayrollException("Error retrieving data: " + e.getMessage());
        }
        return employeeList;
    }

    // UC 4: Update Employee Salary Using Cached PreparedStatement
    public void updateEmployeeSalary(String name, double newSalary) throws PayrollException {
        try {
            updateSalaryStatement.setDouble(1, newSalary);
            updateSalaryStatement.setString(2, name);
            int rowsAffected = updateSalaryStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Salary updated successfully for " + name);
            } else {
                System.out.println("Employee " + name + " not found.");
            }
        } catch (SQLException e) {
            throw new PayrollException("Error updating salary: " + e.getMessage());
        }
    }

    // UC 4.1: Retrieve Employee By Name (Using Cached PreparedStatement)
    public EmployeePayroll getEmployeeByName(String name) throws PayrollException {
        try {
            getEmployeeByNameStatement.setString(1, name);
            ResultSet resultSet = getEmployeeByNameStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToEmployee(resultSet);
            }
        } catch (SQLException e) {
            throw new PayrollException("Error retrieving employee: " + e.getMessage());
        }
        return null; // If employee not found
    }


    // Close Resources When Done
    public void close() throws SQLException {
        if (updateSalaryStatement != null) updateSalaryStatement.close();
        if (getEmployeeByNameStatement != null) getEmployeeByNameStatement.close();
        if (connection != null) connection.close();
        System.out.println("Database connection closed.");
    }

    public List<EmployeePayroll> getEmployeesByDateRange(String startDate, String endDate) throws PayrollException {
        List<EmployeePayroll> employeeList = new ArrayList<>();

        try {
            getEmployeesByDateRangeStatement.setDate(1, Date.valueOf(startDate));
            getEmployeesByDateRangeStatement.setDate(2, Date.valueOf(endDate));

            ResultSet resultSet = getEmployeesByDateRangeStatement.executeQuery();

            while (resultSet.next()) {
                employeeList.add(mapResultSetToEmployee(resultSet));
            }

        } catch (SQLException e) {
            throw new PayrollException("Error retrieving employees by date range: " + e.getMessage());
        }
        return employeeList;
    }

    // Helper Method to Map ResultSet to EmployeePayroll Object
    private EmployeePayroll mapResultSetToEmployee(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String gender = resultSet.getString("gender");
        double salary = resultSet.getDouble("salary");
        Date startDate = resultSet.getDate("start_date");

        return new EmployeePayroll(id, name, gender, salary, startDate);
    }

    public void getPayrollStatistics() throws PayrollException {
        try (ResultSet resultSet = payrollStatsStatement.executeQuery()) {
            System.out.println("\nPayroll Statistics by Gender:");
            System.out.printf("%-10s %-12s %-12s %-10s %-10s %-15s%n", "Gender", "Total Salary", "Avg Salary", "Min Salary", "Max Salary", "Employee Count");
            System.out.println("--------------------------------------------------------------------------");

            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                double totalSalary = resultSet.getDouble("total_salary");
                double avgSalary = resultSet.getDouble("avg_salary");
                double minSalary = resultSet.getDouble("min_salary");
                double maxSalary = resultSet.getDouble("max_salary");
                int employeeCount = resultSet.getInt("employee_count");

                System.out.printf("%-10s %-12.2f %-12.2f %-10.2f %-10.2f %-15d%n",
                        gender, totalSalary, avgSalary, minSalary, maxSalary, employeeCount);
            }
        } catch (SQLException e) {
            throw new PayrollException("Error retrieving payroll statistics: " + e.getMessage());
        }
    }


    // Main Method
    public static void main(String[] args) {
        try {
            PayrollService payrollService = PayrollService.getInstance();

            // Retrieve Employee Payroll Data
            System.out.println("\nFetching Employee Payroll Data...");
            List<EmployeePayroll> employees = payrollService.getEmployeePayrollData();
            employees.forEach(System.out::println);

            // Update Salary for Terisa
            System.out.println("\nUpdating Salary for Terisa...");
            payrollService.updateEmployeeSalary("Terisa", 3000000.00);

            // Retrieve Updated Employee Payroll Data
            System.out.println("\nFetching Updated Employee Payroll Data...");
            EmployeePayroll updatedEmployee = payrollService.getEmployeeByName("Terisa");
            System.out.println(updatedEmployee);

            // Retrieve Employees Who Joined Between Two Dates
            System.out.println("\nFetching Employees Who Joined Between 2022-01-01 and 2023-12-31...");
            List<EmployeePayroll> employeess = payrollService.getEmployeesByDateRange("2022-01-01", "2023-12-31");

            // Print Results
            employeess.forEach(System.out::println);

            // Get Payroll Statistics

            payrollService.getPayrollStatistics();

            // Close connection
            payrollService.close();

        } catch (SQLException | PayrollException e) {
            e.printStackTrace();
        }
    }
}

import java.sql.Date;

public class EmployeePayroll {
    private int id;
    private String name;
    private String gender;
    private double salary;

    // Constructor
    public EmployeePayroll(int id, String name, String gender, double salary, Date startDate) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.salary = salary;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public double getSalary() { return salary; }

    @Override
    public String toString() {
        return "EmployeePayroll{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", salary=" + salary +
                '}';
    }
}

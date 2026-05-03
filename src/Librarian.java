public class Librarian extends User {
    private String employeeId;

    public Librarian(int userId, String name, String email, String employeeId) {
        super(userId, name, email);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }
}
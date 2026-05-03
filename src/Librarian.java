public class Librarian extends User {
    private String employeeId;

    public Librarian(int userId, String name, String email, String employeeId, String password) {
        super(userId, name, email, "LIBRARIAN", password);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }
}
public abstract class User {
    protected int userId;
    protected String name;
    protected String email;
    protected String role; // "ADMIN", "LIBRARIAN", "MEMBER"
    protected String password;

    public User(int userId, String name, String email, String role, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
}
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:inkvault.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    public static void initializeDatabase() {
        String createBooksTable = "CREATE TABLE IF NOT EXISTS books ("
                + "bookId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "author TEXT NOT NULL, "
                + "isbn TEXT UNIQUE NOT NULL, "
                + "genre TEXT NOT NULL, "
                + "totalCopies INTEGER NOT NULL DEFAULT 1, "
                + "availableCopies INTEGER NOT NULL DEFAULT 1"
                + ");";

        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "userId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "email TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL, "
                + "role TEXT NOT NULL CHECK(role IN ('ADMIN', 'LIBRARIAN', 'MEMBER')), "
                + "employeeId TEXT, "
                + "borrowedCount INTEGER DEFAULT 0"
                + ");";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                + "transactionId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "bookId INTEGER, "
                + "memberId INTEGER, "
                + "issueDate TEXT, "
                + "dueDate TEXT, "
                + "returnDate TEXT, "
                + "fineAmount REAL DEFAULT 0, "
                + "FOREIGN KEY(bookId) REFERENCES books(bookId), "
                + "FOREIGN KEY(memberId) REFERENCES users(userId)"
                + ");";

        String createSettingsTable = "CREATE TABLE IF NOT EXISTS settings ("
                + "settingKey TEXT PRIMARY KEY, "
                + "settingValue TEXT NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement()) {

            stmt.execute(createBooksTable);
            stmt.execute(createUsersTable);
            stmt.execute(createTransactionsTable);
            stmt.execute(createSettingsTable);

            // Insert default settings
            stmt.execute("INSERT OR IGNORE INTO settings VALUES ('finePerDay', '1.0')");
            stmt.execute("INSERT OR IGNORE INTO settings VALUES ('maxBorrowLimit', '3')");
            stmt.execute("INSERT OR IGNORE INTO settings VALUES ('loanPeriodDays', '14')");

            // Create default admin if no users exist
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            if (rs.next() && rs.getInt("count") == 0) {
                String hashedPassword = PasswordUtil.hashPassword("admin123");
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
                pstmt.setString(1, "Admin");
                pstmt.setString(2, "admin@inkvault.com");
                pstmt.setString(3, hashedPassword);
                pstmt.setString(4, "ADMIN");
                pstmt.executeUpdate();
            }

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("bookId"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("genre"),
                        rs.getInt("totalCopies"),
                        rs.getInt("availableCopies"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static String addBook(String title, String author, String isbn) {
        String query = "INSERT INTO books (title, author, isbn, genre, totalCopies, availableCopies) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
            pstmt.setString(4, "General"); // Default genre
            pstmt.setInt(5, 1); // Default totalCopies
            pstmt.setInt(6, 1); // Default availableCopies
            pstmt.executeUpdate();
            return "SUCCESS";

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return "ISBN already exists in the database.";
            }
            return "Error: " + e.getMessage();
        }
    }

    public static boolean deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE bookId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBookAvailability(int bookId, int availableCopies) {
        String query = "UPDATE books SET availableCopies = ? WHERE bookId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, availableCopies);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
            return false;
        }
    }

    public static List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'MEMBER'";

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Member member = new Member(
                        rs.getInt("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"));
                member.setBorrowedCount(rs.getInt("borrowedCount"));
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public static String addMember(String name, String email) {
        String query = "INSERT INTO users (name, email, password, role, borrowedCount) VALUES (?, ?, ?, 'MEMBER', 0)";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, PasswordUtil.hashPassword("password123")); // Hash default password
            pstmt.executeUpdate();
            return "SUCCESS";

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return "Email already exists in the database.";
            }
            return "Error: " + e.getMessage();
        }
    }

    public static boolean deleteMember(int userId) {
        String query = "DELETE FROM users WHERE userId = ? AND role = 'MEMBER'";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateMemberBorrowedCount(int userId, int borrowedCount) {
        String query = "UPDATE users SET borrowedCount = ? WHERE userId = ? AND role = 'MEMBER'";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, borrowedCount);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating member borrowed count: " + e.getMessage());
            return false;
        }
    }

    public static boolean addTransaction(int bookId, int memberId, String issueDate, String dueDate) {
        String query = "INSERT INTO transactions (bookId, memberId, issueDate, dueDate) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setString(3, issueDate);
            pstmt.setString(4, dueDate);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            return false;
        }
    }

    public static boolean returnBook(int transactionId, String returnDate) {
        String query = "UPDATE transactions SET returnDate = ? WHERE transactionId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, returnDate);
            pstmt.setInt(2, transactionId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateTransactionFine(int transactionId, double fine) {
        String query = "UPDATE transactions SET fineAmount = ? WHERE transactionId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, fine);
            pstmt.setInt(2, transactionId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating fine: " + e.getMessage());
            return false;
        }
    }

    public static List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE returnDate IS NULL";

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("transactionId"),
                        rs.getInt("bookId"),
                        rs.getInt("memberId"),
                        java.time.LocalDate.parse(rs.getString("issueDate")),
                        java.time.LocalDate.parse(rs.getString("dueDate")));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static Book getBookById(int bookId) {
        String query = "SELECT * FROM books WHERE bookId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Book book = new Book(
                        rs.getInt("bookId"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("genre"),
                        rs.getInt("totalCopies"),
                        rs.getInt("availableCopies"));
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Member getMemberById(int userId) {
        String query = "SELECT * FROM users WHERE userId = ? AND role = 'MEMBER'";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member(
                        rs.getInt("userId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"));
                member.setBorrowedCount(rs.getInt("borrowedCount"));
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Authentication
    public static User authenticateUser(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                // Verify password using BCrypt
                if (!PasswordUtil.verifyPassword(password, storedPassword)) {
                    return null;
                }
                
                String role = rs.getString("role");
                int userId = rs.getInt("userId");
                String name = rs.getString("name");

                if (role.equals("ADMIN")) {
                    return new Admin(userId, name, email, storedPassword);
                } else if (role.equals("LIBRARIAN")) {
                    String employeeId = rs.getString("employeeId");
                    return new Librarian(userId, name, email, employeeId != null ? employeeId : "", storedPassword);
                } else if (role.equals("MEMBER")) {
                    Member member = new Member(userId, name, email, storedPassword);
                    member.setBorrowedCount(rs.getInt("borrowedCount"));
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // User Management
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("userId");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");

                if (role.equals("ADMIN")) {
                    users.add(new Admin(userId, name, email, password));
                } else if (role.equals("LIBRARIAN")) {
                    String employeeId = rs.getString("employeeId");
                    users.add(new Librarian(userId, name, email, employeeId != null ? employeeId : "", password));
                } else if (role.equals("MEMBER")) {
                    Member member = new Member(userId, name, email, password);
                    member.setBorrowedCount(rs.getInt("borrowedCount"));
                    users.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static String addUser(String name, String email, String password, String role, String employeeId) {
        String query = "INSERT INTO users (name, email, password, role, employeeId, borrowedCount) VALUES (?, ?, ?, ?, ?, 0)";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, PasswordUtil.hashPassword(password)); // Hash password
            pstmt.setString(4, role);
            pstmt.setString(5, employeeId);
            pstmt.executeUpdate();
            return "SUCCESS";

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return "Email already exists in the database.";
            }
            return "Error: " + e.getMessage();
        }
    }

    public static boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBook(int bookId, String title, String author, String isbn, String genre,
            int totalCopies) {
        Book currentBook = getBookById(bookId);
        if (currentBook == null) {
            return false;
        }

        int borrowedCopies = currentBook.getTotalCopies() - currentBook.getAvailableCopies();

        int newAvailableCopies = totalCopies - borrowedCopies;

        if (newAvailableCopies < 0) {
            newAvailableCopies = 0;
        }

        String query = "UPDATE books SET title = ?, author = ?, isbn = ?, genre = ?, totalCopies = ?, availableCopies = ? WHERE bookId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
            pstmt.setString(4, genre);
            pstmt.setInt(5, totalCopies);
            pstmt.setInt(6, newAvailableCopies);
            pstmt.setInt(7, bookId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public static List<Transaction> getAllTransactionsHistory() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions ORDER BY transactionId DESC";

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("transactionId"),
                        rs.getInt("bookId"),
                        rs.getInt("memberId"),
                        java.time.LocalDate.parse(rs.getString("issueDate")),
                        java.time.LocalDate.parse(rs.getString("dueDate")));
                String returnDate = rs.getString("returnDate");
                if (returnDate != null) {
                    t.setReturnDate(java.time.LocalDate.parse(returnDate));
                }
                t.setFineAmount(rs.getDouble("fineAmount"));
                transactions.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static String getSetting(String key) {
        String query = "SELECT settingValue FROM settings WHERE settingKey = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("settingValue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateSetting(String key, String value) {
        String query = "UPDATE settings SET settingValue = ? WHERE settingKey = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, value);
            pstmt.setString(2, key);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

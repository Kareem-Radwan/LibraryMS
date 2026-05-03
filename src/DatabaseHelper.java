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
                + "isAvailable BOOLEAN NOT NULL DEFAULT 1"
                + ");";

        String createMembersTable = "CREATE TABLE IF NOT EXISTS members ("
                + "userId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "email TEXT UNIQUE NOT NULL, "
                + "borrowedCount INTEGER DEFAULT 0"
                + ");";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                + "transactionId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "bookId INTEGER, "
                + "memberId INTEGER, "
                + "issueDate TEXT, "
                + "dueDate TEXT, "
                + "returnDate TEXT, "
                + "FOREIGN KEY(bookId) REFERENCES books(bookId), "
                + "FOREIGN KEY(memberId) REFERENCES members(userId)"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement()) {

            stmt.execute(createBooksTable);
            stmt.execute(createMembersTable);
            stmt.execute(createTransactionsTable);
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
                        rs.getString("isbn"));
                if (!rs.getBoolean("isAvailable")) {
                    book.toggleAvailability();
                }
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static String addBook(String title, String author, String isbn) {
        String query = "INSERT INTO books (title, author, isbn) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
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

    public static boolean updateBookAvailability(int bookId, boolean isAvailable) {
        String query = "UPDATE books SET isAvailable = ? WHERE bookId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, isAvailable);
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
        String query = "SELECT * FROM members";

        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Member member = new Member(
                        rs.getInt("userId"),
                        rs.getString("name"),
                        rs.getString("email"));
                member.setBorrowedCount(rs.getInt("borrowedCount"));
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public static String addMember(String name, String email) {
        String query = "INSERT INTO members (name, email) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
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
        String query = "DELETE FROM members WHERE userId = ?";

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
        String query = "UPDATE members SET borrowedCount = ? WHERE userId = ?";

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
                        rs.getString("isbn"));
                if (!rs.getBoolean("isAvailable")) {
                    book.toggleAvailability();
                }
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Member getMemberById(int userId) {
        String query = "SELECT * FROM members WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member(
                        rs.getInt("userId"),
                        rs.getString("name"),
                        rs.getString("email"));
                member.setBorrowedCount(rs.getInt("borrowedCount"));
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
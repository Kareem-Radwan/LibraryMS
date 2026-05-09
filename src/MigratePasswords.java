import java.sql.*;

public class MigratePasswords {
    private static final String URL = "jdbc:sqlite:inkvault.db";

    public static void main(String[] args) {
        System.out.println("Starting password migration...");
        
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            return;
        }

        int migratedCount = 0;
        int skippedCount = 0;

        String selectQuery = "SELECT userId, password FROM users";
        String updateQuery = "UPDATE users SET password = ? WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            while (rs.next()) {
                int userId = rs.getInt("userId");
                String password = rs.getString("password");

                // Check if password is already hashed
                if (PasswordUtil.isHashed(password)) {
                    System.out.println("User ID " + userId + ": Password already hashed, skipping.");
                    skippedCount++;
                    continue;
                }

                // Hash the plain text password
                String hashedPassword = PasswordUtil.hashPassword(password);
                
                // Update the database
                updateStmt.setString(1, hashedPassword);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();

                System.out.println("User ID " + userId + ": Password migrated successfully.");
                migratedCount++;
            }

            System.out.println("\n=== Migration Complete ===");
            System.out.println("Passwords migrated: " + migratedCount);
            System.out.println("Passwords skipped (already hashed): " + skippedCount);

        } catch (SQLException e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

import java.sql.*;

public class MigrateDatabase {
    private static final String URL = "jdbc:sqlite:inkvault.db";

    public static void main(String[] args) {
        System.out.println("Starting database migration...");
        
        try (Connection conn = DriverManager.getConnection(URL)) {
            // Check if genre column exists
            if (!columnExists(conn, "books", "genre")) {
                System.out.println("Adding 'genre' column to books table...");
                conn.createStatement().execute("ALTER TABLE books ADD COLUMN genre TEXT NOT NULL DEFAULT 'General'");
            }
            
            // Check if totalCopies column exists
            if (!columnExists(conn, "books", "totalCopies")) {
                System.out.println("Adding 'totalCopies' column to books table...");
                conn.createStatement().execute("ALTER TABLE books ADD COLUMN totalCopies INTEGER NOT NULL DEFAULT 1");
            }
            
            // Check if availableCopies column exists
            if (!columnExists(conn, "books", "availableCopies")) {
                System.out.println("Adding 'availableCopies' column to books table...");
                conn.createStatement().execute("ALTER TABLE books ADD COLUMN availableCopies INTEGER NOT NULL DEFAULT 1");
                
                // Update availableCopies based on isAvailable if that column exists
                if (columnExists(conn, "books", "isAvailable")) {
                    System.out.println("Migrating isAvailable data to availableCopies...");
                    conn.createStatement().execute("UPDATE books SET availableCopies = CASE WHEN isAvailable = 1 THEN 1 ELSE 0 END");
                }
            }
            
            System.out.println("Migration completed successfully!");
            
        } catch (SQLException e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getColumns(null, null, tableName, columnName);
        boolean exists = rs.next();
        rs.close();
        return exists;
    }
}

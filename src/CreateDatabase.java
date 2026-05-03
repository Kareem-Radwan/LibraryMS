public class CreateDatabase {
    public static void main(String[] args) {
        System.out.println("Creating InkVault database...");
        DatabaseHelper.initializeDatabase();
        System.out.println("Database created successfully at: inkvault.db");
        System.out.println("You can now run the InkVaultApp!");
    }
}

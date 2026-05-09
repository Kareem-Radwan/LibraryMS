import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    
    /**
     * Hash a password using BCrypt
     * @param plainPassword The plain text password
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
    
    /**
     * Verify a password against a hashed password
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to check against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Handle case where hashedPassword is not a valid BCrypt hash
            // This might happen during migration when some passwords are still plain text
            return false;
        }
    }
    
    /**
     * Check if a password is already hashed with BCrypt
     * @param password The password to check
     * @return true if the password is a BCrypt hash, false otherwise
     */
    public static boolean isHashed(String password) {
        return password != null && password.startsWith("$2a$") && password.length() == 60;
    }
}

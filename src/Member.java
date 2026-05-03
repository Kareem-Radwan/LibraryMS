import java.util.ArrayList;
import java.util.List;

public class Member extends User {
    private int borrowedCount;
    private List<Transaction> history;

    public Member(int userId, String name, String email) {
        super(userId, name, email);
        this.borrowedCount = 0;
        this.history = new ArrayList<>();
    }

    public int getBorrowedCount() {
        return borrowedCount;
    }

    public List<Transaction> getHistory() {
        return history;
    }

    public void addTransaction(Transaction t) {
        history.add(t);
        borrowedCount++;
    }

    public void setBorrowedCount(int count) {
        this.borrowedCount = count;
    }

    public void decrementBorrowedCount() {
        if (borrowedCount > 0) {
            borrowedCount--;
        }
    }
}
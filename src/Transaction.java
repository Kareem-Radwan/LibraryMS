import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;

    public Transaction(int transactionId, int bookId, int memberId, LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
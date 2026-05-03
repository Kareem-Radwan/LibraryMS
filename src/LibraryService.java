import java.time.LocalDate;

public class LibraryService {

    private static final int MAX_BORROW_LIMIT = 3;
    private static final int LOAN_PERIOD_DAYS = 14;

    public boolean issueBook(Book book, Member member) {
        if (!book.isAvailable()) {
            System.out.println("Error: Book is currently checked out.");
            return false;
        }

        if (member.getBorrowedCount() >= MAX_BORROW_LIMIT) {
            System.out.println("Error: Member has reached the maximum borrowing limit (" + MAX_BORROW_LIMIT + ").");
            return false;
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);

        // Save to database
        boolean success = DatabaseHelper.addTransaction(
                book.getBookId(),
                member.getUserId(),
                issueDate.toString(),
                dueDate.toString());

        if (success) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            DatabaseHelper.updateBookAvailability(book.getBookId(), false);
            DatabaseHelper.updateMemberBorrowedCount(member.getUserId(), member.getBorrowedCount() + 1);
            member.addTransaction(new Transaction(0, book.getBookId(), member.getUserId(), issueDate, dueDate));
            System.out.println("Success: Book issued. Due on " + dueDate.toString());
            return true;
        }

        return false;
    }

    public boolean returnBook(Transaction transaction) {
        Book book = DatabaseHelper.getBookById(transaction.getBookId());
        Member member = DatabaseHelper.getMemberById(transaction.getMemberId());

        if (book == null || member == null) {
            System.out.println("Error: Book or Member not found.");
            return false;
        }

        LocalDate returnDate = LocalDate.now();
        double fine = 0.0;
        
        if (checkOverdue(transaction)) {
            long overdueDays = calculateOverdueDays(transaction);
            fine = overdueDays * 1.0; // $1 per day
        }
        
        boolean success = DatabaseHelper.returnBook(transaction.getTransactionId(), returnDate.toString());

        if (success) {
            // Update fine in database
            DatabaseHelper.updateTransactionFine(transaction.getTransactionId(), fine);
            
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            DatabaseHelper.updateBookAvailability(book.getBookId(), true);
            member.decrementBorrowedCount();
            DatabaseHelper.updateMemberBorrowedCount(member.getUserId(), member.getBorrowedCount());
            System.out.println("Success: Book returned. Fine: $" + fine);
            return true;
        }

        return false;
    }

    public boolean checkOverdue(Transaction transaction) {
        LocalDate today = LocalDate.now();
        return today.isAfter(transaction.getDueDate());
    }

    public long calculateOverdueDays(Transaction transaction) {
        LocalDate today = LocalDate.now();
        if (today.isAfter(transaction.getDueDate())) {
            return java.time.temporal.ChronoUnit.DAYS.between(transaction.getDueDate(), today);
        }
        return 0;
    }
}
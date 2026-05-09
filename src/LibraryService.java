import java.time.LocalDate;

public class LibraryService {

    public boolean issueBook(Book book, Member member) {
        int maxBorrowLimit = Integer.parseInt(DatabaseHelper.getSetting("maxBorrowLimit"));
        int loanPeriodDays = Integer.parseInt(DatabaseHelper.getSetting("loanPeriodDays"));

        if (!book.isAvailable()) {
            System.out.println("Error: Book is currently checked out.");
            return false;
        }

        if (member.getBorrowedCount() >= maxBorrowLimit) {
            System.out.println("Error: Member has reached the maximum borrowing limit (" + maxBorrowLimit + ").");
            return false;
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(loanPeriodDays);

        // Save to database
        boolean success = DatabaseHelper.addTransaction(
                book.getBookId(),
                member.getUserId(),
                issueDate.toString(),
                dueDate.toString());

        if (success) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            DatabaseHelper.updateBookAvailability(book.getBookId(), book.getAvailableCopies());
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
            double finePerDay = Double.parseDouble(DatabaseHelper.getSetting("finePerDay"));
            fine = overdueDays * finePerDay;
        }
        
        boolean success = DatabaseHelper.returnBook(transaction.getTransactionId(), returnDate.toString());

        if (success) {
            // Update fine in database
            DatabaseHelper.updateTransactionFine(transaction.getTransactionId(), fine);
            
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            DatabaseHelper.updateBookAvailability(book.getBookId(), book.getAvailableCopies());
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
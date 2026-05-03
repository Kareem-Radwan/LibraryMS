# InkVault - Library Management System
## Complete Feature List

### ✅ User Roles & Authentication

#### **Login System**
- Secure login with email and password
- Default admin account: `admin@inkvault.com` / `admin123`
- Role-based access control

#### **Three User Roles:**

1. **ADMIN** - Full system access
   - Manage all books (add, edit, delete)
   - Manage all users (add, delete, assign roles)
   - View all members and transactions
   - Access complete transaction history
   - View dashboard metrics

2. **LIBRARIAN** - Library operations
   - Manage books (add, edit, delete)
   - Manage member registrations
   - Issue and return books
   - Calculate fines automatically
   - View transaction history
   - Track overdue books

3. **MEMBER** - Borrower access
   - Search and browse books
   - View personal borrowed books
   - Check due dates and overdue status
   - View borrowing history

---

### ✅ Book Management

#### **Features:**
- ✅ **Add Books** - Title, Author, ISBN, Genre, Total Copies
- ✅ **Edit Books** - Update all book details including copies
- ✅ **Delete Books** - Remove books from inventory
- ✅ **Search & Filter** - Real-time search by title, author, or ISBN
- ✅ **Availability Tracking** - Shows available vs checked out copies
- ✅ **Genre Classification** - Organize books by genre

#### **Book Details Tracked:**
- Book ID (auto-generated)
- Title
- Author
- ISBN (unique)
- Genre
- Total Copies
- Available Copies
- Status (Available/Checked Out)

---

### ✅ User Management (Admin Only)

#### **Features:**
- ✅ **Add Users** - Create Admin, Librarian, or Member accounts
- ✅ **Delete Users** - Remove user accounts
- ✅ **Role Assignment** - Assign ADMIN, LIBRARIAN, or MEMBER roles
- ✅ **Employee ID** - Track librarian employee IDs
- ✅ **Password Management** - Set passwords for new users

#### **User Details:**
- User ID
- Name
- Email (unique, used for login)
- Password
- Role (ADMIN/LIBRARIAN/MEMBER)
- Employee ID (for librarians)
- Borrowed Count (for members)

---

### ✅ Member Registry

#### **Features:**
- ✅ **Add Members** - Register new library members
- ✅ **Delete Members** - Remove inactive members
- ✅ **Search Members** - Find by name or email
- ✅ **Track Borrowing** - See how many books each member has borrowed
- ✅ **Validation** - Prevent deletion of members with active loans

---

### ✅ Borrow & Return System

#### **Issue Books:**
- ✅ Select available books from dropdown
- ✅ Select members from dropdown
- ✅ Automatic due date calculation (14 days)
- ✅ Borrowing limit enforcement (max 3 books per member)
- ✅ Real-time availability updates

#### **Return Books:**
- ✅ Select transaction from active list
- ✅ Automatic fine calculation for overdue books
- ✅ Update book availability
- ✅ Update member borrowed count
- ✅ Record return date

---

### ✅ Fine Calculation

#### **Automatic Fine System:**
- ✅ **$1.00 per day** for overdue books
- ✅ **Automatic calculation** on return
- ✅ **Overdue tracking** - Shows days overdue
- ✅ **Fine display** in transaction history
- ✅ **Visual indicators** - Red "OVERDUE" status in tables

#### **Fine Details:**
- Calculated automatically when book is returned
- Stored in database with transaction
- Displayed in transaction history
- Shows overdue days count

---

### ✅ Transaction History

#### **Active Transactions:**
- Transaction ID
- Book Title
- Member Name
- Issue Date
- Due Date
- Status (Active/Overdue with days count)

#### **Complete History:**
- All transactions (active + returned)
- Return dates
- Fine amounts
- Full audit trail
- Sortable and searchable

---

### ✅ Search & Filtering

#### **Book Search:**
- ✅ Real-time filtering
- ✅ Search by title, author, ISBN
- ✅ Case-insensitive
- ✅ Instant results

#### **Member Search:**
- ✅ Search by name or email
- ✅ Real-time filtering
- ✅ Instant results

#### **Genre-based Organization:**
- ✅ Books categorized by genre
- ✅ Genre field in book details
- ✅ Editable genre classification

---

### ✅ GUI Dashboard

#### **Modern Dark Theme Interface:**
- ✅ Professional FlatLaf dark theme
- ✅ Responsive layout
- ✅ Color-coded buttons and status
- ✅ Intuitive navigation sidebar

#### **Dashboard Metrics:**
- ✅ Total Books count
- ✅ Total Members count
- ✅ Active Borrowed Books count
- ✅ Real-time updates

#### **Role-Based Navigation:**
- **Admin sees:** Dashboard, Books, Users, Members, Transactions, History
- **Librarian sees:** Dashboard, Books, Members, Transactions, History
- **Member sees:** Dashboard, Books, My Books

---

### ✅ Additional Features

#### **Data Validation:**
- ✅ Required field validation
- ✅ Email format validation
- ✅ ISBN uniqueness check
- ✅ Duplicate prevention

#### **Business Rules:**
- ✅ Max 3 books per member
- ✅ 14-day loan period
- ✅ Cannot delete members with active loans
- ✅ Cannot issue unavailable books

#### **Database:**
- ✅ SQLite database (inkvault.db)
- ✅ Automatic initialization
- ✅ Foreign key relationships
- ✅ Transaction integrity

---

## How to Run

### **Compile:**
```bash
javac -d bin -cp "lib/*" src/*.java
```

### **Run:**
```bash
java -cp "bin;lib/*" InkVaultApp
```

### **Default Login:**
- Email: `admin@inkvault.com`
- Password: `admin123`

---

## Testing the Features

### **As Admin:**
1. Login with admin credentials
2. Go to "Users" - Add librarians and members
3. Go to "Books" - Add, edit, delete books
4. Go to "Members" - View all members
5. Go to "Transactions" - Issue and return books
6. Go to "History" - View complete transaction log

### **As Librarian:**
1. Create a librarian user from Admin panel
2. Logout and login as librarian
3. Manage books and members
4. Issue and return books
5. View transaction history

### **As Member:**
1. Create a member user from Admin panel
2. Logout and login as member
3. Browse available books
4. View "My Books" to see borrowed books
5. Check due dates and overdue status

---

## All University Requirements Met ✅

✅ **User Roles:** Admin, Librarian, Member with proper access control  
✅ **Book Management:** Add, edit, remove with all details  
✅ **User Management:** Add/remove users, assign roles  
✅ **Search & Filtering:** By title, author, genre  
✅ **Borrow & Return:** Track due dates, overdue books, penalties  
✅ **Fine Calculation:** Automatic late return fines  
✅ **Transaction History:** Complete logs of issued and returned books  
✅ **GUI Dashboard:** User-friendly interface for all roles  

---

## Technical Stack

- **Language:** Java
- **GUI:** Swing with FlatLaf Look and Feel
- **Database:** SQLite (JDBC)
- **Architecture:** MVC pattern
- **Build:** Standard javac compilation

---

## Project Structure

```
Library/
├── src/
│   ├── InkVaultApp.java       # Main GUI application
│   ├── LoginFrame.java         # Login screen
│   ├── DatabaseHelper.java     # Database operations
│   ├── LibraryService.java     # Business logic
│   ├── User.java               # User base class
│   ├── Admin.java              # Admin role
│   ├── Librarian.java          # Librarian role
│   ├── Member.java             # Member role
│   ├── Book.java               # Book entity
│   ├── Transaction.java        # Transaction entity
│   ├── CreateDatabase.java     # DB initialization
│   └── MigrateDatabase.java    # DB migration utility
├── lib/
│   ├── flatlaf.jar            # UI theme
│   ├── sqlite-jdbc.jar        # Database driver
│   └── slf4j-*.jar            # Logging
├── bin/                        # Compiled classes
└── inkvault.db                # SQLite database
```

---

**All features are fully implemented and working!** 🎉

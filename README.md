# 📚 InkVault - Library Management System

A modern, feature-rich desktop application for managing library operations built with Java Swing and SQLite. InkVault provides an intuitive interface for managing books, members, and transactions with a sleek dark-themed UI powered by FlatLaf.

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![SQLite](https://img.shields.io/badge/SQLite-3.46-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![Status](https://img.shields.io/badge/Status-Active-success.svg)

## ✨ Features

### 📖 Book Management
- Add, view, and delete books from inventory
- Track book availability status (Available/Checked Out)
- Search and filter books by title, author, or ISBN
- Real-time inventory updates

### 👥 Member Management
- Register new library members
- View member details and borrowing history
- Track number of books borrowed per member
- Email validation and duplicate prevention
- Search members by name or email

### 🔄 Transaction Management
- Issue books to members with automatic due date calculation (14-day loan period)
- Return books with automatic status updates
- View active transactions in real-time
- Enforce borrowing limits (max 3 books per member)
- Prevent issuing unavailable books

### 📊 Dashboard Analytics
- Real-time metrics display
- Total books in inventory
- Total registered members
- Active borrowed books count
- Visual card-based layout

### 🎨 Modern UI/UX
- Dark-themed interface using FlatLaf Look and Feel
- Responsive design with intuitive navigation
- Color-coded action buttons
- Search functionality with live filtering
- Sortable tables with custom styling

## 🛠️ Technology Stack

- **Language**: Java 17+
- **GUI Framework**: Swing with FlatLaf
- **Database**: SQLite 3.46
- **JDBC Driver**: sqlite-jdbc-3.46.0.0
- **Logging**: SLF4J 2.0.13
- **UI Theme**: FlatLaf Dark

## 📋 Prerequisites

- Java Development Kit (JDK) 17 or higher
- Java Runtime Environment (JRE) 17 or higher

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Kareem-Radwan/LibraryMS.git
cd LibraryMS
```

### 2. Compile the Project
```bash
javac -cp "lib/*" -d bin src/*.java
```

### 3. Initialize the Database
```bash
java -cp "bin;lib/*" CreateDatabase
```

### 4. Run the Application
```bash
java -cp "bin;lib/*" InkVaultApp
```

## 📁 Project Structure

```
LibraryMS/
├── src/
│   ├── InkVaultApp.java       # Main application GUI
│   ├── LibraryService.java    # Business logic layer
│   ├── DatabaseHelper.java    # Database operations
│   ├── CreateDatabase.java    # Database initialization
│   ├── Book.java              # Book entity
│   ├── Member.java            # Member entity
│   ├── Librarian.java         # Librarian entity
│   ├── User.java              # Base user class
│   └── Transaction.java       # Transaction entity
├── lib/
│   ├── flatlaf.jar           # FlatLaf Look and Feel
│   ├── sqlite-jdbc-3.46.0.0.jar
│   ├── slf4j-api-2.0.13.jar
│   └── slf4j-nop-2.0.13.jar
├── bin/                       # Compiled classes
├── inkvault.db               # SQLite database
└── README.md
```

## 💾 Database Schema

### Books Table
```sql
CREATE TABLE books (
    bookId INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    isbn TEXT UNIQUE NOT NULL,
    isAvailable BOOLEAN NOT NULL DEFAULT 1
);
```

### Members Table
```sql
CREATE TABLE members (
    userId INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    borrowedCount INTEGER DEFAULT 0
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    transactionId INTEGER PRIMARY KEY AUTOINCREMENT,
    bookId INTEGER,
    memberId INTEGER,
    issueDate TEXT,
    dueDate TEXT,
    returnDate TEXT,
    FOREIGN KEY(bookId) REFERENCES books(bookId),
    FOREIGN KEY(memberId) REFERENCES members(userId)
);
```

## 🎯 Usage Guide

### Adding a Book
1. Navigate to **Books** section
2. Click **Add New Book**
3. Enter book details (Title, Author, ISBN)
4. Click **Save**

### Registering a Member
1. Navigate to **Members** section
2. Click **Add Member**
3. Enter member details (Name, Email)
4. Click **Save**

### Issuing a Book
1. Navigate to **Transactions** section
2. Select an available book from dropdown
3. Select a member from dropdown
4. Click **Issue Book**

### Returning a Book
1. Navigate to **Transactions** section
2. Select the transaction from the table
3. Click **Return Selected Book**

## 🔒 Business Rules

- **Borrowing Limit**: Members can borrow a maximum of 3 books simultaneously
- **Loan Period**: Books are issued for 14 days
- **Availability Check**: Only available books can be issued
- **Member Deletion**: Members with borrowed books cannot be deleted
- **Unique Constraints**: ISBN and email must be unique

## 🎨 UI Features

- **Color Scheme**:
  - Primary: `#00C8FF` (Cyan Blue)
  - Success: `#2ECC71` (Green)
  - Danger: `#E74C3C` (Red)
  - Info: `#3498DB` (Blue)
  - Warning: `#9B59B6` (Purple)

- **Interactive Elements**:
  - Hover effects on buttons
  - Sortable table columns
  - Live search filtering
  - Modal dialogs for data entry

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Kareem Radwan**
- GitHub: [@Kareem-Radwan](https://github.com/Kareem-Radwan)

## 🙏 Acknowledgments

- [FlatLaf](https://www.formdev.com/flatlaf/) - Modern Look and Feel for Java Swing
- [SQLite](https://www.sqlite.org/) - Lightweight database engine
- [SLF4J](https://www.slf4j.org/) - Simple Logging Facade for Java

## 📧 Support

For support, email or open an issue in the GitHub repository.

## 🔮 Future Enhancements

- [ ] Fine calculation for overdue books
- [ ] Book reservation system
- [ ] Advanced search with filters
- [ ] Export reports to PDF/Excel
- [ ] User authentication and roles
- [ ] Book categories and genres
- [ ] Email notifications for due dates
- [ ] Barcode scanning support
- [ ] Multi-language support

---

⭐ If you find this project useful, please consider giving it a star!

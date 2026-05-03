import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

public class InkVaultApp extends JFrame {

    private DefaultTableModel bookTableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private DefaultTableModel memberTableModel;
    private TableRowSorter<DefaultTableModel> memberRowSorter;
    private DefaultTableModel transactionTableModel;
    private JComboBox<String> bookCombo;
    private JComboBox<String> memberCombo;
    private LibraryService libraryService;
    
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Dashboard labels
    private JLabel totalBooksLabel;
    private JLabel totalMembersLabel;
    private JLabel totalTransactionsLabel;

    public InkVaultApp() {
        setTitle("InkVault - Library Management System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        libraryService = new LibraryService();

        // Use BorderLayout for main frame
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main Content Area with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        
        mainContentPanel.add(createDashboardPanel(), "Dashboard");
        mainContentPanel.add(createBookInventoryPanel(), "Books");
        mainContentPanel.add(createMemberRegistryPanel(), "Members");
        mainContentPanel.add(createTransactionPanel(), "Transactions");

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Initial Data Load
        updateDashboardMetrics();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel logo = new JLabel("InkVault", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(new Color(0, 200, 255));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        String[] navItems = {"Dashboard", "Books", "Members", "Transactions"};
        for (String item : navItems) {
            JButton navBtn = new JButton(item);
            navBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            navBtn.setMaximumSize(new Dimension(180, 40));
            navBtn.setFocusPainted(false);
            navBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            navBtn.setBackground(new Color(40, 40, 40));
            navBtn.setForeground(Color.WHITE);
            navBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            navBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            navBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    navBtn.setBackground(new Color(60, 60, 60));
                }
                public void mouseExited(MouseEvent evt) {
                    navBtn.setBackground(new Color(40, 40, 40));
                }
            });

            navBtn.addActionListener(e -> {
                cardLayout.show(mainContentPanel, item);
                if(item.equals("Dashboard")) updateDashboardMetrics();
            });

            sidebar.add(navBtn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        return sidebar;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(25, 25, 25));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsPanel.setBackground(new Color(25, 25, 25));
        cardsPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        totalBooksLabel = new JLabel("0", SwingConstants.CENTER);
        totalMembersLabel = new JLabel("0", SwingConstants.CENTER);
        totalTransactionsLabel = new JLabel("0", SwingConstants.CENTER);

        cardsPanel.add(createCard("Total Books", totalBooksLabel, new Color(52, 152, 219)));
        cardsPanel.add(createCard("Total Members", totalMembersLabel, new Color(46, 204, 113)));
        cardsPanel.add(createCard("Active Borrowed", totalTransactionsLabel, new Color(231, 76, 60)));

        panel.add(cardsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(40, 40, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(valueLabel);

        return card;
    }
    
    private void updateDashboardMetrics() {
        int totalBooks = DatabaseHelper.getAllBooks().size();
        int totalMembers = DatabaseHelper.getAllMembers().size();
        int activeTrans = DatabaseHelper.getAllTransactions().size();
        
        totalBooksLabel.setText(String.valueOf(totalBooks));
        totalMembersLabel.setText(String.valueOf(totalMembers));
        totalTransactionsLabel.setText(String.valueOf(activeTrans));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(40, 40, 40));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JPanel createBookInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(25, 25, 25));

        JLabel title = new JLabel("Book Inventory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 25));
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(25, 25, 25));
        JLabel searchIcon = new JLabel("Search: ");
        searchIcon.setForeground(Color.WHITE);
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.putClientProperty("JTextField.placeholderText", "Title, Author, ISBN...");
        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "Book ID", "Title", "Author", "ISBN", "Status" };
        bookTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable bookTable = new JTable(bookTableModel);
        styleTable(bookTable);
        rowSorter = new TableRowSorter<>(bookTableModel);
        bookTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
        panel.add(scrollPane, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
            private void filterData() {
                String text = searchField.getText();
                if (text.trim().length() == 0) rowSorter.setRowFilter(null);
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(25, 25, 25));
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton addButton = createStyledButton("Add New Book", new Color(46, 204, 113));
        JButton deleteButton = createStyledButton("Delete Selected", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh List", new Color(52, 152, 219));

        addButton.addActionListener(e -> showAddBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook(bookTable));
        refreshButton.addActionListener(e -> { loadBookData(); loadTransactionComboBoxes(); });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadBookData();
        return panel;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        return btn;
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Add New Book", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField isbnField = new JTextField(20);
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(titleLabel, gbc);
        gbc.gridx = 1; dialog.add(titleField, gbc);

        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setForeground(Color.WHITE);
        authorLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(authorLabel, gbc);
        gbc.gridx = 1; dialog.add(authorField, gbc);

        JLabel isbnLabel = new JLabel("ISBN:");
        isbnLabel.setForeground(Color.WHITE);
        isbnLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(isbnLabel, gbc);
        gbc.gridx = 1; dialog.add(isbnField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String result = DatabaseHelper.addBook(title, author, isbn);
            if (result.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(dialog, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBookData(); loadTransactionComboBoxes(); updateDashboardMetrics();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook(JTable bookTable) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = bookTable.convertRowIndexToModel(selectedRow);
        int bookId = (int) bookTableModel.getValueAt(modelRow, 0);
        String title = (String) bookTableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete: " + title + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseHelper.deleteBook(bookId)) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBookData(); loadTransactionComboBoxes(); updateDashboardMetrics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadBookData() {
        bookTableModel.setRowCount(0);
        List<Book> books = DatabaseHelper.getAllBooks();
        for (Book b : books) {
            Object[] row = { b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.isAvailable() ? "Available" : "Checked Out" };
            bookTableModel.addRow(row);
        }
    }

    private JPanel createMemberRegistryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(25, 25, 25));

        JLabel title = new JLabel("Member Registry");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 25));
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(25, 25, 25));
        JLabel searchIcon = new JLabel("Search: ");
        searchIcon.setForeground(Color.WHITE);
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.putClientProperty("JTextField.placeholderText", "Name, Email...");
        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = { "Member ID", "Name", "Email", "Books Borrowed" };
        memberTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable memberTable = new JTable(memberTableModel);
        styleTable(memberTable);
        memberRowSorter = new TableRowSorter<>(memberTableModel);
        memberTable.setRowSorter(memberRowSorter);

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
        panel.add(scrollPane, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
            private void filterData() {
                String text = searchField.getText();
                if (text.trim().length() == 0) memberRowSorter.setRowFilter(null);
                else memberRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(25, 25, 25));
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton addButton = createStyledButton("Add Member", new Color(46, 204, 113));
        JButton deleteButton = createStyledButton("Delete Selected", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh List", new Color(52, 152, 219));

        addButton.addActionListener(e -> showAddMemberDialog());
        deleteButton.addActionListener(e -> deleteSelectedMember(memberTable));
        refreshButton.addActionListener(e -> { loadMemberData(); loadTransactionComboBoxes(); });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadMemberData();
        return panel;
    }

    private void showAddMemberDialog() {
        JDialog dialog = new JDialog(this, "Add New Member", true);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(nameLabel, gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(emailLabel, gbc);
        gbc.gridx = 1; dialog.add(emailField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton saveButton = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid email address!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String result = DatabaseHelper.addMember(name, email);
            if (result.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(dialog, "Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMemberData(); loadTransactionComboBoxes(); updateDashboardMetrics();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }

    private void deleteSelectedMember(JTable memberTable) {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = memberTable.convertRowIndexToModel(selectedRow);
        int userId = (int) memberTableModel.getValueAt(modelRow, 0);
        String name = (String) memberTableModel.getValueAt(modelRow, 1);
        int borrowedCount = (int) memberTableModel.getValueAt(modelRow, 3);

        if (borrowedCount > 0) {
            JOptionPane.showMessageDialog(this, "Cannot delete member with borrowed books. Please return all books first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete member: " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseHelper.deleteMember(userId)) {
                JOptionPane.showMessageDialog(this, "Member deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMemberData(); loadTransactionComboBoxes(); updateDashboardMetrics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete member.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadMemberData() {
        memberTableModel.setRowCount(0);
        List<Member> members = DatabaseHelper.getAllMembers();
        for (Member m : members) {
            Object[] row = { m.getUserId(), m.getName(), m.getEmail(), m.getBorrowedCount() };
            memberTableModel.addRow(row);
        }
    }

    private JPanel createTransactionPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(25, 25, 25));

        JLabel title = new JLabel("Transaction Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Issue/Return Form
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(new Color(35, 35, 35));
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 20);

        JLabel bookLbl = new JLabel("Select Book:");
        bookLbl.setForeground(Color.WHITE);
        bookLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; actionPanel.add(bookLbl, gbc);

        bookCombo = new JComboBox<>();
        bookCombo.setPreferredSize(new Dimension(350, 35));
        bookCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0; actionPanel.add(bookCombo, gbc);

        JLabel memLbl = new JLabel("Select Member:");
        memLbl.setForeground(Color.WHITE);
        memLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1; actionPanel.add(memLbl, gbc);

        memberCombo = new JComboBox<>();
        memberCombo.setPreferredSize(new Dimension(350, 35));
        memberCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 1; actionPanel.add(memberCombo, gbc);

        JButton issueButton = createStyledButton("Issue Book", new Color(155, 89, 182));
        issueButton.setPreferredSize(new Dimension(200, 45));
        issueButton.addActionListener(e -> issueBookAction());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        actionPanel.add(issueButton, gbc);

        // Transaction History Table
        String[] columnNames = { "Tx ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Status" };
        transactionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable transactionTable = new JTable(transactionTableModel);
        styleTable(transactionTable);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        
        JLabel histLbl = new JLabel("Active Transactions History");
        histLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        histLbl.setForeground(Color.LIGHT_GRAY);
        
        JPanel histPanel = new JPanel(new BorderLayout());
        histPanel.setOpaque(false);
        histPanel.add(histLbl, BorderLayout.NORTH);
        histPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(histPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(25, 25, 25));
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton returnButton = createStyledButton("Return Selected Book", new Color(46, 204, 113));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));

        returnButton.addActionListener(e -> returnBookAction(transactionTable));
        refreshButton.addActionListener(e -> {
            loadTransactionData(); loadTransactionComboBoxes(); loadBookData(); loadMemberData();
        });

        bottomPanel.add(returnButton);
        bottomPanel.add(refreshButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadTransactionComboBoxes();
        loadTransactionData();

        return mainPanel;
    }

    private void loadTransactionComboBoxes() {
        bookCombo.removeAllItems();
        memberCombo.removeAllItems();

        bookCombo.addItem("Select a Book...");
        List<Book> books = DatabaseHelper.getAllBooks();
        for (Book book : books) {
            if (book.isAvailable()) {
                bookCombo.addItem(book.getBookId() + " - " + book.getTitle());
            }
        }

        memberCombo.addItem("Select a Member...");
        List<Member> members = DatabaseHelper.getAllMembers();
        for (Member member : members) {
            memberCombo.addItem(member.getUserId() + " - " + member.getName());
        }
    }

    private void issueBookAction() {
        String selectedBook = (String) bookCombo.getSelectedItem();
        String selectedMember = (String) memberCombo.getSelectedItem();

        if (selectedBook == null || selectedBook.equals("Select a Book...")) {
            JOptionPane.showMessageDialog(this, "Please select a book.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedMember == null || selectedMember.equals("Select a Member...")) {
            JOptionPane.showMessageDialog(this, "Please select a member.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = Integer.parseInt(selectedBook.split(" - ")[0]);
        int memberId = Integer.parseInt(selectedMember.split(" - ")[0]);

        Book book = DatabaseHelper.getBookById(bookId);
        Member member = DatabaseHelper.getMemberById(memberId);

        if (book != null && member != null) {
            if (libraryService.issueBook(book, member)) {
                JOptionPane.showMessageDialog(this, "Book issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTransactionData(); loadTransactionComboBoxes(); loadBookData(); loadMemberData(); updateDashboardMetrics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to issue book. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void returnBookAction(JTable transactionTable) {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to return.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int transactionId = (int) transactionTableModel.getValueAt(selectedRow, 0);
        List<Transaction> transactions = DatabaseHelper.getAllTransactions();
        Transaction transaction = null;

        for (Transaction t : transactions) {
            if (t.getTransactionId() == transactionId) {
                transaction = t;
                break;
            }
        }

        if (transaction != null) {
            if (libraryService.returnBook(transaction)) {
                String message = "Book returned successfully!";
                if (libraryService.checkOverdue(transaction)) {
                    long overdueDays = libraryService.calculateOverdueDays(transaction);
                    message += "\nNote: Book was " + overdueDays + " day(s) overdue.";
                }
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTransactionData(); loadTransactionComboBoxes(); loadBookData(); loadMemberData(); updateDashboardMetrics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to return book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTransactionData() {
        transactionTableModel.setRowCount(0);
        List<Transaction> transactions = DatabaseHelper.getAllTransactions();
        for (Transaction t : transactions) {
            Book book = DatabaseHelper.getBookById(t.getBookId());
            Member member = DatabaseHelper.getMemberById(t.getMemberId());

            String status = "Active";
            if (libraryService.checkOverdue(t)) {
                long overdueDays = libraryService.calculateOverdueDays(t);
                status = "OVERDUE (" + overdueDays + " days)";
            }

            Object[] row = {
                    t.getTransactionId(),
                    book != null ? book.getTitle() : "Unknown",
                    member != null ? member.getName() : "Unknown",
                    t.getIssueDate().toString(),
                    t.getDueDate().toString(),
                    status
            };
            transactionTableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
        
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SwingUtilities.invokeLater(() -> {
            InkVaultApp app = new InkVaultApp();
            app.setVisible(true);
        });
    }
}
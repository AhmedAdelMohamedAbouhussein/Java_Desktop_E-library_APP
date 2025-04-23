package GUI.CustomerPanels;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

import Classes.Books;
import Classes.Customers;
import Classes.Reviews;
import GUI.MainFrame;
import GUI.stylesAndComponents.Labels;
import GUI.stylesAndComponents.StyledPanel;
import GUI.stylesAndComponents.ComboBoxes;

public class ViewBookReviews extends JPanel {
    private MainFrame mainFrame;
    private Customers customer;

    public ViewBookReviews(MainFrame mainFrame, Customers customer) {
        this.mainFrame = mainFrame;
        this.customer = customer;

        // Main panel
        StyledPanel mainPanel = new StyledPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("View Book Reviews");
        Labels.styleELibraryLabel(title, "title");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, gbc);

        gbc.gridy++;

        // ComboBox for borrowed + owned books
        JComboBox<String> bookComboBox = new JComboBox<>();
        HashSet<String> addedBooks = new HashSet<>();

        for (Books book : customer.getBorrowedBookList()) {
            if (addedBooks.add(book.getBookName())) {
                bookComboBox.addItem(book.getBookName());
            }
        }

        for (Books book : customer.getOwnedBookList()) {
            if (addedBooks.add(book.getBookName())) {
                bookComboBox.addItem(book.getBookName());
            }
        }

        ComboBoxes.styleELibraryComboBox(bookComboBox);
        mainPanel.add(bookComboBox, gbc);

        gbc.gridy++;

        // Reviews display area
        JTextArea reviewDisplayArea = new JTextArea(10, 40);
        reviewDisplayArea.setEditable(false);
        reviewDisplayArea.setLineWrap(true);
        reviewDisplayArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(reviewDisplayArea);
        mainPanel.add(scrollPane, gbc);

        // Listener for combo box
        bookComboBox.addActionListener(e -> {
            String selectedBookName = (String) bookComboBox.getSelectedItem();
            reviewDisplayArea.setText("");

            if (selectedBookName != null) {
                Books selectedBook = Books.findBookByName(selectedBookName);
                if (selectedBook != null) {
                    StringBuilder reviewText = new StringBuilder();
                    for (Reviews review : selectedBook.getReviews()) {
                        reviewText.append("• ").append(review.getReview()).append("\n\n");
                    }
                    if (reviewText.length() == 0) {
                        reviewDisplayArea.setText("No reviews found for this book.");
                    } else {
                        reviewDisplayArea.setText(reviewText.toString());
                    }
                }
            }
        });

        // Trigger first load
        if (bookComboBox.getItemCount() > 0) {
            bookComboBox.setSelectedIndex(0);
        }

        // Add to main layout
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }
}

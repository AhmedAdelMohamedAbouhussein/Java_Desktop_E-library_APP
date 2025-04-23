package GUI.CustomerPanels;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

import Classes.Books;
import Classes.Customers;
import GUI.MainFrame;
import GUI.stylesAndComponents.Labels;
import GUI.stylesAndComponents.StyledPanel;
import GUI.stylesAndComponents.TextFields;
import GUI.stylesAndComponents.Buttons;
import GUI.stylesAndComponents.ComboBoxes;
import GUI.stylesAndComponents.CustomDialogUtil;

public class AddReview extends JPanel {
    private MainFrame mainFrame;
    private Customers customer;

    public AddReview(MainFrame mainFrame, Customers customer) {
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
        JLabel title = new JLabel("Add Review");
        Labels.styleELibraryLabel(title, "title");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, gbc);

        gbc.gridy++;

        // ComboBox for borrowed + owned books (no duplicates)
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

        // Review TextArea
        JTextArea reviewArea = new JTextArea(5, 20);
        TextFields.styleELibraryTextArea(reviewArea);
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        JScrollPane reviewScrollPane = new JScrollPane(reviewArea);
        mainPanel.add(reviewScrollPane, gbc);

        gbc.gridy++;

        // Submit button
        JButton submitBtn = new JButton("Submit Review");
        Buttons.styleELibraryButton(submitBtn);
        mainPanel.add(submitBtn, gbc);

        // Action listener
        submitBtn.addActionListener(e -> {
            String selectedBookName = (String) bookComboBox.getSelectedItem();
            String reviewText = reviewArea.getText().trim();

            if (selectedBookName == null || selectedBookName.isEmpty()) {
                CustomDialogUtil.showStyledMessage(null, "Please select a book.", "Error");
                return;
            }

            if (reviewText.isEmpty()) {
                CustomDialogUtil.showStyledMessage(null, "Please write a review before submitting.", "Error");
                return;
            }

            // Word count check (minimum 50 words)
            int wordCount = reviewText.split("\\s+").length;
            if (wordCount < 50) {
                CustomDialogUtil.showStyledMessage(null, 
                    "Your review is too short. Please write at least 50 words. Current word count: " + wordCount,
                    "Review Too Short");
                return;
            }

            Books selectedBook = Books.findBookByName(selectedBookName);
            if (selectedBook != null) {
                Books.addReview(selectedBook.getBookId(), customer.getId(), reviewText);
                CustomDialogUtil.showStyledMessage(null, "Review submitted successfully!", "Success");
                reviewArea.setText("");
            } else {
                CustomDialogUtil.showStyledMessage(null, "Selected book not found.", "Error");
            }
        });

        // Add scroll pane for the entire panel
        JScrollPane outerScrollPane = new JScrollPane(mainPanel);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(16); // smooth scrolling

        // Set layout and add
        setLayout(new BorderLayout());
        add(outerScrollPane, BorderLayout.CENTER);
    }
}

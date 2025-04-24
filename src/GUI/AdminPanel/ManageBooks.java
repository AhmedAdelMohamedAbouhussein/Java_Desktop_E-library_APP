package GUI.AdminPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import Classes.Admin;
import Classes.Books;
import Classes.Publisher;
import Database.DAO.BookDAO;
import GUI.MainFrame;
import GUI.stylesAndComponents.*;

public class ManageBooks extends JPanel 
{
    MainFrame mainFrame;
    Admin admin;

    public ManageBooks(MainFrame mainFrame, Admin admin) 
    {
        this.mainFrame = mainFrame;
        this.admin = admin;

        // Get the list of all books
        ArrayList<Books> booksList = new ArrayList<>(Books.getAllBooksList().values());

        // Main styled panel
        StyledPanel mainPanel = new StyledPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Manage Books");
        Labels.styleELibraryLabel(titleLabel, "title");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel to hold all book cards
        StyledPanel bookPanel = new StyledPanel();
        bookPanel.setLayout(new BoxLayout(bookPanel, BoxLayout.Y_AXIS));
        bookPanel.setBackground(new Color(245, 245, 245));

        // Add book info cards
        for (Books book : booksList) {
            StyledPanel bookCard = new StyledPanel();
            bookCard.setLayout(new GridLayout(6, 1));
            bookCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            JLabel idLabel = new JLabel("ID: " + book.getBookId());
            JLabel nameLabel = new JLabel("Name: " + book.getBookName());
            JLabel priceLabel = new JLabel("Price: $" + book.getPrice());
            JLabel categoryLabel = new JLabel("Category: " + book.getCategory());
            JLabel publisherIDLabel = new JLabel("Publisher ID: " + book.getPublisherID());
            JLabel fileLabel = new JLabel("Files: " + (book.getCoverImageFile() != null ? book.getCoverImageFile().getName() : "None") + ", " +(book.getBookTextFile() != null ? book.getBookTextFile().getName() : "None"));

            Labels.styleELibraryLabel(idLabel, "small");
            Labels.styleELibraryLabel(nameLabel, "small");
            Labels.styleELibraryLabel(priceLabel, "small");
            Labels.styleELibraryLabel(categoryLabel, "small");
            Labels.styleELibraryLabel(publisherIDLabel, "small");
            Labels.styleELibraryLabel(fileLabel, "small");

            bookCard.add(idLabel);
            bookCard.add(nameLabel);
            bookCard.add(priceLabel);
            bookCard.add(categoryLabel);
            bookCard.add(publisherIDLabel);
            bookCard.add(fileLabel);

            // Add Edit and Delete Buttons for managing books
            JPanel actionPanel = new JPanel();
            JButton deleteButton = new JButton("Delete");

            // Delete button action (removes the book)
            deleteButton.addActionListener(e -> deleteBook(book));

            actionPanel.add(deleteButton);
            bookCard.add(actionPanel);

            bookPanel.add(bookCard);
            bookPanel.add(Box.createVerticalStrut(10));
        }

        // ScrollPane for the list of books
        JScrollPane scrollPane = new JScrollPane(bookPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Final setup
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    // Delete book
    private void deleteBook(Books selectedBook) 
    {

        boolean success = BookDAO.deleteBook(selectedBook.getBookId());
        
        if (success) 
        {
            // Get publisher by ID
            Publisher publisher = Publisher.getPublisherList().stream().filter(p -> p.getId() == selectedBook.getPublisherID()).findFirst().orElse(null); // Find publisher by ID

            if (publisher != null) 
            {
                // Remove book from publisher's book list
                publisher.getBookList().remove(selectedBook);
                
                // Remove book from the booksByPublisher map
                ArrayList<Books> booksByPub = Books.getbooksByPublisher().get(publisher.getId());
                if (booksByPub != null) 
                {
                    booksByPub.remove(selectedBook);
                }
            }

            // Remove the book from the AllBooksList map
            Books.getAllBooksList().remove(selectedBook.getBookId());

            // Show success message
            CustomDialogUtil.showStyledMessage(null, "Book removed successfully.", "Success");
            
            // Refresh the panel (update the list)
            reloadPanel(new ManageBooks(mainFrame,admin)); // Optionally pass any necessary data
        } else {
            // Show error message if deletion failed
            CustomDialogUtil.showStyledMessage(null, "Failed to remove book from database.", "Error");
        }
    }


    public void reloadPanel(JPanel newPanel) 
    {
        // Remove the current panel
        this.removeAll();

        // Add the new panel
        this.add(newPanel, BorderLayout.CENTER);

        // Revalidate and repaint to refresh the UI
        this.revalidate();
        this.repaint();
    }
}

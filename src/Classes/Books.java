package Classes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Database.DAO.BookDAO;

public class Books 
{
    private String bookName;
    private int bookId;
    private double price;
    private String category;
    private int publisherID;
    private File coverImageFile;  // New field
    private File bookTextFile;    // New field

    private ArrayList<Reviews> reviews;

    private static HashMap<Integer, Books> AllbooksList = new HashMap<>();
    private static HashMap<Integer, ArrayList<Books>> booksByPublisher = new HashMap<>();

    // Constructor without files (for new books)
    public Books(int publisherID, String bookName, double price , String category, File coverImageFile, File bookTextFile)
    {
        this.bookName = bookName;
        this.bookId = BookDAO.giveID();
        this.price = price; 
        this.category = category;
        this.publisherID = publisherID;
        this.coverImageFile = coverImageFile;
        this.bookTextFile = bookTextFile;
        this.reviews = new ArrayList<>();
    }

    // New Constructor with file support
    public Books(int publisherID, String bookName, double price , String category, int id, File coverImageFile, File bookTextFile)
    {
        this.bookName = bookName;
        this.bookId = id;
        this.price = price; 
        this.category = category;
        this.publisherID = publisherID;
        this.coverImageFile = coverImageFile;
        this.bookTextFile = bookTextFile;
        this.reviews = new ArrayList<>();
    }

    // --- Setters and Getters ---
    public static HashMap<Integer, Books> getAllBooksList() {
        return AllbooksList;
    }

    public static void setAllBooksList(HashMap<Integer, Books> allBooksList2) {
        AllbooksList = allBooksList2;
    }

    public static Map<Integer, ArrayList<Books>> getbooksByPublisher() {
        return booksByPublisher;
    }

    public static void setbooksByPublisher(HashMap<Integer, ArrayList<Books>> booksByPublisher2) {
        booksByPublisher = booksByPublisher2;
    }

    public ArrayList<Reviews> getreviews() {
        return reviews;
    }

    public void setreviews(ArrayList<Reviews> reviews2) {
        this.reviews = reviews2;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int id) {
        this.bookId = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPublisherID() {
        return publisherID;
    }

    // New Getters and Setters for the files
    public File getCoverImageFile() {
        return coverImageFile;
    }

    public void setCoverImageFile(File coverImageFile) {
        this.coverImageFile = coverImageFile;
    }

    public File getBookTextFile() {
        return bookTextFile;
    }

    public void setBookTextFile(File bookTextFile) {
        this.bookTextFile = bookTextFile;
    }

    public ArrayList<Reviews> getReviews() {
        return reviews;
    }

    public static void addReview(int bookID, int raterID, String review) {
        Books book = findBookByID(bookID);
        if (book == null) {
            return;
        }

        Reviews newReview = new Reviews(bookID, raterID, review);

        book.reviews.add(newReview);
        Reviews.getAllReviewsList().add(newReview);

        if (!Reviews.getReviewsofBooks().containsKey(bookID)) {
            Reviews.getReviewsofBooks().put(bookID, new ArrayList<>());
        }

        Reviews.getReviewsofBooks().get(bookID).add(newReview);
    }

    public static Books findBookByID(int id) {
        return AllbooksList.get(id);
    }

    public static Books findBookByName(String bookName) {
        for (Publisher publisher : Publisher.getPublisherList()) {
            for (Books book : publisher.getBookList()) {
                if (book.getBookName().equalsIgnoreCase(bookName)) {
                    return book;
                }
            }
        }
        return null;
    }
}

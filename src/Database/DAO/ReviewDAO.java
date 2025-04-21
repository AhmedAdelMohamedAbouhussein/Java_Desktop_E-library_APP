package Database.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import Classes.Reviews;
import Database.DBConnection;

public class ReviewDAO {

    // CREATE - Add a new review
    public static void addReview(Reviews review) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Reviews (review_id, book_id, user_id, review_text) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, review.getId());
            stmt.setInt(2, review.getBookID());
            stmt.setInt(3, review.getRaterId());
            stmt.setString(4, review.getReview());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE - Update an existing review
    public static void updateReview(int reviewId, String newText) 
    {
        try (Connection conn = DBConnection.getConnection()) 
        {
            String sql = "UPDATE Reviews SET review_text = ? WHERE review_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newText);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    // DELETE - Already implemented
    public static void deleteReview(int reviewId) 
    {
        try (Connection conn = DBConnection.getConnection()) 
        {
            String sql = "DELETE FROM Reviews WHERE review_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, reviewId);
            stmt.executeUpdate();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

        // READ: Get all reviews from the database
    public static void loadAllReviews() 
    {
        ArrayList<Reviews> reviewsList = new ArrayList<>();
        Map<Integer, ArrayList<Reviews>> ReviewsofBooks = new HashMap<>();

        try (Connection conn = DBConnection.getConnection()) 
        {
            String sql = "SELECT * FROM Reviews";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) 
            {
                // Creating a new Review object for each record
                Reviews review = new Reviews(
                    rs.getInt("book_id"),
                    rs.getInt("user_id"),
                    rs.getString("review_text")
                );
                review.setId(rs.getInt("review_id")); // If the review object has a setter for ID
                reviewsList.add(review); // Add the review to the list
                
                // Step 3: Group books by bookid
                int bookid = review.getBookID();

                // Check if the bookid already has a list in the map
                if (!ReviewsofBooks.containsKey(bookid)) 
                {
                    ReviewsofBooks.put(bookid, new ArrayList<>());
                }
                
                // Add the book to the publisher's list
                ReviewsofBooks.get(bookid).add(review);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        Reviews.setAllReviewsList(reviewsList);
        Reviews.setReviewsofBooks(ReviewsofBooks);
    }


    public static int giveID() 
    {
        Random R = new Random();

        // 1) Load all used IDs into a Set
        Set<Integer> usedIds = new HashSet<>();

        String sql = "SELECT review_id FROM Reviews";
        try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) 
            {
                usedIds.add(rs.getInt("review_id"));
            }
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }

        // 2) Generate until we find an unused one
        int potentialID;
        do 
        {
            potentialID = R.nextInt(Integer.MAX_VALUE) + 1;
        } 
        while (usedIds.contains(potentialID));

        return potentialID;
    }
}

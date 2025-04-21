package Database;

import Classes.*;
import Database.DAO.*;
public class Load_Everything 
{
    public static void load()
    { 
        AdminsDAO.loadAllAdmins(); //✅      
        CustomerDAO.loadAllCustomers(); //✅
        PublisherDAO.loadAllPublishers(); //✅
        
        BookDAO.loadAllBooks(); //✅
        
        for(Publisher p: Publisher.getPublisherList()) //✅
        {
            if (Books.getbooksByPublisher().containsKey(p.getId())) 
            {
                p.setBookList(Books.getbooksByPublisher().get(p.getId()));
            }
        }

        BorrowedBooksDAO.loadAllBorrowedBooks(); //✅
        OwnedBooksDAO.loadAllOwnedBooks(); //✅

        for(Customers c: Customers.getCustomersList()) //✅
        {
            if(BorrowedBooks.getcustomerBorrowedBooks().containsKey(c.getId()))
            {
                for(int i = 0; i < BorrowedBooks.getcustomerBorrowedBooks().get(c.getId()).size(); i++)
                {
                    if(Books.getAllBooksList().containsKey(BorrowedBooks.getcustomerBorrowedBooks().get(c.getId()).get(i)))
                    {
                        c.getBorrowedBookList().add(Books.getAllBooksList().get(i));
                    }
                }
            }
        }

        for(Customers c: Customers.getCustomersList()) //✅
        {
            if(OwnedBooks.getcustomerOwnedBooks().containsKey(c.getId()))
            {
                for(int i = 0; i < OwnedBooks.getcustomerOwnedBooks().get(c.getId()).size(); i++)
                {
                    if(Books.getAllBooksList().containsKey(OwnedBooks.getcustomerOwnedBooks().get(c.getId()).get(i)))
                    {
                        c.getBorrowedBookList().add(Books.getAllBooksList().get(i));
                    }
                }
            }
        }

        Database.DAO.ReviewDAO.loadAllReviews(); //✅

        for(Integer key: Books.getAllBooksList().keySet()) //✅
        {
            if(Reviews.getReviewsofBooks().containsKey(key));
            {
                Books.getAllBooksList().get(key).setreviews(Reviews.getReviewsofBooks().get(key));
            }
        }
    }
}

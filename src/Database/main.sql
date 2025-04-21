-- 1. USERS (Base Table)
CREATE TABLE Users (
    id INT PRIMARY KEY ,   
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL  -- "customer" or "publisher" or "admin"
);

-- 2. CUSTOMERS (inherits from Users)
CREATE TABLE Customers 
(
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES Users(id) ON DELETE CASCADE
);

-- 3. PUBLISHERS (inherits from Users)
CREATE TABLE Publishers 
(
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES Users(id) ON DELETE CASCADE
);

-- 4. ADMINS (inherits from Users)
CREATE TABLE Admins 
(
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES Users(id) ON DELETE CASCADE
);


-- 5. BOOKS
CREATE TABLE Books 
(
    book_id INT PRIMARY KEY ,
    book_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    publisher_id INT NOT NULL,
    FOREIGN KEY (publisher_id) REFERENCES Publishers(id) ON DELETE CASCADE
);

-- 6. OWNED_BOOKS (Customer owns a book)
CREATE TABLE OwnedBooks (
    customer_id INT,
    book_id INT,
    PRIMARY KEY (customer_id, book_id),
    FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE NO ACTION
);

-- 7. BORROWED_BOOKS (Customer borrows a book)
CREATE TABLE BorrowedBooks (
    customer_id INT,
    book_id INT,
    PRIMARY KEY (customer_id, book_id),
    FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE NO ACTION
);

-- 8. REVIEWS
CREATE TABLE Reviews 
(
    review_id INT PRIMARY KEY ,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    review_text TEXT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE NO ACTION
);

-- Add Admin
INSERT INTO Users (id, name, email, password, role)
VALUES (1, 'Ahmed', 'Aa5913372@gmail.com', 'password', 'admin');

INSERT INTO Admins (id)
VALUES (1);


--Add Publisher
INSERT INTO Users (id, name, email, password, role)
VALUES (2, 'Omar', 'Omar111@gmail.com', 'password123', 'publisher');

INSERT INTO Publishers (id)
VALUES (2);


--Add Customer
INSERT INTO Users (id, name, email, password, role)
VALUES (3, 'Mustafa', 'Mustafa123@gmail.com', 'password321', 'customer');


INSERT INTO Customers(id)
VALUES (3);

ALTER TABLE Books ADD cover_image VARBINARY(MAX);
ALTER TABLE Books ADD book_text VARBINARY(MAX);
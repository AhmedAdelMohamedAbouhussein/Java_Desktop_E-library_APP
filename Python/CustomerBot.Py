import tkinter as tk
from tkinter import simpledialog, messagebox, scrolledtext, Menu
import pyodbc
import datetime
import os
import re
import difflib

class AICustomerAssistant:
    def _init_(self):
        self.user_id = None
        self.user_name = "Customer"
        self.conn = self.create_connection()
        self.root = tk.Tk()
        self.root.withdraw()  # Hide until login
        self.build_login()

    def create_connection(self):
        try:
            conn = pyodbc.connect(
                'DRIVER={ODBC Driver 18 for SQL Server};'  # Update driver version here
                'SERVER=localhost;'                      # Specify your server (e.g., localhost, IP address, or server name)
                'DATABASE=E_Library_DB;'                 # Your database name
                'Trusted_Connection=yes;'                # Use Windows authentication
            )
            return conn
        except Exception as e:
            print("Database connection failed:", e)
            exit()

    def build_login(self):
        self.user_id = simpledialog.askinteger("Login", "Enter your Customer ID:")
        if self.user_id is None:
            messagebox.showinfo("Cancelled", "Login cancelled.")
            return

        if not self.validate_user(self.user_id):
            messagebox.showerror("Invalid ID", "No customer found with this ID.")
            return

        self.fetch_user_name()
        self.root.title("AI Customer Service Assistant")
        self.root.protocol("WM_DELETE_WINDOW", self.exit_program)
        self.build_gui()
        self.root.deiconify()
        self.log_interaction("System", f"Welcome {self.user_name}!")
        self.respond(f"Hello {self.user_name}, how may I assist you today?")
        self.root.mainloop()

    def fetch_user_name(self):
        try:
            cursor = self.conn.cursor()
            cursor.execute("SELECT name FROM Customers WHERE customer_id = ?", self.user_id)
            result = cursor.fetchone()
            if result:
                self.user_name = result[0]
        except:
            pass

    def validate_user(self, user_id):
        cursor = self.conn.cursor()
        cursor.execute("SELECT customer_id FROM Customers WHERE customer_id = ?", user_id)
        return cursor.fetchone() is not None

    def build_gui(self):
        self.text_area = scrolledtext.ScrolledText(self.root, wrap=tk.WORD, width=80, height=25, state='disabled')
        self.text_area.pack(padx=10, pady=10)

        entry_frame = tk.Frame(self.root)
        entry_frame.pack(pady=5, fill=tk.X)

        self.entry = tk.Entry(entry_frame, width=70)
        self.entry.pack(side=tk.LEFT, padx=(10, 5), expand=True, fill=tk.X)
        self.entry.bind("<Return>", lambda event: self.process_input())

        send_btn = tk.Button(entry_frame, text="Send", command=self.process_input)
        send_btn.pack(side=tk.LEFT, padx=(0, 10))

        self.build_menu()

    def build_menu(self):
        menubar = Menu(self.root)
        help_menu = Menu(menubar, tearoff=0)
        help_menu.add_command(label="Help", command=lambda: self.respond(self.help_text()))
        help_menu.add_command(label="About", command=lambda: self.respond("AI Assistant for E-Library v1.0"))
        help_menu.add_separator()
        help_menu.add_command(label="Exit", command=self.exit_program)
        menubar.add_cascade(label="Menu", menu=help_menu)
        self.root.config(menu=menubar)

    def process_input(self):
        user_input = self.entry.get().strip()
        if not user_input:
            return

        self.log_interaction("You", user_input)
        self.entry.delete(0, tk.END)

        response = self.handle_input(user_input)
        self.respond(response)

    def respond(self, text):
        self.log_interaction("AI", text)

    def log_interaction(self, sender, message):
        self.text_area.configure(state='normal')
        timestamp = datetime.datetime.now().strftime("[%H:%M:%S] ")
        self.text_area.insert(tk.END, f"{timestamp}{sender}: {message}\n")
        self.text_area.configure(state='disabled')
        self.text_area.yview(tk.END)

    def handle_input(self, command):
        command = command.lower()
        patterns = {
            "borrowed": self.get_borrowed_books,
            "own": self.get_owned_books,
            "available": self.get_all_books,
            "review": self.leave_review,
            "sales": self.get_sales_info,
            "publisher": self.get_publisher_info,
            "recommend": self.recommend_books,
            "wishlist": self.view_wishlist,
            "add to wishlist": self.add_to_wishlist,
            "delete review": self.delete_review,
            "edit review": self.edit_review,
            "help": self.help_text
        }

        for key, func in patterns.items():
            if re.search(key, command):
                result = func()
                return result if result else "No data available."

        return self.fuzzy_response(command)

    def fuzzy_response(self, command):
        possible_intents = [
            "what have i borrowed", "show owned books", "list books", "submit review", "show sales",
            "publisher info", "recommend a book", "wishlist", "add book to wishlist",
            "delete my review", "edit my review"
        ]
        best_match = difflib.get_close_matches(command, possible_intents, n=1, cutoff=0.5)
        if best_match:
            return self.handle_input(best_match[0])
        return "I'm sorry, I couldn't understand that. Try using keywords like 'borrowed', 'review', 'sales', etc."

    def get_borrowed_books(self):
        cursor = self.conn.cursor()
        cursor.execute("""
            SELECT B.book_name FROM BorrowedBooks BB
            JOIN Books B ON BB.book_id = B.book_id
            WHERE BB.customer_id = ?
        """, self.user_id)
        rows = cursor.fetchall()
        return "Borrowed books:\n" + "\n".join(f"- {row[0]}" for row in rows) if rows else "You haven't borrowed any books."

    def get_owned_books(self):
        cursor = self.conn.cursor()
        cursor.execute("""
            SELECT B.book_name FROM OwnedBooks OB
            JOIN Books B ON OB.book_id = B.book_id
            WHERE OB.customer_id = ?
        """, self.user_id)
        rows = cursor.fetchall()
        return "Owned books:\n" + "\n".join(f"- {row[0]}" for row in rows) if rows else "You don't own any books."

    def get_all_books(self):
        cursor = self.conn.cursor()
        cursor.execute("SELECT book_name FROM Books")
        rows = cursor.fetchall()
        return "Available books:\n" + "\n".join(f"- {row[0]}" for row in rows) if rows else "No books available."

    def leave_review(self):
        try:
            book_id = simpledialog.askinteger("Review", "Enter Book ID:")
            if book_id is None:
                return "Cancelled."

            review = simpledialog.askstring("Review", "Write your review:")
            if not review:
                return "Cancelled."

            cursor = self.conn.cursor()
            cursor.execute("SELECT MAX(review_id) FROM Reviews")
            max_id = cursor.fetchone()[0] or 0
            new_id = max_id + 1
            cursor.execute("""
                INSERT INTO Reviews (review_id, book_id, user_id, review_text)
                VALUES (?, ?, ?, ?)
            """, new_id, book_id, self.user_id, review)
            self.conn.commit()
            return "Review submitted successfully."
        except Exception as e:
            return f"Error: {str(e)}"

    def edit_review(self):
        try:
            review_id = simpledialog.askinteger("Edit Review", "Enter Review ID to edit:")
            new_text = simpledialog.askstring("Edit Review", "Enter new review:")
            cursor = self.conn.cursor()
            cursor.execute("UPDATE Reviews SET review_text = ? WHERE review_id = ? AND user_id = ?", new_text, review_id, self.user_id)
            self.conn.commit()
            return "Review updated."
        except:
            return "Error updating review."

    def delete_review(self):
        try:
            review_id = simpledialog.askinteger("Delete Review", "Enter Review ID to delete:")
            cursor = self.conn.cursor()
            cursor.execute("DELETE FROM Reviews WHERE review_id = ? AND user_id = ?", review_id, self.user_id)
            self.conn.commit()
            return "Review deleted."
        except:
            return "Error deleting review."

    def get_sales_info(self):
        cursor = self.conn.cursor()
        cursor.execute("""
            SELECT B.book_name, B.sales_count FROM Books B WHERE B.sales_count > 0
        """)
        rows = cursor.fetchall()
        return "Books with sales:\n" + "\n".join(f"- {row[0]}: {row[1]} sales" for row in rows) if rows else "No sales recorded."

    def get_publisher_info(self):
        cursor = self.conn.cursor()
        cursor.execute("""
            SELECT DISTINCT P.name, P.email FROM Publisher P
            JOIN Books B ON B.publisher_id = P.publisher_id
        """)
        rows = cursor.fetchall()
        return "Publishers:\n" + "\n".join(f"- {r[0]} (Email: {r[1]})" for r in rows) if rows else "No publisher data."

    def recommend_books(self):
        cursor = self.conn.cursor()
        cursor.execute("""
            SELECT TOP 5 book_name FROM Books ORDER BY sales_count DESC
        """)
        rows = cursor.fetchall()
        return "Top recommendations:\n" + "\n".join(f"- {r[0]}" for r in rows) if rows else "No recommendations found."

    def view_wishlist(self):
        cursor = self.conn.cursor()
        cursor.execute("""
            SELECT B.book_name FROM Wishlist W
            JOIN Books B ON W.book_id = B.book_id
            WHERE W.customer_id = ?
        """, self.user_id)
        rows = cursor.fetchall()
        return "Your wishlist:\n" + "\n".join(f"- {row[0]}" for row in rows) if rows else "Your wishlist is empty."

    def add_to_wishlist(self):
        book_id = simpledialog.askinteger("Add to Wishlist", "Enter Book ID to add:")
        cursor = self.conn.cursor()
        cursor.execute("INSERT INTO Wishlist (customer_id, book_id) VALUES (?, ?)", self.user_id, book_id)
        self.conn.commit()
        return "Book added to wishlist."

    def help_text(self):
        return (
            "You can say things like:\n"
            "- What have I borrowed?\n"
            "- Show me available books\n"
            "- I want to leave a review\n"
            "- Show me the publishers\n"
            "- Recommend me a book\n"
            "- What books did I buy?\n"
            "- Add a book to my wishlist\n"
            "- Help\n"
        )

    def exit_program(self):
        self.conn.close()
        self.root.destroy()

if __name__ == "__main__":
    AICustomerAssistant()

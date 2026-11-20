import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Library Management System (OOP)
 *
 * Classes   : Book, Library, User (abstract) -> Student, Teacher
 * Methods   : Add Book, Borrow Book, Return Book
 * OOP pillars demonstrated:
 *   - Encapsulation : private fields + public getters/methods (Book, User, Library)
 *   - Abstraction   : abstract class User with abstract methods
 *   - Inheritance   : Student and Teacher extend User
 *   - Polymorphism  : getMaxBooksAllowed() / getRole() behave differently per subclass
 */

// ======================================================================
//  BOOK  (Encapsulation: all fields private, access only via methods)
// ======================================================================
class Book {
    private final int bookId;
    private final String title;
    private final String author;
    private boolean available;

    Book(int bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    int getBookId()      { return bookId; }
    String getTitle()    { return title; }
    String getAuthor()   { return author; }
    boolean isAvailable() { return available; }

    void markBorrowed()  { this.available = false; }
    void markReturned()  { this.available = true; }

    @Override
    public String toString() {
        return String.format("[ID: %d] %-28s by %-20s | %s",
                bookId, title, author, available ? "Available" : "Issued");
    }
}

// ======================================================================
//  USER  (Abstraction: abstract class, cannot create object of it directly)
// ======================================================================
abstract class User {
    private final int userId;
    private final String name;
    private final List<Book> borrowedBooks = new ArrayList<>();

    User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    // Abstract methods: every subclass MUST give its own implementation
    abstract int getMaxBooksAllowed();
    abstract String getRole();

    int getUserId()  { return userId; }
    String getName() { return name; }

    boolean canBorrow() {
        return borrowedBooks.size() < getMaxBooksAllowed();
    }

    void addBorrowedBook(Book book)    { borrowedBooks.add(book); }
    void removeBorrowedBook(Book book) { borrowedBooks.remove(book); }

    List<Book> getBorrowedBooks() {
        // Encapsulation: outside code gets a read-only view, can't modify our list
        return Collections.unmodifiableList(borrowedBooks);
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] %s (%s) | Borrowed: %d/%d",
                userId, name, getRole(), borrowedBooks.size(), getMaxBooksAllowed());
    }
}

// ======================================================================
//  STUDENT & TEACHER  (Inheritance + Polymorphism)
// ======================================================================
class Student extends User {
    Student(int userId, String name) {
        super(userId, name);
    }

    @Override
    int getMaxBooksAllowed() { return 2; }

    @Override
    String getRole() { return "Student"; }
}

class Teacher extends User {
    Teacher(int userId, String name) {
        super(userId, name);
    }

    @Override
    int getMaxBooksAllowed() { return 5; }

    @Override
    String getRole() { return "Teacher"; }
}

// ======================================================================
//  LIBRARY  (The manager: holds books + users, does the main operations)
// ======================================================================
class Library {
    private final List<Book> books = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private int nextBookId = 101;
    private int nextUserId = 1;

    // ---------- Add Book ----------
    Book addBook(String title, String author) {
        Book book = new Book(nextBookId++, title, author);
        books.add(book);
        return book;
    }

    // ---------- Register User ----------
    User registerStudent(String name) {
        User user = new Student(nextUserId++, name);
        users.add(user);
        return user;
    }

    User registerTeacher(String name) {
        User user = new Teacher(nextUserId++, name);
        users.add(user);
        return user;
    }

    // ---------- Search helpers ----------
    private Book findBook(int bookId) {
        for (Book b : books) {
            if (b.getBookId() == bookId) {
                return b;
            }
        }
        return null;
    }

    private User findUser(int userId) {
        for (User u : users) {
            if (u.getUserId() == userId) {
                return u;
            }
        }
        return null;
    }

    // ---------- Borrow Book ----------
    void borrowBook(int userId, int bookId) {
        User user = findUser(userId);
        Book book = findBook(bookId);

        if (user == null) {
            System.out.println("User not found!");
        } else if (book == null) {
            System.out.println("Book not found!");
        } else if (!book.isAvailable()) {
            System.out.println("Sorry, this book is already issued to someone else.");
        } else if (!user.canBorrow()) {
            System.out.println(user.getName() + " (" + user.getRole()
                    + ") has reached the limit of " + user.getMaxBooksAllowed() + " books.");
        } else {
            book.markBorrowed();
            user.addBorrowedBook(book);
            System.out.println("\"" + book.getTitle() + "\" issued to " + user.getName() + ".");
        }
    }

    // ---------- Return Book ----------
    void returnBook(int userId, int bookId) {
        User user = findUser(userId);
        Book book = findBook(bookId);

        if (user == null) {
            System.out.println("User not found!");
        } else if (book == null) {
            System.out.println("Book not found!");
        } else if (!user.getBorrowedBooks().contains(book)) {
            System.out.println(user.getName() + " has not borrowed this book.");
        } else {
            book.markReturned();
            user.removeBorrowedBook(book);
            System.out.println("\"" + book.getTitle() + "\" returned by " + user.getName() + ". Thank you!");
        }
    }

    // ---------- Display ----------
    void showAllBooks() {
        System.out.println("\n----- All Books -----");
        if (books.isEmpty()) {
            System.out.println("No books in the library yet.");
        }
        for (Book b : books) {
            System.out.println(b);
        }
    }

    void showAllUsers() {
        System.out.println("\n----- All Users -----");
        if (users.isEmpty()) {
            System.out.println("No users registered yet.");
        }
        for (User u : users) {
            System.out.println(u);
        }
    }

    void showBorrowedBooks(int userId) {
        User user = findUser(userId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        System.out.println("\n----- Books borrowed by " + user.getName() + " -----");
        if (user.getBorrowedBooks().isEmpty()) {
            System.out.println("No books borrowed.");
        }
        for (Book b : user.getBorrowedBooks()) {
            System.out.println(b);
        }
    }
}

// ======================================================================
//  MAIN  (Menu-driven program)
// ======================================================================
public class LibraryApp {

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readText(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static void showMenu() {
        System.out.println("\n========== LIBRARY MENU ==========");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Register User (Student/Teacher)");
        System.out.println("4. View All Users");
        System.out.println("5. Borrow Book");
        System.out.println("6. Return Book");
        System.out.println("7. View Books Borrowed by a User");
        System.out.println("8. Exit");
        System.out.println("==================================");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library library = new Library();

        // Sample data so you can test immediately
        library.addBook("Head First Java", "Kathy Sierra");
        library.addBook("Effective Java", "Joshua Bloch");
        library.addBook("Clean Code", "Robert C. Martin");
        library.addBook("Introduction to Algorithms", "Thomas H. Cormen");
        library.registerStudent("Adarsh");
        library.registerTeacher("Prof. Sharma");

        System.out.println("=====================================");
        System.out.println("   WELCOME TO LIBRARY MANAGEMENT     ");
        System.out.println("=====================================");
        System.out.println("(Sample books and users are pre-loaded)");

        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt(sc, "Enter your choice (1-8): ");

            switch (choice) {
                case 1: {
                    String title = readText(sc, "Book title : ");
                    String author = readText(sc, "Author     : ");
                    Book b = library.addBook(title, author);
                    System.out.println("Book added with ID " + b.getBookId() + ".");
                    break;
                }
                case 2:
                    library.showAllBooks();
                    break;
                case 3: {
                    String name = readText(sc, "User name : ");
                    int type = readInt(sc, "Type (1 = Student, 2 = Teacher): ");
                    User u;
                    if (type == 1) {
                        u = library.registerStudent(name);
                    } else if (type == 2) {
                        u = library.registerTeacher(name);
                    } else {
                        System.out.println("Invalid type! User not registered.");
                        break;
                    }
                    System.out.println("Registered: " + u);
                    break;
                }
                case 4:
                    library.showAllUsers();
                    break;
                case 5: {
                    int userId = readInt(sc, "Enter User ID : ");
                    int bookId = readInt(sc, "Enter Book ID : ");
                    library.borrowBook(userId, bookId);
                    break;
                }
                case 6: {
                    int userId = readInt(sc, "Enter User ID : ");
                    int bookId = readInt(sc, "Enter Book ID : ");
                    library.returnBook(userId, bookId);
                    break;
                }
                case 7: {
                    int userId = readInt(sc, "Enter User ID : ");
                    library.showBorrowedBooks(userId);
                    break;
                }
                case 8:
                    System.out.println("Thank you for using the Library System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please select between 1 and 8.");
            }
        }

        sc.close();
    }
}
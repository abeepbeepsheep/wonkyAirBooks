package airbooks.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;

public class Interface {
    private static final Security sec = new Security("src/airbooks/csv/Secure.csv");;
    private static final Database db = new Database("src/airbooks/csv/Student.csv", "src/airbooks/csv/Books.csv",
            "src/airbooks/csv/SelfCollectStn.csv", "src/airbooks/csv/DistrictAreas.csv");;
    private static final ArrayList<Book> rentalCart = new ArrayList<>();;

    public static int login(String username, String password) {
        if (sec.login(username, password)) {
            return 0; // student login
        }
        if (username.equals("admin") && password.equals("#AB#admin123")) {
            return 1; // admin login
        }
        return -1; // invalid login
    }

    public static void logout() {
        sec.logout();
    }

    public static String convertISBN(String ISBN) {
        int[] spacings = {3, 2, 5, 2, 1};
        String result = "";
        int counter = 0;
        for (int spacing : spacings) {
            for (int j = 0; j < spacing; j++) {
                result += ISBN.charAt(counter) + "";
                counter++;
            }
            result += "-";
        }
        return result.substring(0, result.length() - 1);
    }

    public static ArrayList<Book> getCart() {
        return rentalCart;
    }

    public static double getCartSum() {
        double sum = 0;
        for (Book b : rentalCart) {
            sum += b.getDeposit();
        }
        return sum;
    }

    public static Student getCurrentStudent() {
        return db.getStudent(Security.getCurrStudentLogin());
    }

    public static Account getCurrentAccount() {
        return sec.getAccount(Security.getCurrStudentLogin());
    }

    public static ArrayList<String> getSubjectCodes() {
        return db.getPossibleSubjCodes();
    }

    public static ArrayList<Book> getBooksBySubjectCode(String subject) {
        return db.getBooklistBySubjCode(subject, rentalCart);
    }

    public static ArrayList<SelfCollectStn> getNearbySCS(String postalCode) {
        ArrayList<SelfCollectStn> result = db.getNearbySelfCollection(postalCode);
        if (result != null && result.size() == 0) {
            return null;
        }
        return result;
    }

    public static void checkout(String postal, int lockerNo) {
        getCurrentAccount().deductFromWallet(getCartSum());
        for (Book book : rentalCart) {
            getCurrentAccount().rentBook(book);
            transact(book, postal, lockerNo);
        }
        rentalCart.clear();
        db.writeBook("src/airbooks/csv/Books.csv");
        sec.writeAccount("src/airbooks/csv/Secure.csv");
    }

    public static void transact(Book b, String postal, int lockerNo) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream("src/airbooks/csv/Transaction.csv", true));
            out.printf("%s,%s,%s,%s,%s,%s%n", b.getISBN(), Security.getCurrStudentLogin(), b.getRentalPeriod(),
                    b.getRentalDate(), postal, lockerNo);
            out.close();
        } catch (IOException e) {
            return;
        }
    }

    public static ArrayList<Book> getBooks() {
        return db.getBooksDB();
    }

    public static boolean returnBook(Book book) {
        Account acc = sec.getAccount(book.getStudentID());
        acc.returnBook(book);
        String result = acc.addToWallet(book.getDeposit());
        if (result.matches("Unable.+")) return false;
        db.writeBook("src/airbooks/csv/Books.csv");
        sec.writeAccount("src/airbooks/csv/Secure.csv");
        return true;
    }

    public static Book getBookByISBN(String ISBN) {
        try {
            return db.getBook(ISBN);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
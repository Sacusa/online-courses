package library;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test suite for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

    /*
     * Note: all the tests you write here must be runnable against any
     * Library class that follows the spec.  JUnit will automatically
     * run these tests against both SmallLibrary and BigLibrary.
     */

    /**
     * Implementation classes for the Library ADT.
     * JUnit runs this test suite once for each class name in the returned array.
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name="{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[] { 
                "library.SmallLibrary", 
                "library.BigLibrary"
        }; 
    }

    /**
     * Implementation class being tested on this run of the test suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;    

    /**
     * @return a fresh instance of a Library, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Library makeLibrary() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Library) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * Testing strategy
     * ==================
     * 
     * Number of books    0, 1, >1
     * Copies of a book    1, >1
     * 
     * Exhaustive Cartesian partition of input space.
     */

    @Test
    public void testExampleTest() {
        Library library = makeLibrary();
        Book book = new Book("This Test Is Just An Example", Arrays.asList("You Should", "Replace It", "With Your Own Tests"), 1990);
        assertEquals(Collections.emptySet(), library.availableCopies(book));
    }


    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /**
     * Covers number of books = 0, 1
     *        copies of a book = 1, 3(>1)
     * and the methods checkout()
     *                 checkin()
     *                 isAvailable()
     */
    @Test
    public void testCheckinCheckout() {
        Library library = makeLibrary();
        Set<BookCopy> allCopies = new HashSet<BookCopy>();
        Set<BookCopy> availableCopies = new HashSet<BookCopy>();

        Book book = new Book("AbC", Arrays.asList("Author 1", "Author 2"), 2016);
        BookCopy bookCopy1 = library.buy(book);

        // add a single copy of a book and verify
        allCopies.add(bookCopy1);
        availableCopies.add(bookCopy1);

        assertTrue(library.isAvailable(bookCopy1));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        // checkout the only copy and verify
        library.checkout(bookCopy1);
        availableCopies.remove(bookCopy1);

        assertFalse(library.isAvailable(bookCopy1));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        // checkin the only copy and verify
        library.checkin(bookCopy1);
        availableCopies.add(bookCopy1);

        assertTrue(library.isAvailable(bookCopy1));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        BookCopy bookCopy2 = library.buy(book);
        BookCopy bookCopy3 = library.buy(book);

        // add 2 new copies of the only book and verify
        allCopies.add(bookCopy2);
        allCopies.add(bookCopy3);
        availableCopies.add(bookCopy2);
        availableCopies.add(bookCopy3);

        assertTrue(library.isAvailable(bookCopy2));
        assertTrue(library.isAvailable(bookCopy3));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        // checkout all copies of the book one by one and verify

        // all copies remaining
        library.checkout(bookCopy1);
        availableCopies.remove(bookCopy1);

        assertFalse(library.isAvailable(bookCopy1));
        assertTrue(library.isAvailable(bookCopy2));
        assertTrue(library.isAvailable(bookCopy3));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        // (2/3) copies remaining
        library.checkout(bookCopy2);
        availableCopies.remove(bookCopy2);

        assertFalse(library.isAvailable(bookCopy1));
        assertFalse(library.isAvailable(bookCopy2));
        assertTrue(library.isAvailable(bookCopy3));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        // (1/3) copies remaining
        library.checkout(bookCopy3);
        availableCopies.remove(bookCopy3);

        assertFalse(library.isAvailable(bookCopy1));
        assertFalse(library.isAvailable(bookCopy2));
        assertFalse(library.isAvailable(bookCopy3));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        // checkin all copies of the book one by one and verify

        // 0 copies present
        library.checkin(bookCopy1);
        availableCopies.add(bookCopy1);

        assertTrue(library.isAvailable(bookCopy1));
        assertFalse(library.isAvailable(bookCopy2));
        assertFalse(library.isAvailable(bookCopy3));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        // 1 copy present
        library.checkin(bookCopy2);
        availableCopies.add(bookCopy2);

        assertTrue(library.isAvailable(bookCopy1));
        assertTrue(library.isAvailable(bookCopy2));
        assertFalse(library.isAvailable(bookCopy3));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);

        // 2 copies present
        library.checkin(bookCopy3);
        availableCopies.add(bookCopy3);

        assertTrue(library.isAvailable(bookCopy1));
        assertTrue(library.isAvailable(bookCopy2));
        assertTrue(library.isAvailable(bookCopy3));
        assertEquals(library.allCopies(book), allCopies);
        assertEquals(library.availableCopies(book), availableCopies);
    }

    /**
     * Covers number of books = 3(>1)
     *        copies of a book = 1
     * and the methods isAvailable()
     *                 lose()
     */
    @Test
    public void testIsAvailableLose() {
        Library library = makeLibrary();

        Book book1 = new Book("AbC", Arrays.asList("Author 1"), 2016);
        Book book2 = new Book("pQr", Arrays.asList("Author 1", "Author 2"), 1916);
        Book book3 = new Book("XyZ", Arrays.asList("Author 1", "Author 2", "Author 3"), 1966);

        Set<BookCopy> book1AllCopies = new HashSet<BookCopy>();
        Set<BookCopy> book2AllCopies = new HashSet<BookCopy>();
        Set<BookCopy> book3AllCopies = new HashSet<BookCopy>();
        Set<BookCopy> book1AvailableCopies = new HashSet<BookCopy>();
        Set<BookCopy> book2AvailableCopies = new HashSet<BookCopy>();
        Set<BookCopy> book3AvailableCopies = new HashSet<BookCopy>();

        // add a copy of all three books and verify
        BookCopy book1Copy = library.buy(book1);
        BookCopy book2Copy = library.buy(book2);
        BookCopy book3Copy = library.buy(book3);

        book1AllCopies.add(book1Copy);
        book2AllCopies.add(book2Copy);
        book3AllCopies.add(book3Copy);

        book1AvailableCopies.add(book1Copy);
        book2AvailableCopies.add(book2Copy);
        book3AvailableCopies.add(book3Copy);

        assertTrue(library.isAvailable(book1Copy));
        assertTrue(library.isAvailable(book2Copy));
        assertTrue(library.isAvailable(book3Copy));
        assertEquals(library.allCopies(book1), book1AllCopies);
        assertEquals(library.allCopies(book2), book2AllCopies);
        assertEquals(library.allCopies(book3), book3AllCopies);
        assertEquals(library.availableCopies(book1), book1AvailableCopies);
        assertEquals(library.availableCopies(book2), book2AvailableCopies);
        assertEquals(library.availableCopies(book3), book3AvailableCopies);

        // lose all the books one by one and verify

        // remove one book copy (2/3 remaining)
        library.lose(book1Copy);
        book1AllCopies.remove(book1Copy);
        book1AvailableCopies.remove(book1Copy);

        assertFalse(library.isAvailable(book1Copy));
        assertTrue(library.isAvailable(book2Copy));
        assertTrue(library.isAvailable(book3Copy));
        assertEquals(library.allCopies(book1), book1AllCopies);
        assertEquals(library.allCopies(book2), book2AllCopies);
        assertEquals(library.allCopies(book3), book3AllCopies);
        assertEquals(library.availableCopies(book1), book1AvailableCopies);
        assertEquals(library.availableCopies(book2), book2AvailableCopies);
        assertEquals(library.availableCopies(book3), book3AvailableCopies);

        // remove one book copy (1/3 remaining)
        library.lose(book2Copy);
        book2AllCopies.remove(book2Copy);
        book2AvailableCopies.remove(book2Copy);

        assertFalse(library.isAvailable(book1Copy));
        assertFalse(library.isAvailable(book2Copy));
        assertTrue(library.isAvailable(book3Copy));
        assertEquals(library.allCopies(book1), book1AllCopies);
        assertEquals(library.allCopies(book2), book2AllCopies);
        assertEquals(library.allCopies(book3), book3AllCopies);
        assertEquals(library.availableCopies(book1), book1AvailableCopies);
        assertEquals(library.availableCopies(book2), book2AvailableCopies);
        assertEquals(library.availableCopies(book3), book3AvailableCopies);

        // remove one book copy (0/3 remaining)
        library.lose(book3Copy);
        book3AllCopies.remove(book3Copy);
        book3AvailableCopies.remove(book3Copy);

        assertFalse(library.isAvailable(book1Copy));
        assertFalse(library.isAvailable(book2Copy));
        assertFalse(library.isAvailable(book3Copy));
        assertEquals(library.allCopies(book1), book1AllCopies);
        assertEquals(library.allCopies(book2), book2AllCopies);
        assertEquals(library.allCopies(book3), book3AllCopies);
        assertEquals(library.availableCopies(book1), book1AvailableCopies);
        assertEquals(library.availableCopies(book2), book2AvailableCopies);
        assertEquals(library.availableCopies(book3), book3AvailableCopies);
    }

    /**
     * Covers number of books = 5(>1)
     *        copies of a book = 1, 3(>1)
     * and the method find()
     * 
     * Assumes find() to be case-sensitive.
     */
    @Test
    public void testFind() {
        Library library = makeLibrary();
        List<Book> availableBooksList = new ArrayList<Book>();
        Set<Book> availableBooksSet = new HashSet<Book>();
        Set<Book> booksSet;

        Book book1 = new Book("AbC", Arrays.asList("Author 1"), 2016);
        Book book2 = new Book("AbC", Arrays.asList("Author 1"), 2013);
        Book book3 = new Book("pQr", Arrays.asList("Author 1", "Author 2"), 1916);
        Book book4 = new Book("XyZ", Arrays.asList("Author 1", "Author 2", "Author 3"), 1916);
        Book book5 = new Book("XyZ", Arrays.asList("Author 1", "Author 2", "Author 3"), 1966);

        // checkout only copy of book1
        library.checkout(library.buy(book1));

        // add three copies each of book2, book3, book4, book5
        for (int i = 0; i < 3; ++i) {
            library.buy(book2);
            library.buy(book4);

            // checkout 1 copy of book3
            if (i <= 0) {
                library.checkout(library.buy(book3));
            }
            else {
                library.buy(book3);
            }

            // checkout all copies of book 5
            library.checkout(library.buy(book5));
        }

        // search for Author 1; order not verified
        availableBooksSet.add(book1);
        availableBooksSet.add(book2);
        availableBooksSet.add(book3);
        availableBooksSet.add(book4);
        availableBooksSet.add(book5);

        booksSet = new HashSet<Book>(library.find("Author 1"));

        //assertEquals(booksSet, availableBooksSet);
        assertEquals(availableBooksSet, booksSet);

        // search for Author 2; order not verified
        availableBooksSet.clear();
        availableBooksSet.add(book3);
        availableBooksSet.add(book4);
        availableBooksSet.add(book5);

        booksSet = new HashSet<Book>(library.find("Author 2"));

        assertEquals(booksSet, availableBooksSet);

        // search for Author 3; order not verified
        availableBooksSet.clear();
        availableBooksSet.add(book4);
        availableBooksSet.add(book5);

        booksSet = new HashSet<Book>(library.find("Author 3"));

        assertEquals(booksSet, availableBooksSet);

        // search for AbC; order verified
        availableBooksList.add(book1);
        availableBooksList.add(book2);

        assertEquals(availableBooksList, library.find("AbC"));

        // search for XyZ; order verified
        availableBooksList.clear();
        availableBooksList.add(book5);
        availableBooksList.add(book4);

        assertEquals(library.find("XyZ"), availableBooksList);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

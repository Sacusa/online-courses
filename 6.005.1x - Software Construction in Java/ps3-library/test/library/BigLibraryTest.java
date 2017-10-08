package library;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Test suite for BigLibrary's stronger specs.
 */
public class BigLibraryTest {

    /* 
     * NOTE: use this file only for tests of BigLibrary.find()'s stronger spec.
     * Tests of all other Library operations should be in LibraryTest.java 
     */

    /*
     * Testing strategy
     * ==================
     * 
     * query.length 0, 1, >1
     * 
     * Exhaustive Cartesian partition of input space.
     */

    @Test
    public void testExampleTest() {
        // this is just an example test, you should delete it
        Library library = new BigLibrary();
        assertEquals(Collections.emptyList(), library.find("This Test Is Just An Example"));
    }

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // book declarations
    Book book1 = new Book("Title 1", Arrays.asList("Author 12"), 1940);
    Book book2 = new Book("Title 2", Arrays.asList("Author 1", "Author 2"), 1920);
    Book book3 = new Book("Title 3", Arrays.asList("Author 12", "Author 2", "Author 3"), 1900);
    Book book4 = new Book("Title 4", Arrays.asList("Writer 1"), 1960);
    Book book5 = new Book("Title 5", Arrays.asList("Writer 1", "Writer 2"), 1980);

    // function to create and return a new BigLibrary with five books
    private BigLibrary createLibrary() {
        BigLibrary library = new BigLibrary();

        library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);
        library.buy(book5);

        return library;
    }

    /**
     * Covers query.length() = 0
     */
    @Test
    public void testEmptyQuery() {
        BigLibrary library = createLibrary();
        List<Book> answer = new ArrayList<Book>();

        assertEquals(answer, library.find(""));
    }

    /**
     * Covers query.length() > 1
     */
    @Test
    public void testReturnSizeThree() {
        BigLibrary library = createLibrary();
        List<Book> answer = new ArrayList<Book>();

        answer.add(book2);
        answer.add(book1);
        answer.add(book3);

        assertEquals(answer, library.find("Author 1"));
    }

    /**
     * Covers query.length = 1
     */
    @Test
    public void testReturnSizeFive() {
        BigLibrary library = createLibrary();
        List<Book> answer = new ArrayList<Book>();

        answer.add(book5);
        answer.add(book4);
        answer.add(book1);
        answer.add(book2);
        answer.add(book3);

        assertEquals(answer, library.find("T"));
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

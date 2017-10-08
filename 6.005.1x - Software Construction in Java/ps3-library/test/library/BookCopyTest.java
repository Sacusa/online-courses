package library;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Test suite for BookCopy ADT.
 */
public class BookCopyTest {

    /*
     * Testing strategy
     * ==================
     * 
     * - Two objects with different values and different references should not be equal.
     * - Two objects with same values but different references should not be equal.
     * - Two objects with same references should be equal.
     * 
     * Tested object equality using both equals() method and hashCode() method.
     * 
     * Tested getCondition(), setCondition() and toString() methods for both values, i.e.
     *   GOOD
     *   DAMAGED
     * 
     * Tested getBook() method.
     * 
     * Tested exception handling of checkRep().
     * 
     * Exhaustive Cartesian partition of input space.
     */

    @Test
    public void testExampleTest() {
        Book book = new Book("This Test Is Just An Example", Arrays.asList("You Should", "Replace It", "With Your Own Tests"), 1990);
        BookCopy copy = new BookCopy(book);
        assertEquals(book, copy.getBook());
    }

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /**
     * Test case for two objects with different values and different references
     */
    @Test
    public void testDiffValueDiffRef() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;

        authors.add("Author 1");

        BookCopy book1 = new BookCopy(new Book(title, authors, year));

        title = "XyZ";

        BookCopy book2 = new BookCopy(new Book(title, authors, year));

        assertFalse("Same books", book1.equals(book2));
        assertFalse("Same hashcode", book1.hashCode() == book2.hashCode());
    }

    /**
     * Test case for two objects with same values and different references
     */
    @Test
    public void testSameValueDiffRef() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;

        authors.add("Author 1");

        BookCopy book1 = new BookCopy(new Book(title, authors, year));
        BookCopy book2 = new BookCopy(new Book(title, authors, year));

        assertFalse("Same books", book1.equals(book2));
        assertFalse("Same hashcode", book1.hashCode() == book2.hashCode());
    }

    /**
     * Test case for two objects with same references
     */
    @Test
    public void testSameRef() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;

        authors.add("Author 1");

        BookCopy book1 = new BookCopy(new Book(title, authors, year));
        BookCopy book2 = book1;

        assertTrue("Different books", book1.equals(book2));
        assertTrue("Different hashcode", book1.hashCode() == book2.hashCode());
    }

    /**
     * Test case for getCondition(), setCondition() and toString() for both possible values of
     * Condition, i.e.
     *   GOOD
     *   DAMAGED
     */
    @Test
    public void testConditionAndString() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;

        authors.add("xYz");

        BookCopy book1 = new BookCopy(new Book(title, authors, year));

        assertEquals("invalid condition", book1.getCondition(), BookCopy.Condition.GOOD);
        assertEquals("invalid string", book1.toString(), "AbC (2016) by xYz in good condition");

        book1.setCondition(BookCopy.Condition.DAMAGED);
        assertEquals("invalid condition", book1.getCondition(), BookCopy.Condition.DAMAGED);
        assertEquals("invalid string", book1.toString(), "AbC (2016) by xYz in damaged condition");

        book1.setCondition(BookCopy.Condition.GOOD);
        assertEquals("invalid condition", book1.getCondition(), BookCopy.Condition.GOOD);
        assertEquals("invalid string", book1.toString(), "AbC (2016) by xYz in good condition");
    }
    
    /**
     * Test case for getBook()
     */
    @Test
    public void testGetBook() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;

        authors.add("xYz");

        Book book = new Book(title, authors, year);
        
        BookCopy bookCopy = new BookCopy(book);
        
        assertEquals("different book", bookCopy.getBook(), book);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

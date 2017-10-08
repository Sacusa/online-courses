package library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for Book ADT.
 */
public class BookTest {

    /*
     * Testing strategy
     * ==================
     * 
     * The input space has been partitioned as follows:
     * authors.size() 1, >1
     * 
     * - Test for safety from rep exposure
     * - Two book objects having same title, authors and year should be equal.
     * - Two book objects having same title and authors, different year should not be equal
     * - Two book objects having same authors and year, different title should not be equal
     * - Two book objects having same year and title, different authors should not be equal
     * - Two book objects having same title, authors and year, but different order and case should
     *   not be equal.
     * 
     * Tested object equality using both equals() method and hashCode() method.
     * Tested exception handling of checkRep().
     * Tested toString() and equals() methods.
     * 
     * Exhaustive Cartesian partition of input space.
     */
    
    @Test
    public void testExampleTest() {
        Book book = new Book("This Test Is Just An Example", Arrays.asList("You Should", "Replace It", "With Your Own Tests"), 1990);
        assertEquals("This Test Is Just An Example", book.getTitle());
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * Test for authors.size() = 1, 5(>1).
     * Test for safety from rep exposure.
     * Test for two book objects having same year and title, different authors.
     */
    @Test
    public void testRepExposure() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("Author 1");
        
        Book initialBook = new Book(title, authors, year);
        
        assertEquals("initialBook: invalid title", initialBook.getTitle(), title);
        assertTrue("initialBook: invalid authors", initialBook.getAuthors().equals(authors));
        assertEquals("initialBook: invalid year", initialBook.getYear(), year);
        
        authors.add("Author 2");
        authors.add("Author 3");
        authors.add("Author 4");
        authors.add("Author 5");
        
        Book newBook = new Book(title, authors, year);
        
        assertEquals("newBook: invalid title", newBook.getTitle(), title);
        assertTrue("newBook: invalid authors", newBook.getAuthors().equals(authors));
        assertEquals("newBook: invalid year", newBook.getYear(), year);
        
        assertFalse("Books not different.", initialBook.equals(newBook));
        assertFalse("Same hashcodes", initialBook.hashCode() == newBook.hashCode());
    }
    
    /**
     * Test for two book objects having same title, authors and year.
     */
    @Test
    public void testManyAuthorsEqualObjects() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("Author 1");
        authors.add("Author 2");
        authors.add("Author 3");
        authors.add("Author 4");
        authors.add("Author 5");
        
        Book book1 = new Book(title, authors, year);
        Book book2 = new Book(title, authors, year);
        
        assertTrue("Books not equal.", book1.equals(book2));
        assertEquals("Different hashcodes", book1.hashCode(), book2.hashCode());
    }
    
    /**
     * Test case for two book objects having same title and authors, different year
     */
    @Test
    public void testDifferentYear() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("xYz");
        
        Book book1 = new Book(title, authors, year);
        
        year = 1988;
        
        Book book2 = new Book(title, authors, year);
        
        assertFalse(book1.equals(book2));
        assertFalse("Same hashcodes", book1.hashCode() == book2.hashCode());
    }
    
    /**
     * Test case for two book objects having same authors and year, different title
     */
    @Test
    public void testDifferentTitle() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("XyZ");
        
        Book book1 = new Book(title, authors, year);
        
        title = "xYz";
        
        Book book2 = new Book(title, authors, year);
        
        assertFalse(book1.equals(book2));
        assertFalse("Same hashcodes", book1.hashCode() == book2.hashCode());
    }
    
    /**
     * Two book objects having same title, authors and year, but different order and case
     */
    @Test
    public void testDifferentCaseAndOrdering() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("Author 1");
        authors.add("Author 2");
        authors.add("Author 3");
        authors.add("Author 4");
        authors.add("Author 5");
        
        Book book1 = new Book(title, authors, year);
        
        title = "aBc";
        authors.clear();
        authors.add("Author 5");
        authors.add("Author 4");
        authors.add("Author 3");
        authors.add("Author 2");
        authors.add("Author 1");
        
        Book book2 = new Book(title, authors, year);
        
        assertFalse(book1.equals(book2));
        assertFalse("Same hashcodes", book1.hashCode() == book2.hashCode());
    }
    
    /**
     * Test case for title verification of checkRep().
     */
    @Test(expected=RuntimeException.class)
    public void testVerifiesTitle() {
        String title = "";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("xYz");
        
        @SuppressWarnings("unused")
        Book book = new Book(title, authors, year);
    }
    
    /**
     * Test case for authors verification of checkRep(), where
     * authors.size() = 0
     */
    @Test(expected=RuntimeException.class)
    public void testVerifiesAuthorsZero() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        @SuppressWarnings("unused")
        Book book = new Book(title, authors, year);
    }
    
    /**
     * Test case for authors verification of checkRep(), where
     * authors.size() = 1
     */
    @Test(expected=RuntimeException.class)
    public void testVerifiesAuthorsOne() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("");
        
        @SuppressWarnings("unused")
        Book book = new Book(title, authors, year);
    }
    
    /**
     * Test case for year verification of checkRep().
     */
    @Test(expected=RuntimeException.class)
    public void testVerifiesYear() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = -2016;
        
        authors.add("xYz");
        
        @SuppressWarnings("unused")
        Book book = new Book(title, authors, year);
    }
    
    /**
     * Test case for invalid object handling of equals().
     */
    @Test
    public void testEqualsInvalidObject() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("xYz");
        
        Book book = new Book(title, authors, year);
        String invalidObject = "This is a string!";
        
        assertFalse(book.equals(invalidObject));
    }
    
    /**
     * Test case for toString(), where
     * authors.size() = 1
     */
    @Test
    public void testToStringOneAuthor() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2016;
        
        authors.add("xYz");
        
        Book book = new Book(title, authors, year);
        String string = "AbC (2016) by xYz";
        
        assertEquals(book.toString(), string);
    }
    
    /**
     * Test case for toString(), where
     * authors.size() = 5(>1)
     */
    @Test
    public void testToStringManyAuthors() {
        String title = "AbC";
        List<String> authors = new ArrayList<String>();
        int year = 2013;
        
        authors.add("Author 1");
        authors.add("Author 2");
        authors.add("Author 3");
        authors.add("Author 4");
        authors.add("Author 5");
        
        Book book = new Book(title, authors, year);
        String string = "AbC (2013) by Author 1, Author 2, Author 3, Author 4, Author 5";
        
        assertEquals(book.toString(), string);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

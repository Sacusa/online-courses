package library.beta;

import library.Book;

import java.util.List;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Beta grading tests for Book ADT.
 */
public class BookTest {

    /*
     * Testing strategy
     * ==================
     * 
     * Book(), getTitle(), getAuthors(), getYear(), toString():
     *    title: length=1, length>1
     *    words in title: 1, >1
     *
     *    authors: length=1, length>1
     *    author name: length=1, length>1
     *    words in author name: 1, >1
     *
     *    year: 1, >1
     *    
     * 
     * equals()/hashCode():
     *     same object, different objects but same value, different objects/different value
     *     title same, title differs only in case, title different
     *     author list same, author list has extra author
     *     year same, year different
     *     no fields different, only one field different, multiple fields different
     */
    
    @Test
    public void testNormalBook() {
        Book book = new Book("Gone with the Wind", Arrays.asList("Ellen Robinson", "Joe Don Baker"), 1995);

        assertEquals("Gone with the Wind", book.getTitle());
        assertEquals(Arrays.asList("Ellen Robinson", "Joe Don Baker"), book.getAuthors());
        assertEquals(1995, book.getYear());

        assertTrue(book.toString().contains("Gone with the Wind"));
        assertTrue(book.toString().contains("Ellen Robinson"));
        assertTrue(book.toString().contains("Joe Don Baker"));
        assertTrue(book.toString().contains("1995"));
    }
    
    @Test
    public void testMinimalBook() {
        Book book = new Book("M", Arrays.asList("C"), 1);
        assertEquals("M", book.getTitle());
        assertEquals(Arrays.asList("C"), book.getAuthors());
        assertEquals(1, book.getYear());
        
        assertTrue(book.toString().contains("M"));
        assertTrue(book.toString().contains("C"));
        assertTrue(book.toString().contains("1"));
    }
    
    @Test
    public void testEqualsHashCode() {
        Book bookA = new Book("Eragon", Arrays.asList("Christopher Paolini"), 2001);
        Book bookB = new Book("Eragon", Arrays.asList("Christopher Paolini"), 2001);
        
        // reflexive
        assertTrue(bookA.equals(bookA));
        assertEquals(bookA.hashCode(), bookA.hashCode());

        // different objects/same value
        assertTrue(bookA.equals(bookB));
        assertEquals(bookA.hashCode(), bookB.hashCode());

        // symmetric
        assertTrue(bookB.equals(bookA));
        
        // different values, different in all fields
        assertFalse(bookA.equals(new Book("Twilight", Arrays.asList("Stephanie Meyer"), 2005)));
        
        // different values, different in only one field
        assertFalse(bookA.equals(new Book("Dragon", Arrays.asList("Christopher Paolini"), 2001)));
        assertFalse(bookA.equals(new Book("Eragon", Arrays.asList("Chris Paolini"), 2001)));
        assertFalse(bookA.equals(new Book("Eragon", Arrays.asList("Christopher Paolini"), 2002)));
    }
    

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

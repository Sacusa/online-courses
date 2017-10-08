package library.beta;

import library.Book;
import library.BookCopy;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * Beta grading tests for BookCopy ADT.
 */
public class BookCopyTest {

    /*
     * Testing strategy
     * ==================
     * 
     * book: no important partitions
     * condition: good, damaged
     * 
     * equals()/hashCode():
     *     same object, different objects/same fields, different objects/different fields
     */
    
    @Test
    public void testGetBook() {
        Book book = new Book("Fellowship of the Ring", Arrays.asList("J.R.R. Tolkien"), 1954);
        BookCopy copy = new BookCopy(book);
        assertEquals(book, copy.getBook());
        
        assertTrue(copy.toString().contains(book.toString()));
    }
    
    @Test
    public void testCondition() {
        Book book = new Book("The Hobbit", Arrays.asList("J.R.R. Tolkien"), 1937);
        BookCopy copy = new BookCopy(book);
        assertEquals(BookCopy.Condition.GOOD, copy.getCondition());
        assertTrue("BookCopy.toString() for a GOOD book must contain the word 'good'", copy.toString().contains("good"));

        // damage the book
        copy.setCondition(BookCopy.Condition.DAMAGED);
        assertEquals(BookCopy.Condition.DAMAGED, copy.getCondition());
        assertTrue("BookCopy.toString() for a DAMAGED book must contain the word 'damaged'", copy.toString().contains("damaged"));

        // repair it
        copy.setCondition(BookCopy.Condition.GOOD);
        assertEquals(BookCopy.Condition.GOOD, copy.getCondition());
    }
    
    @Test
    public void testEqualsHashcode() {
        Book book = new Book("The Two Towers", Arrays.asList("J.R.R. Tolkien"), 1954);
        BookCopy copyA = new BookCopy(book);
        BookCopy copyB = new BookCopy(book);
        BookCopy copyC = new BookCopy(new Book("Return of the King", Arrays.asList("J.R.R. Tolkien"), 1955));
        
        // reflexive
        assertTrue(copyA.equals(copyA));
        assertEquals(copyA.hashCode(), copyA.hashCode());
        
        // different copies, same book
        assertFalse(copyA.equals(copyB));
        
        // different copies, different books
        assertFalse(copyA.equals(copyC));
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

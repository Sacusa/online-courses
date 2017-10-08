package library.staff;

import library.Book;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Final grading tests for Book ADT.
 */
public class BookTest {

    @Test
    public void testRepExposureInGetAuthors() {
        Book book = new Book("Gone with the Wind", Arrays.asList("Ellen Robinson", "Joe Don Baker"), 1995);

        assertEquals(Arrays.asList("Ellen Robinson", "Joe Don Baker"), book.getAuthors());

        // mutating the returned list shouldn't change the immutable Book
        try {
            book.getAuthors().remove(0);
        } catch (Throwable t) {
            // remove() may throw an exception if authors list rejects mutation            
        }
        assertEquals(Arrays.asList("Ellen Robinson", "Joe Don Baker"), book.getAuthors());
    }
    
    @Test
    public void testRepExposureInConstructor() {
        List<String> authors = new ArrayList<>();
        authors.add("Joe Don Baker");
        Book book = new Book("Gone with the Wind", authors, 1995);
        assertEquals(Arrays.asList("Joe Don Baker"), book.getAuthors());

        // mutating authors shouldn't change the immutable Book
        authors.add("Ellen Robinson");
        assertEquals(Arrays.asList("Joe Don Baker"), book.getAuthors());
    }
    

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

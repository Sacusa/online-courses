package library.beta;

import library.Book;
import library.BookCopy;
import library.Library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Beta grading tests for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

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
     * ================
     * 
     * buy:
     *   doesn't have the book, already has the book
     * checkout, checkin:
     *   library has 1 copy of the book or >1 copy
     * isAvailable:
     *   library doesn't own the copy; copy owned but not available; copy owned and available
     * allCopies, availableCopies:
     *   0 owned copies; 1 owned and available; 1 owned and checked out; >1 owned and 1 available; >1 owned, >1 available
     * find:
     *   exact match to author
     *   exact match to title
     *   library has 0 total books, 1 total book, >1 total books
     *   query matches 0 books, 1 book, >1 books
     *   library has 1 copy of matching book, >1 copy of matching book
     *   all matched books have different title or author; >1 matched books with same title/author
     * lose:
     *   lost copy was available; lost copy was checked out
     *   library has 1 copy of the book or >1 copy
     * equals/hashCode:
     *   same object; different objects same collection; different objects different collection
     */
    
    @Test
    public void testBuy() {
        Library library = makeLibrary();
        Book book = new Book("Moby Dick", Arrays.asList("Herman Melville"), 1851);

        // buy a book the library doesn't already have
        BookCopy copy1 = library.buy(book);
        assertEquals(book, copy1.getBook());
        assertTrue(library.isAvailable(copy1));

        // buy a book the library already has
        BookCopy copy2 = library.buy(book);
        assertEquals(book, copy2.getBook());
        assertTrue(copy1 != copy2); // has to be fresh
        assertTrue(library.isAvailable(copy2));
    }
    
    @Test
    public void testCheckinCheckout() {
        Library library = makeLibrary();
        Book book = new Book("Bartleby, the Scrivener", Arrays.asList("Herman Melville"), 1853);

        // buy one copy of the book and check it out, then back in
        BookCopy copy1 = library.buy(book);
        library.checkout(copy1);
        assertFalse(library.isAvailable(copy1));
        library.checkin(copy1);
        assertTrue(library.isAvailable(copy1));

        // do the same with a second copy of the book
        BookCopy copy2 = library.buy(book);
        library.checkout(copy2);
        assertTrue(library.isAvailable(copy1));
        assertFalse(library.isAvailable(copy2));
        library.checkin(copy2);
        assertTrue(library.isAvailable(copy1));
        assertTrue(library.isAvailable(copy2));
    }
    
    @Test
    public void testIsAvailable() {
        Library library = makeLibrary();
        Book book = new Book("Pride and Prejudice", Arrays.asList("Jane Austen"), 1813);

        // copy not even owned by library
        assertFalse(library.isAvailable(new BookCopy(book)));
        
        // copy available
        BookCopy copy = library.buy(book);
        assertTrue(library.isAvailable(copy));
        
        // copy checked out
        library.checkout(copy);
        assertFalse(library.isAvailable(copy));
        
        // copy checked back in
        library.checkin(copy);
        assertTrue(library.isAvailable(copy));
    }
    
    @Test
    public void testAllCopiesAvailableCopies() {
        Library library = makeLibrary();
        Book book = new Book("Sense and Sensibility", Arrays.asList("Jane Austen"), 1811);
        
        // no copies owned
        Set<BookCopy> noCopies = Collections.emptySet();
        assertEquals(noCopies, library.allCopies(book));
        assertEquals(noCopies, library.availableCopies(book));

        // 1 copy owned, available
        BookCopy copy1 = library.buy(book);
        Set<BookCopy> justCopy1 = new HashSet<>(Arrays.asList(copy1));
        assertEquals(justCopy1, library.allCopies(book));
        assertEquals(justCopy1, library.availableCopies(book));

        // 1 copy owned, checked out
        library.checkout(copy1);
        assertEquals(justCopy1, library.allCopies(book));
        assertEquals(noCopies, library.availableCopies(book));
        
        // >1 copy owned, >1 available
        library.checkin(copy1);
        BookCopy copy2 = library.buy(book);
        Set<BookCopy> copy1and2 = new HashSet<>(Arrays.asList(copy1, copy2));
        assertEquals(copy1and2, library.allCopies(book));
        assertEquals(copy1and2, library.availableCopies(book));
        
        // >1 copy owned, 1 available
        library.checkout(copy2);
        assertEquals(copy1and2, library.allCopies(book));
        assertEquals(justCopy1, library.availableCopies(book));        
    }
    
    @Test
    public void testFind() {
        Library library = makeLibrary();
        Book book1 = new Book("Ulysses", Arrays.asList("James Joyce"), 1922);
        Book book2 = new Book("Infinite Jest", Arrays.asList("David Foster Wallace"), 1996);
        Book book3 = new Book("Consider the Lobster and Other Essays", Arrays.asList("David Foster Wallace"), 2005);
        
        // empty library, no matches
        assertEquals(0, library.find("Ulysses").size());
        
        // one-book library, one match, one copy of match, title search
        library.buy(book1);
        assertEquals(Arrays.asList(book1), library.find("Ulysses"));
        
        // >1 book library, >1 match, >1 copy of each match, author search
        library.buy(book2);
        library.buy(book2);
        library.buy(book3);
        List<Book> result = library.find("David Foster Wallace");
        assertEquals(2, result.size());
        assertEquals(new HashSet<>(Arrays.asList(book2, book3)), new HashSet<>(result));
        
        // 4 matched books with same title/author but different dates must return in decreasing date order
        Book book4 = new Book("Ulysses", Arrays.asList("James Joyce"), 1942);
        Book book5 = new Book("Ulysses", Arrays.asList("James Joyce"), 1965);
        Book book6 = new Book("Ulysses", Arrays.asList("James Joyce"), 2008);
        library.buy(book6);
        library.buy(book4);
        library.buy(book5);
        assertEquals(Arrays.asList(book6, book5, book4, book1), library.find("Ulysses"));
    }

    @Test
    public void testLose() {
        Library library = makeLibrary();
        Book book = new Book("Midshipman Hornblower", Arrays.asList("C.S. Forester"), 1950);
        
        BookCopy copy1 = library.buy(book);
        BookCopy copy2 = library.buy(book);
        
        // lost copy was checked out, >1 copies of book
        library.checkout(copy2);
        library.lose(copy2);
        assertEquals(new HashSet<>(Arrays.asList(copy1)), library.allCopies(book)); 
        
        // lost copy was available, 1 copy of book
        library.lose(copy1);
        assertFalse(library.isAvailable(copy1));        
        assertTrue(library.allCopies(book).isEmpty()); 
    }    
    
    @Test
    public void testEqualsHashcode() {
        Library libraryA = makeLibrary();
        Library libraryB = makeLibrary();
        Library libraryC = makeLibrary();
        libraryC.buy(new Book("The Happy Return", Arrays.asList("C.S. Forester"), 1937));
        
        // reflexive
        assertTrue(libraryA.equals(libraryA));
        assertEquals(libraryA.hashCode(), libraryA.hashCode());
        
        // different libraries, same contents (empty)
        assertFalse(libraryA.equals(libraryB));
        
        // different libraries, different contents
        assertFalse(libraryA.equals(libraryC));
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

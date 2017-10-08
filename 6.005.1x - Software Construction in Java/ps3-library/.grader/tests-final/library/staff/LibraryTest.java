package library.staff;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import library.Book;
import library.BookCopy;
import library.Library;

/**
 * Test suite for Library ADT.
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
    
    @Test
    public void testDifferentObjectsRepresentingSameBook() {
        Library library = makeLibrary();
        // new Book objects with same abstract value should be treated the same 
        // as far as Library operations are concerned
        library.buy(new Book("Of Mice and Men", Arrays.asList("John Steinbeck"), 1937));
        assertEquals("allCopies() must return books that it owns even when passed a fresh Book() object", 
                     1, library.allCopies(new Book("Of Mice and Men", Arrays.asList("John Steinbeck"), 1937)).size());
        assertEquals("availableCopies() must return books that it owns even when passed a fresh Book() object", 
                     1, library.availableCopies(new Book("Of Mice and Men", Arrays.asList("John Steinbeck"), 1937)).size());
    }
    
    @Test
    public void testFindAfterLosingLastCopy() {
        Library library = makeLibrary();
        Book book = new Book("The Grapes of Wrath", Arrays.asList("John Steinbeck"), 1939);
        BookCopy copy1 = library.buy(book);
        BookCopy copy2 = library.buy(book);
        assertEquals(Arrays.asList(book), library.find(book.getTitle()));
        
        library.lose(copy1);
        assertEquals("losing a copy of book should not disappear from find() results if there are still other copies",
                     Arrays.asList(book), library.find(book.getTitle()));

        library.lose(copy2);
        assertEquals("losing last copy of a book should remove it from find() results",
                     Collections.emptyList(), library.find(book.getTitle()));
    }
    
    @Test
    public void testFindAfterLosingAndRebuyingBook() {
        Library library = makeLibrary();
        Book book = new Book("Cannery Row", Arrays.asList("John Steinbeck"), 1945);
        BookCopy copy = library.buy(book);
        assertEquals(Arrays.asList(book), library.find(book.getTitle()));
        
        library.lose(copy);
        assertEquals("losing last copy of a book should remove it from find() results",
                     Collections.emptyList(), library.find(book.getTitle()));

        library.buy(book);
        assertEquals(Arrays.asList(book), library.find(book.getTitle()));
    }

    @Test
    public void testRepExposureInAvailableCopies() {
        Library library = makeLibrary();
        Book book = new Book("Master and Commander", Arrays.asList("Patrick O'Brian"), 1969);
        library.buy(book);
        
        // clearing the set returned by the library shouldn't affect the library itself
        assertEquals(1, library.availableCopies(book).size());
        try {
            library.availableCopies(book).clear();
        } catch (Throwable t) {
            // if returns an immutable collection, then fine, test passes
            return;
        }
        assertEquals(1, library.availableCopies(book).size());
    }
    
    @Test
    public void testRepExposureInAllCopies() {
        Library library = makeLibrary();
        Book book = new Book("Post Captain", Arrays.asList("Patrick O'Brian"), 1972);
        library.buy(book);
        
        // clearing the set returned by the library shouldn't affect the library itself
        assertEquals(1, library.allCopies(book).size());
        try {
            library.allCopies(book).clear();
        } catch (Throwable t) {
            // if returns an immutable collection, then fine, test passes
            return;
        }
        assertEquals(1, library.allCopies(book).size());
    }
    
    @Test
    public void testRepExposureInFind() {
        Library library = makeLibrary();
        Book book = new Book("HMS Surprise", Arrays.asList("Patrick O'Brian"), 1973);
        library.buy(book);
        
        // clearing the list of results shouldn't affect future calls to find()
        assertEquals(1, library.find(book.getTitle()).size());
        try {
            library.find(book.getTitle()).clear();
        } catch (Throwable t) {
            // if returns an immutable collection, then fine, test passes
            return;
        }
        assertEquals(1, library.find(book.getTitle()).size());
    }
        
    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

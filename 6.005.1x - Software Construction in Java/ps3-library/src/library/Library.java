package library;

import java.util.List;
import java.util.Set;

/**
 * Library represents a mutable collection of books.  The library may have multiple copies of the same book.
 * At any time, a particular book copy may be either "available" (i.e. present in the library building)
 * or "checked out" (i.e. not in the building, in the possession of a borrower).
 */
public interface Library {

    /**
     * Buy a new copy of a book and add it to the library's collection.
     * @param book Book to buy
     * @return a new, good-condition copy of the book, which is now available in this library
     */
    public BookCopy buy(Book book);
    
    /**
     * Check out a copy of a book.
     * @param copy Copy to check out. Requires that the copy be available in this library.
     */
    public void checkout(BookCopy copy);
    
    /**
     * Check in a copy of a book, making it available again.
     * @param copy Copy to check in.  Requires that the copy be checked out of this library.
     */
    public void checkin(BookCopy copy);
    
    /**
     * Test whether a book copy is available in this library.
     * @param copy Book copy to test
     * @return true if and only if copy is available in this library
     */
    public boolean isAvailable(BookCopy copy);
    
    /**
     * Get all the copies of a book.
     * @param book Book to find
     * @return set of all copies of the book in this library's collection, both available and checked out.
     */
    public Set<BookCopy> allCopies(Book book);
    
    /**
     * Get all the available copies of a book.
     * @param book Book to find
     * @return set of all copies of the book that are available in this library.
     */
    public Set<BookCopy> availableCopies(Book book);
    
    /**
     * Search for books in this library's collection.
     * @param query search string
     * @return list of books in this library's collection (both available and checked out) 
     * whose title or author match the search string, ordered by decreasing amount  of match.
     * A book should appear at most once on the list. 
     * Keyword matching and ranking is underdetermined, but at the very least must support: 
     *     - exact matching of title and author: i.e., if a copy of a book is in the library's 
     *           collection, then find(book.getTitle()) and find(book.getAuthors().get(i)) 
     *           must include book among the results.
     *     - date ordering: if two matching books have the same title and author but different
     *           publication dates, then the newer book should appear earlier on the list. 
     */
    public List<Book> find(String query);
    
    /**
     * Declare a copy of a book as lost from the library.  A copy can be declared lost if it is stolen
     * without being checked out, or if a borrower checks it out but never returns it. 
     * @param copy BookCopy to declare lost.  Must have been previously returned from buy() on this library.
     */
    public void lose(BookCopy copy);


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

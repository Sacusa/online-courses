package library;

import java.util.*;

/** 
 * SmallLibrary represents a small collection of books, like a single person's home collection.
 */
public class SmallLibrary implements Library {

    // This rep is required! 
    // Do not change the types of inLibrary or checkedOut, 
    // and don't add or remove any other fields.
    // (BigLibrary is where you can create your own rep for
    // a Library implementation.)

    // rep
    private Set<BookCopy> inLibrary;
    private Set<BookCopy> checkedOut;

    // rep invariant:
    //    the intersection of inLibrary and checkedOut is the empty set
    //
    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out
    //
    // safety from rep exposure:
    //     Both the reps are private. All methods return copies of the sets.

    public SmallLibrary() {
        inLibrary = new HashSet<BookCopy>();
        checkedOut = new HashSet<BookCopy>();

        checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        for (BookCopy book : inLibrary) {
            if (checkedOut.contains(book)) {
                throw new RuntimeException("rep invariant failure");
            }
        }
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy boughtBook = new BookCopy(book);

        inLibrary.add(boughtBook);

        checkRep();

        return boughtBook;
    }

    @Override
    public void checkout(BookCopy copy) {
        if (inLibrary.contains(copy)) {
            inLibrary.remove(copy);
            checkedOut.add(copy);
        }

        checkRep();
    }

    @Override
    public void checkin(BookCopy copy) {
        if (checkedOut.contains(copy)) {
            checkedOut.remove(copy);
            inLibrary.add(copy);
        }

        checkRep();
    }

    @Override
    public boolean isAvailable(BookCopy copy) {
        checkRep();

        return inLibrary.contains(copy);
    }

    @Override
    public Set<BookCopy> allCopies(Book book) {
        Set<BookCopy> allCopies = new HashSet<BookCopy>();

        for (BookCopy copy : inLibrary) {
            if (copy.getBook().equals(book)) {
                allCopies.add(copy);
            }
        }

        for (BookCopy copy : checkedOut) {
            if (copy.getBook().equals(book)) {
                allCopies.add(copy);
            }
        }

        checkRep();

        return allCopies;
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        Set<BookCopy> allCopies = new HashSet<BookCopy>();

        for (BookCopy copy : inLibrary) {
            if (copy.getBook().equals(book)) {
                allCopies.add(copy);
            }
        }

        checkRep();

        return allCopies;
    }

    @Override
    public List<Book> find(String query) {
        Set<Book> findResult = new HashSet<Book>();

        for (BookCopy copy : inLibrary) {
            Book book = copy.getBook();

            if (book.getTitle().equals(query) || 
                    book.getAuthors().contains(query)) {

                findResult.add(book);
            }
        }

        for (BookCopy copy : checkedOut) {
            Book book = copy.getBook();

            if (book.getTitle().equals(query) || 
                    book.getAuthors().contains(query)) {

                findResult.add(book);
            }
        }

        checkRep();

        List<Book> sortedResult = new ArrayList<Book>(findResult);
        Collections.sort(sortedResult);
        
        return sortedResult;
    }

    @Override
    public void lose(BookCopy copy) {
        inLibrary.remove(copy);
        checkedOut.remove(copy);

        checkRep();
    }

    // uncomment the following methods if you need to implement equals and hashCode,
    // or delete them if you don't
    // @Override
    // public boolean equals(Object that) {
    //     throw new RuntimeException("not implemented yet");
    // }
    // 
    // @Override
    // public int hashCode() {
    //     throw new RuntimeException("not implemented yet");
    // }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}

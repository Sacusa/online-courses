package library;

import java.util.*;

/**
 * BigLibrary represents a large collection of books that might be held by a city or
 * university library system -- millions of books.
 * 
 * In particular, every operation needs to run faster than linear time (as a function of the number of books
 * in the library).
 */
public class BigLibrary implements Library {

    private Map<Book, List<BookCopy>> inLibrary;
    private Map<Book, List<BookCopy>> checkedOut;
    private Map<String, List<Book>> sortedBooks;
    // rep invariant:
    //    the intersection of values of inLibrary and checkedOut are empty sets
    //
    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out
    //    sortedBooks contains the list of books associated with a string,
    //      used to speed up find. Does not care if the book is available or not.
    //
    // safety from rep exposure:
    //     All the reps are private. All methods return copies of the values in maps.

    public BigLibrary() {
        inLibrary = new HashMap<Book, List<BookCopy>>();
        checkedOut = new HashMap<Book, List<BookCopy>>();
        sortedBooks = new HashMap<String, List<Book>>();

        checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        for (List<BookCopy> inCopies : inLibrary.values()) {
            for (BookCopy copy : inCopies) {
                for (List<BookCopy> outCopies : checkedOut.values()) {
                    if (outCopies.contains(copy)) {
                        throw new RuntimeException("rep invariant failure");
                    }
                }
            }
        }
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy copy = new BookCopy(book);

        // check if there is already a list of copies
        if (inLibrary.containsKey(book)) {
            List<BookCopy> existingCopies = inLibrary.get(book);
            existingCopies.add(copy);
            inLibrary.put(book, existingCopies);
        }

        // otherwise add a list of copies
        else {
            List<BookCopy> copies = new ArrayList<BookCopy>();
            copies.add(copy);
            inLibrary.put(book, copies);
        }

        // associate the new book with its title
        String title = book.getTitle();

        if (sortedBooks.containsKey(title)) {
            List<Book> existingBooks = sortedBooks.get(title);

            // add the book, if it doesn't already exist
            if (!existingBooks.contains(book)) {
                existingBooks.add(book);
                Collections.sort(existingBooks);
                sortedBooks.put(title, existingBooks);
            }
        }
        else {
            List<Book> books = new ArrayList<Book>();
            books.add(book);
            sortedBooks.put(title, books);
        }

        // associate the new book with the authors
        for (String author : book.getAuthors()) {
            if (sortedBooks.containsKey(author)) {
                List<Book> existingBooks = sortedBooks.get(author);

                // add the book, if it doesn't already exist
                if (!existingBooks.contains(book)) {
                    existingBooks.add(book);
                    Collections.sort(existingBooks);
                    sortedBooks.put(author, existingBooks);
                }
            }
            else {
                List<Book> books = new ArrayList<Book>();
                books.add(book);
                sortedBooks.put(author, books);
            }
        }

        // update all the keys in sortedBooks
        for (String key : sortedBooks.keySet()) {
            Set<Book> allBooks = new HashSet<Book>();
            allBooks.addAll(inLibrary.keySet());
            allBooks.addAll(checkedOut.keySet());

            List<Book> books = sort(allBooks, key);

            sortedBooks.put(key, books);
        }

        checkRep();

        return copy;
    }

    @Override
    public void checkout(BookCopy copy) {
        Book book = copy.getBook();

        // check if copies of the book exist
        if (!inLibrary.containsKey(book)) {
            return;
        }

        List<BookCopy> inCopies = inLibrary.get(book);

        if (inCopies.contains(copy)) {

            // remove copy from library
            inCopies.remove(copy);

            // add copy to checkedOut
            List<BookCopy> outCopies;

            if (checkedOut.containsKey(book)) {
                outCopies = checkedOut.get(book);
            }
            else {
                outCopies = new ArrayList<BookCopy>();
            }
            outCopies.add(copy);

            // update maps
            inLibrary.put(book, inCopies);
            checkedOut.put(book, outCopies);
        }

        checkRep();
    }

    @Override
    public void checkin(BookCopy copy) {
        Book book = copy.getBook();

        // check if copies of the book exist
        if (!checkedOut.containsKey(book)) {
            return;
        }

        List<BookCopy> outCopies = checkedOut.get(book);

        if (outCopies.contains(copy)) {

            // remove copy from checkedOut
            outCopies.remove(copy);

            // add copy to library
            List<BookCopy> inCopies;

            if (inLibrary.containsKey(book)) {
                inCopies = inLibrary.get(book);
            }
            else {
                inCopies = new ArrayList<BookCopy>();
            }
            inCopies.add(copy);

            // update maps
            inLibrary.put(book, inCopies);
            checkedOut.put(book, outCopies);
        }

        checkRep();
    }

    @Override
    public Set<BookCopy> allCopies(Book book) {
        Set<BookCopy> copies;

        // add copies from library, if they exist
        if (inLibrary.containsKey(book)) {
            copies = new HashSet<BookCopy>(inLibrary.get(book));
        }
        else {
            copies = new HashSet<BookCopy>();
        }


        // add copies from checkedOut, if they exist
        if (checkedOut.containsKey(book)) {
            copies.addAll(checkedOut.get(book));
        }

        return copies;
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        Set<BookCopy> copies;

        // add copies from library, if they exist
        if (inLibrary.containsKey(book)) {
            copies = new HashSet<BookCopy>(inLibrary.get(book));
        }
        else {
            copies = new HashSet<BookCopy>();
        }

        return copies;
    }

    @Override
    public boolean isAvailable(BookCopy copy) {
        Book book = copy.getBook();

        if (inLibrary.containsKey(book)) {
            return inLibrary.get(book).contains(copy);
        }

        return false;
    }

    /**
     * Find the books according to the given query.
     * The books that contain the exact match of the query are ranked higher, from
     * highest number of matches to lower.
     * The books that conatin the query come next,
     * from highest number matches to lowest.
     * In case of conflicts, newer books are ranked higher.
     */
    @Override
    public List<Book> find(String query) {
        // check if the sortedBooks map already contains the results
        if (sortedBooks.containsKey(query)) {
            return new ArrayList<Book>(sortedBooks.get(query));
        }

        // prepare a set of books to search and sort for
        Set<Book> books = new HashSet<Book>();
        books.addAll(inLibrary.keySet());
        books.addAll(checkedOut.keySet());        

        // sort the results into a list
        List<Book> sortedResult = sort(books, query);

        // add this set into sortedBooks
        sortedBooks.put(query, sortedResult);

        checkRep();

        return sortedResult;
    }

    /**
     * Sort the given set of books, ranking them according to the number of matches
     * they have with the given query.
     * If the score of any books is zero, it is not added to the list
     * 
     * @param books the set of books to sort
     * @param query the string to compare with the title and authors of the book
     * @return sorted list of books
     */
    private List<Book> sort(Set<Book> books, String query) {
        Map<Integer, List<Book>> map = new TreeMap<Integer, List<Book>>();
        List<Book> sortedBooks = new ArrayList<Book>();

        // check if query.length = 0
        if (query.length() == 0) {
            return sortedBooks;
        }

        // map number of matches to a list of books
        // the lists are sorted by compare() in books
        for (Book book : books) {
            Integer numberOfMatches = 0;
            String title = book.getTitle();

            // check if title matches the query
            if (title.equals(query)) {
                ++numberOfMatches;
            }

            // check if title contains the query
            if (title.contains(query)) {
                ++numberOfMatches;
            }

            // check if query contains the title
            if (query.contains(title)) {
                ++numberOfMatches;
            }

            for (String author : book.getAuthors()) {
                // check if author matches the query
                if (author.equals(query)) {
                    ++numberOfMatches;
                }

                // check if author contains the query
                if (author.contains(query)) {
                    ++numberOfMatches;
                }

                // check if query contains the author
                if (query.contains(author)) {
                    ++numberOfMatches;
                }
            }

            // skip books with a zero score
            if (numberOfMatches == 0) {
                continue;
            }

            // add the book to the list mapped to by numberOfMatches
            List<Book> associatedBooks;
            if (map.containsKey(numberOfMatches)) {
                associatedBooks = map.get(numberOfMatches);
                associatedBooks.add(book);
                Collections.sort(associatedBooks);
            }
            else {
                associatedBooks = new ArrayList<Book>();
                associatedBooks.add(book);
            }
            map.put(numberOfMatches, associatedBooks);
        }

        // add the sorted lists to the final result
        for (List<Book> booksToAdd : map.values()) {
            sortedBooks.addAll(0, booksToAdd);
        }

        return sortedBooks;
    }

    @Override
    public void lose(BookCopy copy) {
        Book book = copy.getBook();

        if (inLibrary.containsKey(book)) {
            List<BookCopy> inCopies = inLibrary.get(book);
            inCopies.remove(copy);
            inLibrary.put(book, inCopies);

            // remove book from sortedBooks if no copy left
            if (inCopies.size() == 0) {
                for (List<Book> books : sortedBooks.values()) {
                    books.remove(book);
                }
            }
        }

        if (checkedOut.containsKey(book)) {
            List<BookCopy> outCopies = checkedOut.get(book);
            outCopies.remove(copy);
            checkedOut.put(book, outCopies);

            // remove book from sortedBooks if no copy left
            if (outCopies.size() == 0) {
                for (List<Book> books : sortedBooks.values()) {
                    books.remove(book);
                }
            }
        }

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

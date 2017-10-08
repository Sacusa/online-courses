package library;

import java.util.*;

/**
 * Book is an immutable type representing an edition of a book -- not the physical object, 
 * but the combination of words and pictures that make up a book.  Each book is uniquely
 * identified by its title, author list, and publication year.  Alphabetic case and author 
 * order are significant, so a book written by "Fred" is different than a book written by "FRED".
 */
public class Book implements Comparator<Book>, Comparable<Book> { 

    private final String title;
    // Rep invariant:
    //   contains at least one non-space character
    // Abstraction function:
    //   represents the title of a book
    // Safety from rep exposure:
    //   immutable data type

    private final List<String> authors;
    // Rep invariant:
    //   must contain atleast on element; each list item contains at least one non-space character
    // Abstraction function:
    //   represents the list of authors
    // Safety from rep exposure:
    //   in constructor, copy of the list is stored.
    //   in getAuthors(), copy of list is passed.

    private final int year;
    // Rep invariant:
    //   non-negative
    // Abstraction function:
    //   represents the year the book was published
    // Safety from rep exposure:
    //   immutable data type

    /**
     * Make a Book.
     * @param title Title of the book. Must contain at least one non-space character.
     * @param authors Names of the authors of the book.  Must have at least one name, and each name must contain 
     * at least one non-space character.
     * @param year Year when this edition was published in the conventional (Common Era) calendar.  Must be nonnegative. 
     */
    public Book(String title, List<String> authors, int year) {
        this.title = title;
        this.authors = new ArrayList<String>(authors);
        this.year = year;

        checkRep();
    }

    // assert the rep invariant
    private void checkRep() {

        // check title
        boolean titleOK = false;

        if (title.length() > 0) {
            for (char letter : title.toCharArray()) {
                if (letter != ' ') {
                    titleOK = true;
                }
            }
        }

        if (!titleOK) {
            throw new RuntimeException("invalid title");
        }

        // check authors
        boolean authorOK = false;
        boolean authorsOK = true;

        if (authors.size() > 0) {
            for (String author : authors) {

                // check individual author
                if (author.length() > 0) {
                    for (char letter : author.toCharArray()) {
                        if (letter != ' ') {
                            authorOK = true;
                        }
                    }
                }

                if (!authorOK) {
                    authorsOK = false;
                }
            }
        }
        else {
            authorsOK = false;
        }

        if (!authorsOK) {
            throw new RuntimeException("invalid authors");
        }

        // check year
        if (year < 0) {
            throw new RuntimeException("invalid year");
        }
    }

    /**
     * @return the title of this book
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the authors of this book
     */
    public List<String> getAuthors() {
        return new ArrayList<String>(authors);
    }

    /**
     * @return the year that this book was published
     */
    public int getYear() {
        return year;
    }

    /**
     * @return human-readable representation of this book that includes its title,
     *    authors, and publication year
     */
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append(title + " (" + year + ") by ");

        for (int i = 0, size = authors.size() - 1; i < size; ++i) {
            string.append(authors.get(i) + ", ");
        }

        string.append(authors.get(authors.size() - 1));

        checkRep();
        
        return string.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Book)) return false;
        Book thatBook = (Book) that;
        
        boolean equals = true;
        
        // check title
        if (!(this.title.equals(thatBook.getTitle()))) {
            equals = false;
        }
        
        // check authors
        if (!(this.authors.equals(thatBook.getAuthors()))) {
            equals = false;
        }
        
        // check year
        if (!(this.year == thatBook.getYear())) {
            equals = false;
        }
        
        checkRep();
        
        return equals;
    }

    @Override
    public int hashCode() {
        int result = 13;
        int c = 0;
        
        // hashcode for title
        c = title.hashCode();
        result = 37 * result + c;
        
        // hashcode for authors
        for (String author : authors) {
            c = author.hashCode();
            result = 37 * result + c;
        }
        
        // hashcode for year
        c = (int)year;
        result = 37 * result + c;
        
        checkRep();
        
        return result;
    }

    @Override
    public int compare(Book book1, Book book2) {
        return book1.getYear() - book2.getYear();
    }
    
    @Override
    public int compareTo(Book book) {
        return book.getYear() - this.getYear();
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}

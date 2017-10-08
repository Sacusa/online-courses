package library;

/**
 * BookCopy is a mutable type representing a particular copy of a book that is held in a library's
 * collection.
 */
public class BookCopy {

    private final Book book;
    // Rep invariant:
    //   a valid Book object, as defined in Book.java
    // Abstraction function:
    //   represents the book of which this object is a copy
    // Safety from rep exposure:
    //   the object is private and final
    //   safety of Book class is documented in Book.java

    private Condition condition;
    // Rep invariant:
    //   one of the possible values of enum Condition (statically checked)
    // Abstraction function:
    //   represents the condition of the copy of the book
    // Safety from rep exposure:
    //   private data member

    public static enum Condition {
        GOOD, DAMAGED
    };

    /**
     * Make a new BookCopy, initially in good condition.
     * @param book the Book of which this is a copy
     */
    public BookCopy(Book book) {
        this.book = book;
        condition = BookCopy.Condition.GOOD;

        checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        // create a new Book object with values of book
        Book checkBook = new Book(book.getTitle(), book.getAuthors(), book.getYear());
    }

    /**
     * @return the Book of which this is a copy
     */
    public Book getBook() {
        return book;
    }

    /**
     * @return the condition of this book copy
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Set the condition of a book copy.  This typically happens when a book copy is returned and a librarian inspects it.
     * @param condition the latest condition of the book copy
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     * @return human-readable representation of this book that includes book.toString()
     *    and the words "good" or "damaged" depending on its condition
     */
    public String toString() {
        StringBuilder string = new StringBuilder(book.toString());

        string.append(" in ");

        switch(condition) {
        case GOOD: string.append("good");
        break;
        case DAMAGED: string.append("damaged");
        break;
        }
        
        string.append(" condition");
        
        return string.toString();
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

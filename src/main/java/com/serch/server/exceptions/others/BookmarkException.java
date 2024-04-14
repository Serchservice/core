package com.serch.server.exceptions.others;

/**
 * The BookmarkException class represents an exception related to bookmarking operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class BookmarkException extends RuntimeException {
    public BookmarkException(String message) {
        super(message);
    }
}

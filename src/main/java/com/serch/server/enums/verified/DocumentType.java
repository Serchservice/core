package com.serch.server.enums.verified;

/**
 * The DocumentType enum represents different types of documents in the application.
 * Each enum constant corresponds to a specific type of document.
 * <p></p>
 * The document types are:
 * <ul>
 *     <li>{@link DocumentType#LICENSE} - Represents a license document</li>
 *     <li>{@link DocumentType#CERTIFICATION} - Represents a certification document</li>
 *     <li>{@link DocumentType#TIN} - Represents a tax identification number (TIN) document</li>
 *     <li>{@link DocumentType#PERMIT} - Represents a permit document</li>
 *     <li>{@link DocumentType#NONE} - Represents no specific document type</li>
 * </ul>
 */
public enum DocumentType {
    LICENSE,
    CERTIFICATION,
    TIN,
    PERMIT,
    NONE
}
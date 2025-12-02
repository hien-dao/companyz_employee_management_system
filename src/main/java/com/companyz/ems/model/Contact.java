package com.companyz.ems.model;

/**
 * Represents a contact record for a person (phone, email, etc.).
 * <p>
 * Each contact entry stores a specific contact type and value, with an optional
 * flag indicating whether this is the primary contact method.
 * </p>
 */
public class Contact {
    /** Primary key identifier for the contact record. */
    private int contactId;

    /** Type of contact (e.g., "EMAIL", "PHONE", "MOBILE"). */
    private String contactType;

    /** The actual contact value (e.g., email address or phone number). */
    private String contactValue;

    /** Flag indicating if this is the primary contact method. */
    private Boolean isPrimary;

    /**
     * Returns the contact record identifier.
     *
     * @return the contact id
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Sets the contact record identifier.
     *
     * @param contactId the contact id to set
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Returns the contact type.
     *
     * @return the contact type or {@code null} if not set
     */
    public String getContactType() {
        return contactType;
    }

    /**
     * Sets the contact type.
     *
     * @param contactType the contact type to set (e.g., "EMAIL", "PHONE")
     */
    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    /**
     * Returns the contact value.
     *
     * @return the contact value or {@code null} if not set
     */
    public String getContactValue() {
        return contactValue;
    }

    /**
     * Sets the contact value.
     *
     * @param contactValue the contact value to set (e.g., email or phone number)
     */
    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }

    /**
     * Returns whether this is the primary contact.
     *
     * @return {@code true} if primary, {@code false} if not, or {@code null} if not set
     */
    public Boolean getIsPrimary() {
        return isPrimary;
    }

    /**
     * Sets whether this is the primary contact.
     *
     * @param isPrimary {@code true} if this is the primary contact, {@code false} otherwise
     */
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}

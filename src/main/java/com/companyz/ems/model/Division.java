package com.companyz.ems.model;

/**
 * Represents a company division or business unit.
 */
public class Division {
    /** Primary key identifier for the division. */
    private int divisionId;

    /** Human-friendly division name. */
    private String divisionName;

    /** Optional description of the division. */
    private String description;
    /** Postal address for the division (optional). */
    private Address address;

    /** Whether the division is currently active. */
    private Boolean isActive;

    /**
     * Returns the division identifier.
     *
     * @return division id
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Sets the division identifier.
     *
     * @param divisionId id to set
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * Returns the division name.
     *
     * @return division name or {@code null}
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * Sets the division name.
     *
     * @param divisionName name to set
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * Returns the division description.
     *
     * @return description or {@code null}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the division description.
     *
     * @param description description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the division postal address.
     *
     * @return {@link Address} instance or {@code null} if not set
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the division postal address.
     *
     * @param address address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Returns whether the division is active.
     *
     * @return {@code true} if active, {@code false} if inactive, or {@code null} if unknown
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Sets whether the division is active.
     *
     * @param isActive boolean flag to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

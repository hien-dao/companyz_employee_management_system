package com.companyz.ems.model;

/**
 * Simple value object representing a postal address.
 */
public class Address {
    /** Primary address line (street, number). */
    private String addressLine1;

    /** Secondary address line (apartment, suite). */
    private String addressLine2;

    /** City name. */
    private String city;

    /** State or province. */
    private String state;

    /** Country name. */
    private String country;

    /** Postal or ZIP code. */
    private String postalCode;

    /**
     * Returns the primary address line.
     *
     * @return address line 1 or {@code null}
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the primary address line.
     *
     * @param addressLine1 address line 1 to set
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Returns the secondary address line.
     *
     * @return address line 2 or {@code null}
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the secondary address line.
     *
     * @param addressLine2 address line 2 to set
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Returns the city name.
     *
     * @return city or {@code null}
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city name.
     *
     * @param city city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the state or province name.
     *
     * @return state or {@code null}
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state or province name.
     *
     * @param state state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Returns the country name.
     *
     * @return country or {@code null}
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country name.
     *
     * @param country country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns the postal or ZIP code.
     *
     * @return postal code or {@code null}
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal or ZIP code.
     *
     * @param postalCode postal code to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}

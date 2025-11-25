package com.companyz.ems.model;

/**
 * Represents a person's basic personal information used across the
 * employee management system.
 * <p>
 * This simple POJO holds commonly required attributes such as first/last
 * name, gender, race and date of birth. All fields are exposed via
 * standard getters and setters.
 * </p>
 */
public abstract class Person {
    /** Given (first) name of the person. */
    private String firstName;

    /** Family (last) name of the person. */
    private String lastName;

    /** Gender value; stored as a free-form string (e.g. "Male", "Female"). */
    private String gender;

    /** Race or ethnicity value; stored as a free-form string. */
    private String race;

    /** Date of birth as an ISO-like string (yyyy-MM-dd). */
    private String dob;

    /**
     * Returns the person's first name.
     *
     * @return the first name, or {@code null} if not set
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the person's first name.
     *
     * @param firstName given name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the person's last name.
     *
     * @return the last name, or {@code null} if not set
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the person's last name.
     *
     * @param lastName family name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the person's gender value.
     *
     * @return the gender string, or {@code null} if not set
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the person's gender.
     *
     * @param gender a free-form gender string
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Returns the person's race/ethnicity value.
     *
     * @return the race string, or {@code null} if not set
     */
    public String getRace() {
        return race;
    }

    /**
     * Sets the person's race or ethnicity.
     *
     * @param race a free-form race/ethnicity string
     */
    public void setRace(String race) {
        this.race = race;
    }

    /**
     * Returns the person's date of birth.
     * <p>
     * The value is stored as a string in ISO-like format (yyyy-MM-dd).
     * Consider using a date type in the future if date operations are needed.
     * </p>
     *
     * @return date of birth string, or {@code null} if not set
     */
    public String getDob() {
        return dob;
    }

    /**
     * Sets the person's date of birth.
     *
     * @param dob date of birth string (recommended format: yyyy-MM-dd)
     */
    public void setDob(String dob) {
        this.dob = dob;
    }
}

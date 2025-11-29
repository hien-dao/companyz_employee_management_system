package com.companyz.ems.model;
import java.time.LocalDate;

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

    /** Date of birth as a {@link java.time.LocalDate}. */
    private LocalDate dob;

    /** Last 4 digits of the SSN for display. */
    private String ssnLast4;     // CHAR(4) → String

    /** Irreversible hash of the SSN (e.g. SHA-256 hex string) for searches. */
    private String ssnHash;      // CHAR(64) → String (SHA-256 hex string)

    /** Encrypted SSN bytes for secure retrieval when needed. */
    private byte[] ssnEnc;       // VARBINARY(256) → byte[]

    /** Initialization vector used during SSN encryption. */
    private byte[] ssnIv;        // VARBINARY(16) → byte[]

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
     * Returns the person's date of birth as {@link LocalDate}.
     *
     * @return the date of birth, or {@code null} if not set
     */
    public LocalDate getDob() {
        return dob;
    }

    /**
     * Sets the person's date of birth.
     *
     * @param dob the date of birth to set (may be {@code null})
     */
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    /**
     * Returns the last 4 digits of the SSN used for display purposes.
     *
     * @return last 4 digits of SSN, or {@code null} if not set
     */
    public String getSsnLast4() {
        return ssnLast4;
    }

    /**
     * Sets the last 4 digits of the SSN.
     *
     * @param ssnLast4 last 4 digits (exactly 4 characters recommended)
     */
    public void setSsnLast4(String ssnLast4) {
        this.ssnLast4 = ssnLast4;
    }

    /**
     * Returns the irreversible hash of the SSN (e.g. SHA-256 hex string).
     *
     * @return SSN hash string, or {@code null} if not set
     */
    public String getSsnHash() {
        return ssnHash;
    }

    /**
     * Sets the irreversible SSN hash.
     *
     * @param ssnHash hash value (hex-encoded) to set
     */
    public void setSsnHash(String ssnHash) {
        this.ssnHash = ssnHash;
    }

    /**
     * Returns a defensive copy of the encrypted SSN bytes.
     *
     * @return a copy of the encrypted SSN bytes, or {@code null} if not set
     */
    public byte[] getSsnEnc() {
        return ssnEnc == null ? null : ssnEnc.clone();
    }

    /**
     * Sets the encrypted SSN bytes (defensive copy made).
     *
     * @param ssnEnc encrypted SSN bytes to set
     */
    public void setSsnEnc(byte[] ssnEnc) {
        this.ssnEnc = ssnEnc == null ? null : ssnEnc.clone();
    }

    /**
     * Returns a defensive copy of the SSN initialization vector (IV).
     *
     * @return IV bytes copy, or {@code null} if not set
     */
    public byte[] getSsnIv() {
        return ssnIv == null ? null : ssnIv.clone();
    }

    /**
     * Sets the SSN initialization vector (IV).
     *
     * @param ssnIv IV bytes to set
     */
    public void setSsnIv(byte[] ssnIv) {
        this.ssnIv = ssnIv == null ? null : ssnIv.clone();
    }
}

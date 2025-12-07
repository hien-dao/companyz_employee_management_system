package com.companyz.ems.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords using BCrypt,
 * plus an extra random salt stored separately in the DB.
 */
public class PasswordHasher {
    private final int strength;
    private final SecureRandom random = new SecureRandom();

    public PasswordHasher(int strength) {
        this.strength = strength;
    }

    /**
     * Hash a password with BCrypt and generate a separate random salt.
     *
     * @param password raw password characters
     * @return HashedPassword containing hash bytes and salt bytes
     */
    public HashedPassword hashWithSalt(char[] password) {
        // Generate random salt bytes
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String saltBase64 = Base64.getEncoder().encodeToString(saltBytes);

        // Combine password + extra salt
        String saltedPassword = new String(password) + saltBase64;

        // Hash with BCrypt
        String hashStr = BCrypt.hashpw(saltedPassword, BCrypt.gensalt(strength));

        return new HashedPassword(hashStr.getBytes(StandardCharsets.UTF_8), saltBytes);
    }

    /**
     * Verify a password against stored hash and salt.
     *
     * @param password raw password characters
     * @param storedHash hash bytes from DB
     * @param storedSalt salt bytes from DB
     * @return true if password matches
     */
    public boolean verify(char[] password, byte[] storedHash, byte[] storedSalt) {
        String saltBase64 = Base64.getEncoder().encodeToString(storedSalt);
        String saltedPassword = new String(password) + saltBase64;
        String storedHashStr = new String(storedHash, StandardCharsets.UTF_8);
        return BCrypt.checkpw(saltedPassword, storedHashStr);
    }

    /**
     * Simple container for hash + salt.
     */
    public static class HashedPassword {
        private final byte[] hash;
        private final byte[] salt;

        public HashedPassword(byte[] hash, byte[] salt) {
            this.hash = hash;
            this.salt = salt;
        }

        public byte[] getHash() { return hash; }
        public byte[] getSalt() { return salt; }
    }
}

package com.companyz.ems.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.companyz.ems.config.AppConfig;

public class SsnEncryptor {

    private final SecretKey secretKey;
    private final String algorithm;
    private final SecureRandom secureRandom;

    public SsnEncryptor() {
        this.algorithm = AppConfig.get("security.ssn.encryption.algorithm");
        this.secretKey = loadKey();
        this.secureRandom = new SecureRandom();
    }

    private SecretKey loadKey() {
        String keyBase64 = AppConfig.get("security.ssn.encryption.key");
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public byte[] hashSsn(String ssn) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(ssn.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] encryptSsn(String ssn, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(ssn.getBytes(StandardCharsets.UTF_8));
    }

    public String decryptSsn(byte[] encrypted, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] plain = cipher.doFinal(encrypted);
        return new String(plain, StandardCharsets.UTF_8);
    }

    public byte[] generateIv() {
        byte[] iv = new byte[16]; // AES block size
        secureRandom.nextBytes(iv);
        return iv;
    }

    public String extractLast4(String ssn) {
        if (ssn == null || ssn.length() < 4) {
            throw new IllegalArgumentException("SSN too short");
        }
        return ssn.substring(ssn.length() - 4);
    }
}

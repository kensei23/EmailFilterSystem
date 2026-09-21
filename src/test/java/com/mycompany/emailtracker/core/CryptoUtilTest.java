/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.emailtracker.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author aaron
 */
public class CryptoUtilTest {

    @Test
    void decryptReversesEncryptForNormalText() throws Exception {
        String original = "my-super-secret-app-password";

        String encrypted = CryptoUtil.encrypt(original);
        String decrypted = CryptoUtil.decrypt(encrypted);

        assertEquals(original, decrypted);
    }

    @Test
    void encryptedTextIsNotTheSameAsPlainText() throws Exception {
        String original = "hunter2";

        String encrypted = CryptoUtil.encrypt(original);

        assertNotEquals(original, encrypted);
    }

    @Test
    void encryptingTheSameStringTwiceGivesDifferentCiphertext() throws Exception {
        String original = "same-password-both-times";

        String firstEncryption = CryptoUtil.encrypt(original);
        String secondEncryption = CryptoUtil.encrypt(original);

        assertNotEquals(firstEncryption, secondEncryption);

        assertEquals(original, CryptoUtil.decrypt(firstEncryption));
        assertEquals(original, CryptoUtil.decrypt(secondEncryption));
    }

    @Test
    void emptyStringRoundTripsCorrectly() throws Exception {
        String original = "";

        String encrypted = CryptoUtil.encrypt(original);
        String decrypted = CryptoUtil.decrypt(encrypted);

        assertEquals(original, decrypted);
    }

    @Test
    void decryptingGarbageInputThrowsRatherThanReturningWrongData() {
        assertThrows(Exception.class, () -> CryptoUtil.decrypt("not-valid-encrypted-data"));
    }
}

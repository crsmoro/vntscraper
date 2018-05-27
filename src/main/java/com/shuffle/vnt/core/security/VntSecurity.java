package com.shuffle.vnt.core.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.core.configuration.model.Preferences;
import com.shuffle.vnt.core.db.PersistenceManager;

public interface VntSecurity {
	
	Log log = LogFactory.getLog(VntSecurity.class);

	int ITERATION_COUNT = 1000;

	int SALT_ENCRYPTION_KEY_BITS = 160;

	int ENCRYPTION_KEY_BITS = 128;

	int SALT_HMAC_KEY_BITS = 160;

	int HMAC_KEY_BITS = 160;

	public static byte[] deriveKey(byte[] s, int l, String key) throws Exception {
		PBEKeySpec ks = new PBEKeySpec(key.toCharArray(), s, ITERATION_COUNT, l);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		return skf.generateSecret(ks).getEncoded();
	}

	public static String encrypt(String plainText, String key) {
		try {
			long startEncrypt = System.currentTimeMillis();
			SecureRandom r = SecureRandom.getInstance("SHA1PRNG");

			// Generate Salt for Encryption Key
			byte[] esalt = new byte[SALT_ENCRYPTION_KEY_BITS / 8];
			long start = System.currentTimeMillis();
			r.nextBytes(esalt);
			long finish = System.currentTimeMillis();
			log.debug("Took " + (finish - start) + "ms to generate esalt");
			// Generate Encryption Key
			byte[] dek = deriveKey(esalt, ENCRYPTION_KEY_BITS, key);

			// Perform Encryption
			SecretKeySpec eks = new SecretKeySpec(dek, "AES");
			Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
			c.init(Cipher.ENCRYPT_MODE, eks, new IvParameterSpec(new byte[16]));
			byte[] es = c.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			// Generate Salt for HMAC Key
			byte[] hsalt = new byte[SALT_HMAC_KEY_BITS / 8];
			start = System.currentTimeMillis();
			r.nextBytes(hsalt);
			finish = System.currentTimeMillis();
			log.debug("Took " + (finish - start) + "ms to generate hsalt");
			// Generate HMAC Key
			byte[] dhk = deriveKey(hsalt, HMAC_KEY_BITS, key);

			// Perform HMAC using SHA-256
			SecretKeySpec hks = new SecretKeySpec(dhk, "HmacSHA256");
			Mac m = Mac.getInstance("HmacSHA256");
			m.init(hks);
			byte[] hmac = m.doFinal(es);

			// Construct Output as "ESALT + HSALT + CIPHERTEXT +
			// HMAC"
			byte[] os = new byte[(SALT_ENCRYPTION_KEY_BITS / 8) + (SALT_HMAC_KEY_BITS / 8) + es.length + 32];
			System.arraycopy(esalt, 0, os, 0, (SALT_ENCRYPTION_KEY_BITS / 8));
			System.arraycopy(hsalt, 0, os, (SALT_ENCRYPTION_KEY_BITS / 8), (SALT_HMAC_KEY_BITS / 8));
			System.arraycopy(es, 0, os, (SALT_ENCRYPTION_KEY_BITS / 8) + (SALT_HMAC_KEY_BITS / 8), es.length);
			System.arraycopy(hmac, 0, os, (SALT_ENCRYPTION_KEY_BITS / 8) + (SALT_HMAC_KEY_BITS / 8) + es.length, 32);

			long finishEncrypt = System.currentTimeMillis();
			log.debug("Took " + (finishEncrypt - startEncrypt) + "ms to encrypt");
			// Return a Base64 Encoded String
			return new String(Base64.encodeBase64(os));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decrypt(String encrypted, String key) {
		try {
			long startDecrypt = System.currentTimeMillis();
			// Recover our Byte Array by Base64 Decoding
			byte[] os = Base64.decodeBase64(encrypted.getBytes());

			// Check Minimum Length (ESALT (20) + HSALT (20) + HMAC
			// (32))
			if (os.length > ((SALT_ENCRYPTION_KEY_BITS / 8) + (SALT_HMAC_KEY_BITS / 8) + 32)) {
				// Recover Elements from String
				byte[] esalt = Arrays.copyOfRange(os, 0, (SALT_ENCRYPTION_KEY_BITS / 8));
				byte[] hsalt = Arrays.copyOfRange(os, (SALT_ENCRYPTION_KEY_BITS / 8), (SALT_ENCRYPTION_KEY_BITS / 8) + (SALT_HMAC_KEY_BITS / 8));
				byte[] es = Arrays.copyOfRange(os, (SALT_ENCRYPTION_KEY_BITS / 8) + (SALT_HMAC_KEY_BITS / 8), os.length - 32);
				byte[] hmac = Arrays.copyOfRange(os, os.length - 32, os.length);

				// Regenerate HMAC key using Recovered Salt
				// (hsalt)
				byte[] dhk = deriveKey(hsalt, SALT_HMAC_KEY_BITS, key);

				// Perform HMAC using SHA-256
				SecretKeySpec hks = new SecretKeySpec(dhk, "HmacSHA256");
				Mac m = Mac.getInstance("HmacSHA256");
				m.init(hks);
				byte[] chmac = m.doFinal(es);

				// Compare Computed HMAC vs Recovered HMAC
				if (MessageDigest.isEqual(hmac, chmac)) {
					// HMAC Verification Passed
					// Regenerate Encryption Key using
					// Recovered
					// Salt (esalt)
					byte[] dek = deriveKey(esalt, ENCRYPTION_KEY_BITS, key);

					// Perform Decryption
					SecretKeySpec eks = new SecretKeySpec(dek, "AES");
					Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
					c.init(Cipher.DECRYPT_MODE, eks, new IvParameterSpec(new byte[16]));
					byte[] s = c.doFinal(es);

					long finishDecrypt = System.currentTimeMillis();
					log.debug("Took " + (finishDecrypt - startDecrypt) + "ms to decrypt");
					// Return our Decrypted String
					return new String(s, StandardCharsets.UTF_8);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	Preferences preferences = PersistenceManager.getDao(Preferences.class).findOne();
	
	public static String getTokenKey() {
		return preferences.getTokenKey();
	}
	
	public static String getRefreshTokenKey() {
		return preferences.getRefreshTokenKey();
	}
	
	public static String getPasswordKey() {
		return preferences.getPasswordKey();
	}
}

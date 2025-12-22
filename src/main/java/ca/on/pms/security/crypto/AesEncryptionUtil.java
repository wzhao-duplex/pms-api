package ca.on.pms.security.crypto;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesEncryptionUtil {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int KEY_SIZE = 256;
	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH = 128;

	private AesEncryptionUtil() {
	}

	// =====================================================
	// Encrypt (GENERATES key + IV internally)
	// =====================================================
	public static EncryptionResult encrypt(byte[] plainData) throws Exception {

		KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
		keyGenerator.init(KEY_SIZE);
		SecretKey key = keyGenerator.generateKey();

		byte[] iv = new byte[IV_LENGTH];
		SecureRandom.getInstanceStrong().nextBytes(iv);

		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));

		byte[] encrypted = cipher.doFinal(plainData);

		return new EncryptionResult(encrypted, encodeKey(key), iv);
	}

	// =====================================================
	// Decrypt
	// =====================================================
	public static byte[] decrypt(byte[] encryptedData, SecretKey key, byte[] iv) throws Exception {

		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));

		return cipher.doFinal(encryptedData);
	}

	// =====================================================
	// Key helpers
	// =====================================================
	public static String encodeKey(SecretKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	public static SecretKey decodeKey(String base64Key) {
		return new SecretKeySpec(Base64.getDecoder().decode(base64Key), ALGORITHM);
	}

	// =====================================================
	// Result holder
	// =====================================================
	public record EncryptionResult(byte[] encryptedData, String base64Key, byte[] iv) {
	}
}

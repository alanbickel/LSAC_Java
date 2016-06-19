package ComLog;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import java.io.*;

/*REFERENCE URLS
 * http://www.software-architect.net/articles/using-strong-encryption-in-java/introduction.html
 * http://www.javamex.com/tutorials/random_numbers/securerandom.shtml
 * http://stackoverflow.com/questions/3451670/java-aes-and-using-my-own-key
 * 
 * */
public class ComSecurity {

	/**
	 * generate a unique 16-bit AES encryption key~ each user provided with a
	 * secure, truly random key.
	 */
	public static byte[] getNewRandomKey() {

		Random ranGen = new SecureRandom();
		byte[] aesKey = new byte[16]; // 16 bytes = 128 bits
		ranGen.nextBytes(aesKey);

		return aesKey;
	}

	/**
	 * encode a string with user-specific key~ only the user who writes the
	 * entry can view it.
	 * */
	public static byte[] encryptString(String input, byte[] key) {
		// initialize to failure state, update with encrypted byte array
		// upon successful encryption. calling method will check this return
		// array with
		// is equal to null
		byte[] encrypted = null;
		try {
			// create cypher and key
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

			// encrypt the text
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			encrypted = cipher.doFinal(input.getBytes());

		} catch (Exception e) {

			ExceptionHandler eH = new ExceptionHandler(
					"Security encoding failure: ", e);
		}
		return encrypted;
	}

	/**
	 * 
	 * @param input
	 *            - encoded byte array from file
	 * @param key
	 *            - unique AES key for the user
	 * @return plain-text string
	 */
	public static String getDecryptedStringFromEncrypted(byte[] input,
			byte[] key) {
		String err = "Error";
		try {

			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			String decrypted = new String(cipher.doFinal(input));

			return decrypted;

		} catch (Exception e) {

			ExceptionHandler eH = new ExceptionHandler(
					"Security decryption failure: ", e);
			return err; // if not return good string, let return value be
						// testable for an error.

		}
	}

	public static boolean isEnteredPassMatchUserPass(String user,
			String passInput) {

		String userPassPath = user + "\\" + Util.INIT; // path to stored
														// password
		InputStream fis = null;
		byte[] scrambledInput;
		byte[] storedPass;
		boolean isMatch = false;
		try {
			File file = new File(userPassPath); // get the pass file

			fis = new FileInputStream(userPassPath); // pass to input stream

			storedPass = new byte[(int) file.length()]; // array to hold data

			// convert file into array of bytes
			fis.read(storedPass);
			fis.close();
			// get user key
			byte[] userKey = retrieveUserKey(user);
			scrambledInput = encryptString(passInput, userKey);

			// compare
			if (Arrays.equals(storedPass, scrambledInput)) {
				// passwords match
				isMatch = true;

			}
			return isMatch;
		} catch (Exception e) {

			ExceptionHandler eH = new ExceptionHandler(
					"Authentication Read Failure. ", e);

			return isMatch;
		}

	}

	public static byte[] retrieveUserKey(String user) {
		String userPassPath = user + "\\" + Util.CONFIG_FILE; // path to key for
																// username
		InputStream fis = null;
		BufferedReader br = null;
		byte[] key = null;
		try {
			File file = new File(userPassPath); // get the pass file

			// fis = new FileInputStream(userPassPath); // pass to input stream

			key = new byte[(int) file.length()]; // array to hold data

			// convert file into array of bytes
			fis = new FileInputStream(file);
			fis.read(key);
			fis.close();

			return key;

		} catch (Exception e) {
			ExceptionHandler eH = new ExceptionHandler("Key Read Failue. ", e);

			return key;
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				ExceptionHandler eH = new ExceptionHandler(
						"Error closing file stream. ", e);
			}
		}
	}
}

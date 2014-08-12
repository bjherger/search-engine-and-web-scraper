package webUI;

import indexAndSearch.HTMLCleaner;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import database.Status;

/**
 * Several static methods useful for handling String objects. Should be replaced
 * by a third-party library, such as Apache Commons.
 */
public class StringUtilities {
	/**
	 * Returns current date and time using a long format.
	 * 
	 * @return current date and time
	 */
	public static String getDate() {
		String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
		return getDate(format);
	}

	/**
	 * Returns current date and time using provided format.
	 * 
	 * @return current date and time
	 */
	public static String getDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * Checks to see if a String is null or empty.
	 * 
	 * @param text
	 *            - String to check
	 * @return true if null or empty
	 */
	public static boolean checkString(String text) {
		return (text == null) || text.trim().isEmpty();
	}

	/**
	 * Returns a random byte array. Note: Does not use a secure random number
	 * generator.
	 * 
	 * @param size
	 *            - number of bytes to return
	 * @return random byte array
	 * 
	 * @see {@link SecureRandom}
	 */
	public static byte[] randomBytes(int size) {
		byte[] saltBytes = new byte[size];
		new Random().nextBytes(saltBytes);
		return saltBytes;
	}

	/**
	 * Returns the hex encoding of a byte array.
	 * 
	 * @param bytes
	 *            - byte array to encode
	 * @param length
	 *            - desired length of encoding
	 * @return hex encoded byte array
	 */
	public static String encodeHex(byte[] bytes, int length) {
		BigInteger bigint = new BigInteger(1, bytes);
		String hex = String.format("%0" + length + "X", bigint);

		assert hex.length() == length;
		return hex;
	}

	/**
	 * Calculates the hash of a password and salt using SHA-256.
	 * 
	 * @param password
	 *            - password to hash
	 * @param salt
	 *            - salt associated with user
	 * @return hashed password, or null if unable to hash
	 */
	public static String getHash(String password, String salt) {
		String salted = salt + password;
		String hashed = salted;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salted.getBytes());
			hashed = encodeHex(md.digest(), 64);
		} catch (Exception ex) {
			hashed = null;
		}

		return hashed;
	}

	/**
	 * Returns the {@link Status} object for the provided name, if valid.
	 * Otherwise, returns {@link Status#ERROR}.
	 * 
	 * @param name
	 * @return status
	 */
	public static Status getStatus(String name) {
		Status status = Status.OK;

		try {
			status = Status.valueOf(name);
		} catch (IllegalArgumentException ex) {
			status = Status.ERROR;
		}

		return status;
	}

	/**
	 * Returns the {@link Status} object for the provided ordinal, if valid.
	 * Otherwise, returns {@link Status#ERROR}.
	 * 
	 * @param name
	 * @return status
	 */
	public static Status getStatus(int ordinal) {
		Status status = Status.OK;

		try {
			status = Status.values()[ordinal];
		} catch (ArrayIndexOutOfBoundsException ex) {
			status = Status.ERROR;
		}

		return status;
	}

	/**
	 * Creates a string[] from the given string input, separated on +, and
	 * stripped of HTML tags and formatting
	 * 
	 * @param input
	 * @return
	 */
	public static String[] cleanWebQueryString(String input) {
		ArrayList<String> stringClean = HTMLCleaner.cleanHTMLFormatting(input,
				"[+]");
		String[] toReturn = new String[stringClean.size()];
		int i = 0;
		for (String word : stringClean) {
			toReturn[i] = word;
			i++;
		}
		return toReturn;

	}

	/**
	 * Creates a string of the elements of the given String[], concatenated with
	 * a +
	 * 
	 * @param input
	 * @return
	 */
	public static String createWebQueryString(String[] input) {
		StringBuffer buffer = new StringBuffer();
		if (input != null) {
			for (String word : input) {
				buffer.append(word);
				buffer.append("+");
			}
		}
		return buffer.toString();
	}

	/**
	 * Creates a string of the elements of the given String[], concatenated with
	 * a space
	 * 
	 * @param input
	 * @return
	 */
	public static String createSaveQueryString(String[] input) {
		StringBuffer buffer = new StringBuffer();
		if (input != null) {
			for (String word : input) {
				buffer.append(word);
				buffer.append(" ");
			}
		}
		return buffer.toString();
	}

}

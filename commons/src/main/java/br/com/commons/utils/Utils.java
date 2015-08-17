package br.com.commons.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

public class Utils {
	private final static String THE_NUMBER_OF_THE_BEST = "666";

	/**
	 * Convert a literal amount in bytes
	 * 
	 * @param value
	 *            - A integer value + using K (Kilobytes), M (Megabytes), G (Gigabytes), T (Terabytes), P (Petabytes)
	 * @return The given amount in bytes
	 */
	public static long convertToBytes(final String value) {
		long result = 0;

		final boolean isBytes = value.matches("^.+?\\d$");
		if (!isBytes && (value.length() > 1)) {
			final Float quote = Float.valueOf(value.substring(0, value.length() - 1));

			final char mul = value.toLowerCase().charAt(value.length() - 1);
			switch (mul) {
				case 'k' : {
					result = Float.valueOf(quote * 1024).longValue();
				}
					break;

				case 'm' : {
					result = Float.valueOf(quote * 1024 * 1024).longValue();
				}
					break;

				case 'g' : {
					result = Float.valueOf(quote * 1024 * 1024 * 1024).longValue();
				}
					break;

				case 't' : {
					result = Float.valueOf(quote * 1024 * 1024 * 1024 * 1024).longValue();
				}
					break;

				case 'p' : {
					result = Float.valueOf(quote * 1024 * 1024 * 1024 * 1024 * 1024).longValue();
				}
					break;

				default : {
					result = 0;
				}
					break;
			}
		} else {
			result = Float.valueOf(value).longValue();
		}

		return result;
	}

	/**
	 * Covert a byte value to a human representation
	 * 
	 * @param bytes
	 * @return A human representation from given value. (e.g. bytes=2048 => 2K)
	 */
	public static String convertFromBytes(final Long bytesLong) {
		String quota = null;

		final Float kilo = 1024f;
		final Float mega = 1024f * 1024f;
		final Float giga = 1024f * 1024f * 1024f;
		final Float tera = 1024f * 1024f * 1024f * 1024f;
		final Float peta = 1024f * 1024f * 1024f * 1024f * 1024f;
		final Float exab = 1024f * 1024f * 1024f * 1024f * 1024f * 1024f;;
		
		final Float bytes = bytesLong.floatValue();

		if (bytes == 0) {
			quota = "0";
		} else if ((bytes > 0) && (bytes < kilo)) {
			quota = String.valueOf(bytes).concat("B");
		} else if ((bytes >= kilo) && (bytes < mega)) {
			quota = String.valueOf(Float.valueOf((bytes / kilo))).concat("K");
		} else if ((bytes >= mega) && (bytes < giga)) {
			quota = String.valueOf(Float.valueOf((bytes / mega))).concat("M");
		} else if ((bytes >= giga) && (bytes < tera)) {
			quota = String.valueOf(Float.valueOf((bytes / giga))).concat("G");
		} else if ((bytes >= tera) && (bytes < peta)) {
			quota = String.valueOf(Float.valueOf((bytes / tera))).concat("T");
		} else if ((bytes >= peta) && (bytes < exab)) {
			quota = String.valueOf(Float.valueOf((bytes / peta))).concat("P");
		} else if ((bytes >= exab) && (bytes < (exab * 1024l))) {
			quota = String.valueOf(Float.valueOf((bytes / exab))).concat("E");
		}

		return quota;
	}

	/**
	 * Receive a string path and nomalize it.
	 * 
	 * @param path
	 */

	public static final String normalizePath(final String path) {
		// Normalize the slashes and add leading slash if necessary
		String normalized = path;
		if (normalized.indexOf('\\') >= 0) {
			normalized = normalized.replace('\\', '/');
		}

		/*
		 * if (!normalized.startsWith("/")) { normalized = "/" + normalized; }
		 */

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			final int index = normalized.indexOf("//");
			if (index < 0) {
				break;
			}
			normalized = normalized.substring(0, index) + normalized.substring(index + 1);
		}

		// Resolve occurrences of "%20" in the normalized path
		while (true) {
			final int index = normalized.indexOf("%20");
			if (index < 0) {
				break;
			}
			normalized = normalized.substring(0, index) + " " + normalized.substring(index + 3);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			final int index = normalized.indexOf("/./");
			if (index < 0) {
				break;
			}
			normalized = normalized.substring(0, index) + normalized.substring(index + 2);
		}

		while (true) {
			final int index = normalized.indexOf("/../");
			if (index < 0) {
				break;
			}
			if (index == 0) {
				return (null); // Trying to go outside our context
			}
			final int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
		}

		// Return the normalized path that we have completed
		return (normalized);
	}

	/**
	 * Convert a byte array to hex string
	 * 
	 * @param data
	 * @return
	 */
	public static String convertToHex(final byte[] data) {
		final StringBuilder buf = new StringBuilder();
		for (final byte element : data) {
			int halfbyte = (element >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) {
					buf.append((char) ('0' + halfbyte));
				} else {
					buf.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = element & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Generate a random string
	 * 
	 * @return
	 */
	public static String generateRandomString() {
		final SecureRandom random = new SecureRandom();
		BigInteger bigInteger = new BigInteger(180, random);
		bigInteger = bigInteger.divide(new BigInteger(THE_NUMBER_OF_THE_BEST));

		final String randomString = bigInteger.toString(32);

		return randomString;
	}
	
	/**
	 * Generate a random UUID
	 * 
	 * @return
	 */
	public static String generateRandomUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Test if a host is reachable
	 * @param host
	 * @return
	 */
	public static boolean isHostReachable(final String host) {
		boolean reachable =  false;
		try {
			String os = System.getProperty("os.name");
			Process p1;
			if (os != null && os.startsWith("Windows")) {
				p1 = java.lang.Runtime.getRuntime().exec("ping " + host);
			} else {
				// 'ping -c 1' does not work on Windows
				p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 "+host);
			}
			int returnVal = p1.waitFor();
			reachable = (returnVal==0);
		} catch (Exception e) {
		}
		
		return reachable;
	}
	
	public static void main(String[] args) {
		System.out.println(convertFromBytes(2748779069440l));
	}
}
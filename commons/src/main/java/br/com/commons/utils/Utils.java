package br.com.commons.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import br.com.commons.enums.PlaceTypeEnum;
import br.com.commons.enums.UnitTypeEnum;
import br.com.commons.transport.PlaceObject;

public class Utils {

	private final static String THE_NUMBER_OF_THE_BEST = "666";
	public final static int DEFAULT_MOVE_TIME = 60000; // milliseconds
	public final static int DIAGONAL_MOVE_TIME = 90000; // milliseconds
	public final static int COMBAT_ROUND_TIME = 30000; // milliseconds
	public final static int ORTOGONAL_COST = 1;
	public final static double DIAGONAL_COST = 1.5;
	public static final int MAP_SIZE = 1000;

	/**
	 * Convert a literal amount in bytes
	 * 
	 * @param value
	 *            - A integer value + using K (Kilobytes), M (Megabytes), G
	 *            (Gigabytes), T (Terabytes), P (Petabytes)
	 * @return The given amount in bytes
	 */
	public static long convertToBytes(final String value) {
		long result = 0;

		final boolean isBytes = value.matches("^.+?\\d$");
		if (!isBytes && (value.length() > 1)) {
			final Float quote = Float.valueOf(value.substring(0,
					value.length() - 1));

			final char mul = value.toLowerCase().charAt(value.length() - 1);
			switch (mul) {
			case 'k': {
				result = Float.valueOf(quote * 1024).longValue();
			}
				break;

			case 'm': {
				result = Float.valueOf(quote * 1024 * 1024).longValue();
			}
				break;

			case 'g': {
				result = Float.valueOf(quote * 1024 * 1024 * 1024).longValue();
			}
				break;

			case 't': {
				result = Float.valueOf(quote * 1024 * 1024 * 1024 * 1024)
						.longValue();
			}
				break;

			case 'p': {
				result = Float
						.valueOf(quote * 1024 * 1024 * 1024 * 1024 * 1024)
						.longValue();
			}
				break;

			default: {
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
		final Float exab = 1024f * 1024f * 1024f * 1024f * 1024f * 1024f;
		;

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
			normalized = normalized.substring(0, index)
					+ normalized.substring(index + 1);
		}

		// Resolve occurrences of "%20" in the normalized path
		while (true) {
			final int index = normalized.indexOf("%20");
			if (index < 0) {
				break;
			}
			normalized = normalized.substring(0, index) + " "
					+ normalized.substring(index + 3);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			final int index = normalized.indexOf("/./");
			if (index < 0) {
				break;
			}
			normalized = normalized.substring(0, index)
					+ normalized.substring(index + 2);
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
			normalized = normalized.substring(0, index2)
					+ normalized.substring(index + 3);
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
	 * 
	 * @param host
	 * @return
	 */
	public static boolean isHostReachable(final String host) {
		boolean reachable = false;
		try {
			String os = System.getProperty("os.name");
			Process p1;
			if (os != null && os.startsWith("Windows")) {
				p1 = java.lang.Runtime.getRuntime().exec("ping " + host);
			} else {
				// 'ping -c 1' does not work on Windows
				p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 " + host);
			}
			int returnVal = p1.waitFor();
			reachable = (returnVal == 0);
		} catch (Exception e) {
		}

		return reachable;
	}

	public static Set<PlaceObject> listAllPlacesVisible(Integer visibility,
			Integer x, Integer y) {
		Integer delta = 0;

		if (visibility != 2) {
			if (visibility % 3 == 0) { // divisors
				delta = visibility / 3;
			} else if ((visibility + 1) % 3 == 0) { // adjacents
				delta = (visibility + 1) / 3;
				delta--;
			} else { // fails
				delta = (visibility - 1) / 3;
				delta--;
			}
		}

		Set<PlaceObject> visibles = new HashSet<>();
		visibles.addAll(listPlacesOnQuad1(x, y, delta, visibility));
		visibles.addAll(listPlacesOnQuad2(x, y, delta, visibility));
		visibles.addAll(listPlacesOnQuad3(x, y, delta, visibility));
		visibles.addAll(listPlacesOnQuad4(x, y, delta, visibility));
		return visibles;
	}

	private static Set<PlaceObject> listPlacesOnQuad1(Integer x, Integer y,
			Integer delta, Integer visibility) {
		int cont = visibility + delta + 1;
		int x2 = x;
		int y2 = y + visibility;
		Set<PlaceObject> visibles = new HashSet<>();
		for (int i = 1; i <= cont; i++) {
			for (int j = 0; j <= (y2 - y); j++) {
				visibles.add(new PlaceObject(x2, y + j));
			}
			if (x2 < x + visibility) {
				x2++;
			}
			if (i > delta) {
				y2--;
			}
		}
		return visibles;
	}

	private static Set<PlaceObject> listPlacesOnQuad2(Integer x, Integer y,
			Integer delta, Integer visibility) {
		int cont = visibility + delta + 1;
		int x2 = x;
		int y2 = y + visibility;
		Set<PlaceObject> visibles = new HashSet<>();
		for (int i = 1; i <= cont; i++) {
			for (int j = 0; j <= (y2 - y); j++) {
				visibles.add(new PlaceObject(x2, y + j));
			}
			if (x2 > x - visibility) {
				x2--;
			}
			if (i > delta) {
				y2--;
			}
		}
		return visibles;
	}

	private static Set<PlaceObject> listPlacesOnQuad3(Integer x, Integer y,
			Integer delta, Integer visibility) {
		int cont = visibility + delta + 1;
		int x2 = x;
		int y2 = y - visibility;
		Set<PlaceObject> visibles = new HashSet<>();
		for (int i = 1; i <= cont; i++) {
			for (int j = 0; j <= (y - y2); j++) {
				visibles.add(new PlaceObject(x2, y - j));
			}
			if (x2 > x - visibility) {
				x2--;
			}
			if (i > delta) {
				y2++;
			}
		}
		return visibles;
	}

	private static Set<PlaceObject> listPlacesOnQuad4(Integer x, Integer y,
			Integer delta, Integer visibility) {
		int cont = visibility + delta + 1;
		int x2 = x;
		int y2 = y - visibility;
		Set<PlaceObject> visibles = new HashSet<>();
		for (int i = 1; i <= cont; i++) {
			for (int j = 0; j <= (y - y2); j++) {
				visibles.add(new PlaceObject(x2, y - j));
			}
			if (x2 < x + visibility) {
				x2++;
			}
			if (i > delta) {
				y2++;
			}
		}
		return visibles;
	}

	public static boolean checkProximity(Integer x, Integer y, Integer targetX,
			Integer targetY, Integer range) {

		// Get max orthogonal positions.
		Integer xPlusExtreme = x + range;
		Integer xMinoExtreme = x - range;
		Integer yPlusExtreme = y + range;
		Integer yMinoExtreme = y - range;

		// Discard absurd positions.
		if (targetX > xPlusExtreme || targetX < xMinoExtreme
				|| targetY > yPlusExtreme || targetY < yMinoExtreme) {
			return false;
		}

		Integer delta = 0;

		if (range != 2) {
			if (range % 3 == 0) { // divisors
				delta = range / 3;
			} else if ((range + 1) % 3 == 0) { // adjacents
				delta = (range + 1) / 3;
				delta--;
			} else { // fails
				delta = (range - 1) / 3;
				delta--;
			}
		}

		// Quadrant 1.
		if (targetX >= x && targetY >= y) {
			return calcArcQuad1(x, y, targetX, targetY, xPlusExtreme,
					yPlusExtreme, delta, range);
		}
		// Quadrant 2.
		if (targetX <= x && targetY >= y) {
			return calcArcQuad2(x, y, targetX, targetY, xMinoExtreme,
					yPlusExtreme, delta, range);
		}
		// Quadrant 3.
		if (targetX <= x && targetY <= y) {
			return calcArcQuad3(x, y, targetX, targetY, xMinoExtreme,
					yMinoExtreme, delta, range);
		}
		// Quadrant 4.
		if (targetX >= x && targetY <= y) {
			return calcArcQuad4(x, y, targetX, targetY, xPlusExtreme,
					yMinoExtreme, delta, range);
		}

		return false;
	}

	private static boolean calcArcQuad1(Integer x, Integer y, Integer targetX,
			Integer targetY, Integer xPlusExtreme, Integer yPlusExtreme,
			Integer delta, Integer visibility) {
		int cont = visibility + delta + 1;
		int x2 = x;
		int y2 = yPlusExtreme;
		for (int i = 1; i <= cont; i++) {
			if (x2 >= targetX && y2 >= targetY) {
				return true;
			}
			if (x2 < xPlusExtreme) {
				x2++;
			}
			if (i > delta) {
				y2--;
			}
		}
		return false;
	}

	private static boolean calcArcQuad2(Integer x, Integer y, Integer targetX,
			Integer targetY, Integer xMinoExtreme, Integer yPlusExtreme,
			Integer delta, Integer visibility) {
		int cont = visibility + delta + 1;
		int x2 = x;
		int y2 = yPlusExtreme;
		for (int i = 1; i <= cont; i++) {
			if (x2 <= targetX && y2 >= targetY) {
				return true;
			}
			if (x2 > xMinoExtreme) { // warning.
				x2--;
			}
			if (i > delta) {
				y2--;
			}
		}
		return false;
	}

	private static boolean calcArcQuad3(Integer x, Integer y, Integer targetX,
			Integer targetY, Integer xMinoExtreme, Integer yMinoExtreme,
			Integer delta, Integer visibility) {
		int cont = visibility + delta + 1;
		int x2 = x;
		int y2 = yMinoExtreme;
		for (int i = 1; i <= cont; i++) {
			if (x2 <= targetX && y2 <= targetY) {
				return true;
			}
			if (x2 > xMinoExtreme) {
				x2--;
			}
			if (i > delta) {
				y2++;
			}
		}
		return false;
	}

	private static boolean calcArcQuad4(Integer x, Integer y, Integer targetX,
			Integer targetY, Integer xPlusExtreme, Integer yMinoExtreme,
			Integer delta, Integer visibility) {
		int cont = visibility + delta + 1;
		int x2 = x;
		int y2 = yMinoExtreme;
		for (int i = 1; i <= cont; i++) {
			if (x2 >= targetX && y2 <= targetY) {
				return true;
			}
			if (x2 < xPlusExtreme) {
				x2++;
			}
			if (i > delta) {
				y2++;
			}
		}
		return false;
	}

	// Returns percents
	public static Integer calcMoveBuff(UnitTypeEnum type, PlaceTypeEnum type2) {
		// TODO Auto-generated method stub
		return 1;
	}

	// Returns percents
	public static Integer calcAttackBuff(UnitTypeEnum unit, UnitTypeEnum enemy,
			PlaceTypeEnum place) {
		// TODO Auto-generated method stub
		return 1;
	}

	public static Integer calcAttackPowerPerUnit(Integer atkPower,
			Integer unitQtd, Integer atkBuff) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Integer calcTotalLifePerUnit(Integer life, Integer armour) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean validateMovement(PlaceObject placeActual,
			Deque<Integer> places) {
		// TODO Auto-generated method stub
		return true;
	}

	public static Long convertCoordinateToId(PlaceObject placeObject) {
		return convertCoordinateToId(placeObject.getX(), placeObject.getY());
	}

	public static Long convertCoordinateToId(Integer x, Integer y) {
		return (long) (((x - 1) * MAP_SIZE) + y);
	}
}

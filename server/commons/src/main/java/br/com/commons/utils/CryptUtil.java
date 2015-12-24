package br.com.commons.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class CryptUtil {
	private static final String DEFAULT_KEY = "AquiNaFaculdade0123456789ChupaChupaShow";

	private static CryptUtil instance;

	private final String chiperKey;

	private CryptUtil(final String chiperKey) {
		this.chiperKey = chiperKey;
	}

	public static CryptUtil getInstance() {
		if (null == instance) {
			instance = new CryptUtil(DEFAULT_KEY);
		}

		return instance;
	}

	public static CryptUtil getInstance(final String cipherkey) {
		if (null == instance) {
			instance = new CryptUtil(cipherkey);
		}

		return instance;
	}

	/**
	 * Encrypt a string using a symmetric encryption key
	 * 
	 * @param plainText
	 * @return NULL if some exception occurs
	 */
	public String aesEncrypt(final String plainText) {
		try {
			final byte[] keyBytes = Arrays.copyOf(this.chiperKey.getBytes("ASCII"), 16);

			final SecretKey key = new SecretKeySpec(keyBytes, "AES");
			final Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);

			final byte[] cleartext = plainText.getBytes("UTF-8");
			final byte[] ciphertextBytes = cipher.doFinal(cleartext);
			final char[] encodeHex = Hex.encodeHex(ciphertextBytes);

			return new String(encodeHex);

		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (final NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (final InvalidKeyException e) {
			e.printStackTrace();
		} catch (final IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (final BadPaddingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Dencrypt a string using a symmetric encryption key
	 * 
	 * @param ciphertext
	 * @param decKey
	 * @return
	 */
	public String aesDecrypt(final String ciphertext) {
		try {
			final byte[] keyBytes = Arrays.copyOf(this.chiperKey.getBytes("ASCII"), 16);

			final SecretKey key = new SecretKeySpec(keyBytes, "AES");
			final Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);

			final byte[] decodeHex = Hex.decodeHex(ciphertext.toCharArray());
			final byte[] ciphertextBytes = cipher.doFinal(decodeHex);

			return new String(ciphertextBytes);
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (final NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (final InvalidKeyException e) {
			e.printStackTrace();
		} catch (final IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (final BadPaddingException e) {
			e.printStackTrace();
		} catch (final DecoderException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate a md5
	 * 
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String md5(final byte[] text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] encHash;
		md.update(text, 0, text.length);
		encHash = md.digest();

		return Utils.convertToHex(encHash);
	}

	/**
	 * Generate a md5
	 * 
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String md5(final String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return md5(text.getBytes());
	}
}

package br.com.commons.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesLoader {
	public static String propertiesFile = "project.properties";

	private static PropertiesLoader instance;

	private final String profile;

	private Properties properties;

	/**
	 * 
	 * @param profile
	 */
	private PropertiesLoader(final String profile) {
		this.profile = profile;
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	public static PropertiesLoader getInstance() {
		if (instance != null) {
			return instance;
		} else {
			final InputStream stream = ClassLoader.getSystemResourceAsStream(propertiesFile);
			return getInstance(stream);
		}
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	public static PropertiesLoader getInstance(final String resource) {
		if (instance != null) {
			return instance;
		} else {
			final String path = ClassLoader.getSystemResource(resource).getPath();
			propertiesFile = Utils.normalizePath(path);
			final InputStream stream = ClassLoader.getSystemResourceAsStream(resource);
			return getInstance(stream);
		}
	}

	/**
	 * 
	 * @param urlResource
	 * @return
	 */
	public static PropertiesLoader getInstance(final java.net.URL urlResource) {
		if (instance != null) {
			return instance;
		} else {
			try {
				return getInstance(urlResource.openStream());
			} catch (final java.lang.NullPointerException ne) {
				throw new RuntimeException("The configuration file was not found.!");
			} catch (final java.io.IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * 
	 * @param stream
	 * @return
	 */
	public static PropertiesLoader getInstance(final InputStream stream) {
		if (instance != null) {
			return instance;
		} else {
			final Properties properties = new Properties();
			try {
				properties.load(stream);
			} catch (final java.lang.NullPointerException ne) {
				throw new RuntimeException("The configuration file was not found.!");
			} catch (final java.io.IOException e) {
				throw new RuntimeException(e);
			}

			return getInstance(properties);
		}
	}

	/**
	 * 
	 * @param properties
	 * @return
	 */
	public static PropertiesLoader getInstance(final java.util.Properties properties) {
		if (instance != null) {
			return instance;
		} else {
			final String profile = properties.getProperty("profile", "development");

			instance = new PropertiesLoader(profile);
			instance.properties = properties;

			return instance;
		}
	}

	/**
	 * Retrieve a property from a given key
	 * 
	 * @param key
	 * @return
	 */
	public String loadProperty(final String key) {
		return this.properties.getProperty(this.profile + "." + key);
	}

	/**
	 * Save/update a property
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(final String key, final String value) {
		this.properties.setProperty(key, value);
		try {
			System.out.println(propertiesFile);
			this.properties.store(new FileOutputStream(propertiesFile), "");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
package br.com.engine.persistence.core;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class ConfigFactory {

	private static ConfigFactory config;
	private ResourceBundle resource;
	private boolean load;

	public static ConfigFactory getInstance() {
		if (config == null) {
			return new ConfigFactory();
		} else {
			return config;
		}
	}

	private ConfigFactory() {
		try {
			this.resource = ResourceBundle.getBundle("hibernate-configurations", Locale.getDefault(), Thread
					.currentThread().getContextClassLoader());
			this.load = true;
		}

		catch (final Exception e) {
			this.load = false;
			this.resource = null;
		}
	}

	public int getValorInteger(final String chave) {
		return Integer.parseInt(this.resource.getString(chave));
	}
	public String getValorString(final String chave) {
		return this.resource.getString(chave);
	}

	public Enumeration<String> getKeys() {
		return this.resource.getKeys();
	}

	public boolean isLoad() {
		return this.load;
	}
}

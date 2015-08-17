package br.com.commons.transport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "session")
@XmlAccessorType(XmlAccessType.FIELD)
public class SessionObject implements Serializable{

	private static final long serialVersionUID = 5968355670408977648L;
	
	private String token;
	
	private UserObject user;
	
	private Date expirationDate;
	
	private Date registerDate;
	
	/**
	 * Check if expiration date if greater than actual time
	 * 
	 * @return
	 */
	public boolean isExpired() {
		final Calendar sessionExpiration = Calendar.getInstance();
		sessionExpiration.setTime(this.expirationDate);

		final Calendar now = Calendar.getInstance();

		return sessionExpiration.compareTo(now) <= 0;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the user
	 */
	public UserObject getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(UserObject user) {
		this.user = user;
	}

	/**
	 * @return the expirationDate
	 */
	public Date getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
}

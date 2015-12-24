package br.com.engine.persistence.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.commons.transport.SessionObject;
import br.com.commons.transport.interfaces.TransportObjectInterface;

@Entity
@Table(name = "session")
public class Session implements Serializable, TransportObjectInterface{

	private static final long serialVersionUID = 3609446307419677356L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private Long id;
	
	@Column(nullable = false)
	private String token;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userId")
	private User user;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date expirationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date registerDate;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
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
	 * @return the registerDate
	 */
	public Date getRegisterDate() {
		return registerDate;
	}

	/**
	 * @param registerDate the registerDate to set
	 */
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SessionObject generateTransportObject() {
		SessionObject sessionObject = new SessionObject();
		sessionObject.setToken(getToken());
		sessionObject.setExpirationDate(getExpirationDate());
		sessionObject.setRegisterDate(getRegisterDate());
		sessionObject.setUser(getUser().generateTransportObject());
		return sessionObject;
	}
}

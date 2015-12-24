package br.com.commons.transport;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserObject implements Serializable{
	
	private static final long serialVersionUID = -1014176920578255689L;
	
	private Long id;
	
	private String name;
	
	private String login;
	
	private String password;
	
	private UserGroupObject userGroup;
	
	private Set<Integer> permissions;

	public UserObject() {
		permissions = new HashSet<Integer>();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the userGroup
	 */
	public UserGroupObject getUserGroup() {
		return userGroup;
	}

	/**
	 * @param userGroup the userGroup to set
	 */
	public void setUserGroup(UserGroupObject userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * @return the permissions
	 */
	public Set<Integer> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<Integer> permissions) {
		this.permissions = permissions;
	}

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
}

package br.com.commons.transport;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserGroupObject implements Serializable {

	private static final long serialVersionUID = -3125019850723350931L;
	
	private String name;

	private Set<Integer> permissions;
	
	public UserGroupObject() {
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
}

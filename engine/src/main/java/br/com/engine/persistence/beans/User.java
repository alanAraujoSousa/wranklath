package br.com.engine.persistence.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.com.commons.transport.UserObject;
import br.com.commons.transport.interfaces.TransportObjectInterface;

@Entity
@Table(name = "user")
public class User implements Serializable, TransportObjectInterface {

	private static final long serialVersionUID = -197612681163892724L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "login", unique = true, nullable = false)
	private String login;

	@Column(name = "password", nullable = false)
	private String password;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userGroup_id")
	private UserGroup userGroup;
	
	@OneToMany(fetch=FetchType.LAZY)
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	@JoinColumn(name = "user_id")
	private Set<Building> buildings;
	
	@OneToMany(fetch=FetchType.LAZY)
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	@JoinColumn(name = "user_id")
	private Set<Unit> units;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_permission", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})
	@Fetch(FetchMode.SUBSELECT)
	private Set<Permission> permissions;
	
	public User() {
		permissions = new HashSet<Permission>();
		buildings = new HashSet<Building>();
		units = new HashSet<Unit>();
	}

	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Changes the value of id as the parameter.
	 *
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return Returns the nome.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Changes the value of nome as the parameter.
	 *
	 * @param nome
	 */
	public void setName(String nome) {
		this.name = nome;
	}

	/**
	 * @return Returns the login.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Changes the value of login as the parameter.
	 *
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Changes the value of password as the parameter.
	 *
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserObject generateTransportObject() {
		UserObject userObject = new UserObject();
		userObject.setLogin(getLogin());
		userObject.setPassword(getPassword());
		userObject.setUserGroup(getUserGroup().generateTransportObject());
		for (Permission permission : getPermissions()) {
			userObject.getPermissions().add(permission.getPermission().getId());			
		}
		return userObject;
	}

	/**
	 * @return the userGroup
	 */
	public UserGroup getUserGroup() {
		return userGroup;
	}

	/**
	 * @param userGroup the userGroup to set
	 */
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * @return the permissions
	 */
	public Set<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the buildings
	 */
	public Set<Building> getBuildings() {
		return buildings;
	}

	/**
	 * @param buildings the buildings to set
	 */
	public void setBuildings(Set<Building> buildings) {
		this.buildings = buildings;
	}

	/**
	 * @return the units
	 */
	public Set<Unit> getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(Set<Unit> units) {
		this.units = units;
	}
}

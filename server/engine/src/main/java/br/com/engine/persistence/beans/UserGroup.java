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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.com.commons.transport.UserGroupObject;
import br.com.commons.transport.interfaces.TransportObjectInterface;

@Entity
@Table(name = "userGroup")
public class UserGroup implements Serializable, TransportObjectInterface {

	private static final long serialVersionUID = 1781003160398304164L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private Long id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@OneToMany(fetch=FetchType.LAZY)
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	@JoinColumn(name = "userGroup_id")
	private Set<User> users;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "group_permission", joinColumns = {@JoinColumn(name = "group_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})
	@Fetch(FetchMode.SUBSELECT)
	private Set<Permission> permissions;
	
	public UserGroup() {
		users = new HashSet<User>();
		permissions = new HashSet<Permission>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public UserGroupObject generateTransportObject() {
		UserGroupObject userGroupObject = new UserGroupObject();
		userGroupObject.setName(getName());
		for (Permission permission : getPermissions()) {
			userGroupObject.getPermissions().add(permission.getPermission().getId());			
		}
		return userGroupObject;
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
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
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
}

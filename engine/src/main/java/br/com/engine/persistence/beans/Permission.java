package br.com.engine.persistence.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.commons.enums.PermissionEnum;
import br.com.commons.transport.PermissionObject;
import br.com.commons.transport.interfaces.TransportObjectInterface;

@Entity
@Table(name = "permission")
public class Permission implements Serializable, TransportObjectInterface {

	private static final long serialVersionUID = 1368814118611506132L;

	@Id
	@Column(name = "permission", nullable = false, unique = true)
	private Integer permission;

	@Column(name = "description")
	private String description;

	public Permission() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PermissionObject generateTransportObject() {
		PermissionObject permissionObject = new PermissionObject();
		permissionObject.setPermission(PermissionEnum.getType(permission));
		return permissionObject;
	}
	
	/**
	 * @return Returns the permission.
	 */
	public PermissionEnum getPermission() {
		return PermissionEnum.getType(this.permission);
	}
	
	/**
	 * @param permission the permission to set
	 */
	public void setPermission(PermissionEnum permission) {
		if (permission == null) {
            this.permission = null;
            this.description = null;
        } else {
            this.permission = permission.getId();
            this.description = permission.getPermission();
        }
	}
}

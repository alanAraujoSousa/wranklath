package br.com.commons.transport;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.commons.enums.PermissionEnum;

@XmlRootElement(name = "permission")
@XmlAccessorType(XmlAccessType.FIELD)
public class PermissionObject implements Serializable{

	private static final long serialVersionUID = 6718990687272169926L;
	
	private Integer permission;
	
	public PermissionObject() {
	}
	
	public PermissionObject(PermissionEnum type) {
		setPermission(type);
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
        } else {
            this.permission = permission.getId();
        }
	}
}

package br.com.commons.enums;

public enum PermissionEnum {
	ADMIN("Administrator.", 904232) ,
	LIST_ALL_USER("List all users.", 474722),
	CREATE_USER("Create users.", 109842), 
	DELETE_USER("Delete users", 322093) ,
	
	;
	
	private String permission;
	private int id;

	private PermissionEnum(final String permission, int id) {
		this.permission = permission;
		this.id = id;
	}
	
	/**
	 * @return Returns the permission.
	 */
	public String getPermission() {
		return permission;
	}

	public static PermissionEnum getType(Integer id) {

		if (id == null) {
			return null;
		}

		for (PermissionEnum staus : PermissionEnum.values()) {
			if (id.equals(staus.getId())) {
				return staus;
			}
		}
		throw new IllegalArgumentException("No matching type for id " + id);
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
}

package br.com.commons.enums;

public enum BuildingTypeEnum {
	TOWN("town", 943213),
	BARRACKS("barracks", 194234),
	;
	
	private String name;
	private Integer id;
	
	private BuildingTypeEnum(String name, Integer id) {
		this.name = name;
		this.id = id;
	}
	
	public static BuildingTypeEnum getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (BuildingTypeEnum type : BuildingTypeEnum.values()) {
			if (id.equals(type.getId())) {
				return type;
			}
		}
		throw new IllegalArgumentException("No matching type for id " + id);
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
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
}

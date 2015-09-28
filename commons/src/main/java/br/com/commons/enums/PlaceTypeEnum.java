package br.com.commons.enums;

public enum PlaceTypeEnum {
	WATER(923313, "water", false), 
	IMPASSIBLE(923313, "impassable", false), 
	FOREST(530013, "forest", true), 
	ROAD(530013, "road", true), ;

	private Integer id;
	private String name;
	private boolean passable;

	private PlaceTypeEnum(Integer id, String name, boolean passable) {
		this.id = id;
		this.name = name;
		this.passable = passable;
	}
	
	public static PlaceTypeEnum getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (PlaceTypeEnum type : PlaceTypeEnum.values()) {
			if (id.equals(type.getId())) {
				return type;
			}
		}
		throw new IllegalArgumentException("No matching type for id " + id);
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
	 * @return the passable
	 */
	public boolean isPassable() {
		return passable;
	}

	/**
	 * @param passable the passable to set
	 */
	public void setPassable(boolean passable) {
		this.passable = passable;
	}
}

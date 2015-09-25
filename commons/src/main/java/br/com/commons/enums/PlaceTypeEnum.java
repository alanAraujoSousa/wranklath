package br.com.commons.enums;

public enum PlaceTypeEnum {
	WATER(923313, "water", false, 0, 0, 0), 
	IMPASSIBLE(923313, "impassable", false, 0, 0, 0), 
	FOREST(530013, "forest", true, -10, 0, 15), 
	ROAD(530013, "road", true, 20, 10, 0), ;

	private Integer id;
	private String name;
	private boolean passable;
	private Integer moveBuff;
	private Integer atkBuff;
	private Integer defBuff;

	private PlaceTypeEnum(Integer id, String name, boolean passable,
			Integer moveBuff, Integer atkBuff, Integer defBuff) {
		this.id = id;
		this.name = name;
		this.passable = passable;
		this.moveBuff = moveBuff;
		this.atkBuff = atkBuff;
		this.defBuff = defBuff;
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

	/**
	 * @return the moveBuff
	 */
	public Integer getMoveBuff() {
		return moveBuff;
	}

	/**
	 * @param moveBuff the moveBuff to set
	 */
	public void setMoveBuff(Integer moveBuff) {
		this.moveBuff = moveBuff;
	}

	/**
	 * @return the atkBuff
	 */
	public Integer getAtkBuff() {
		return atkBuff;
	}

	/**
	 * @param atkBuff the atkBuff to set
	 */
	public void setAtkBuff(Integer atkBuff) {
		this.atkBuff = atkBuff;
	}

	/**
	 * @return the defBuff
	 */
	public Integer getDefBuff() {
		return defBuff;
	}

	/**
	 * @param defBuff the defBuff to set
	 */
	public void setDefBuff(Integer defBuff) {
		this.defBuff = defBuff;
	}
	
	
}

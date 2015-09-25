package br.com.commons.enums;

public enum UnitIntentEnum {
	ATTACK(320991),
	DEFENSE(999333),
	MOVE(113033),
	;
	
	private Integer id;

	private UnitIntentEnum(Integer id) {
		this.id = id;
	}
	
	public static UnitIntentEnum getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (UnitIntentEnum type : UnitIntentEnum.values()) {
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
	
}

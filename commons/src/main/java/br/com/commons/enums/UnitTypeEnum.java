package br.com.commons.enums;

public enum UnitTypeEnum {
	CAVALARY(422322, "cavalary", 10, 4, 3, 30, 1, 3),
	INFANTRY(392333, "infantry", 10, 4, 3, 10, 1, 3),
	ARCHER(202356, "archer", 8, 3, 1, 10, 3, 5),
	;
	
	private Integer id;
	private String name;
	private Integer life;	
	private Integer attack;	
	private Integer armour;
	private Integer velocity;
	private Integer atkRange;
	private Integer visibility;
	
	
	private UnitTypeEnum(Integer id, String name, Integer life, 
			Integer attack, Integer armour, Integer velocity, 
			Integer atkRange, Integer visibility) {
		this.name = name;
		this.id = id;
		this.life = life;
		this.setAttack(attack);
		this.setArmour(armour);
		this.velocity = velocity;
		this.setAtkRange(atkRange);
		this.setVisibility(visibility);
	}
	
	public static UnitTypeEnum getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (UnitTypeEnum type : UnitTypeEnum.values()) {
			if (id.equals(type.getId())) {
				return type;
			}
		}
		throw new IllegalArgumentException("No matching type for id " + id);
	}
	
//	public void resolve(String metadata) throws Exception {
//	}
//
//	protected abstract void doResolve() throws Exception;
	
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

	/**
	 * @return the life
	 */
	public Integer getLife() {
		return life;
	}

	/**
	 * @param life the life to set
	 */
	public void setLife(Integer life) {
		this.life = life;
	}

	/**
	 * @return the velocity
	 */
	public Integer getVelocity() {
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(Integer velocity) {
		this.velocity = velocity;
	}

	/**
	 * @return the atkRange
	 */
	public Integer getAtkRange() {
		return atkRange;
	}

	/**
	 * @param atkRange the atkRange to set
	 */
	public void setAtkRange(Integer atkRange) {
		this.atkRange = atkRange;
	}

	/**
	 * @return the visibility
	 */
	public Integer getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return the armour
	 */
	public Integer getArmour() {
		return armour;
	}

	/**
	 * @param armour the armour to set
	 */
	public void setArmour(Integer armour) {
		this.armour = armour;
	}

	/**
	 * @return the attack
	 */
	public Integer getAttack() {
		return attack;
	}

	/**
	 * @param attack the attack to set
	 */
	public void setAttack(Integer attack) {
		this.attack = attack;
	}

}

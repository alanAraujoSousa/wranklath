package br.com.commons.transport;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "combat")
@XmlAccessorType(XmlAccessType.FIELD)
public class CombatObject {

	private String combatId;
	private Set<UnitObject> sideA;
	private Set<UnitObject> sideB;
	private Date startTime;	
	
	public CombatObject() {
		sideA = new HashSet<UnitObject>();
		sideB = new HashSet<UnitObject>();
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the sideA
	 */
	public Set<UnitObject> getSideA() {
		return sideA;
	}

	/**
	 * @param sideA the sideA to set
	 */
	public void setSideA(Set<UnitObject> sideA) {
		this.sideA = sideA;
	}

	/**
	 * @return the sideB
	 */
	public Set<UnitObject> getSideB() {
		return sideB;
	}

	/**
	 * @param sideB the sideB to set
	 */
	public void setSideB(Set<UnitObject> sideB) {
		this.sideB = sideB;
	}

	/**
	 * @return the combatId
	 */
	public String getCombatId() {
		return combatId;
	}

	/**
	 * @param combatId the combatId to set
	 */
	public void setCombatId(String combatId) {
		this.combatId = combatId;
	}
	
}

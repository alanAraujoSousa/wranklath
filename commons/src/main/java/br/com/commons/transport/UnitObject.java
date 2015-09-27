package br.com.commons.transport;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.commons.enums.UnitIntentEnum;
import br.com.commons.enums.UnitTypeEnum;


@XmlRootElement(name = "unit")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitObject {
	
	private Long id;
	private Long targetId;
	private String combatId;
	private Integer type;
	private Integer unitIntent;
	private Date timeToNextMove;
	private MovementObject movementObject;
	private PlaceObject place;
	
	

	/**
	 * @return the unitIntent
	 */
	public UnitIntentEnum getUnitIntent() {
		return UnitIntentEnum.getType(this.unitIntent);
	}

	/**
	 * @param unitIntent the unitIntent to set
	 */
	public void setUnitIntent(UnitIntentEnum unitIntent) {
		if (unitIntent == null) {
			this.unitIntent = null;
		} else {
			this.unitIntent = unitIntent.getId();			
		}
	}
	
	/**
	 * @return the type
	 */
	public UnitTypeEnum getType() {
		return UnitTypeEnum.getType(type);
	}

	/**
	 * @param type the type to set
	 */
	public void setType(UnitTypeEnum type) {
		if (type != null) {
			this.type = type.getId();
		}
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
	 * @return the place
	 */
	public PlaceObject getPlace() {
		return place;
	}
	/**
	 * @param place the place to set
	 */
	public void setPlace(PlaceObject place) {
		this.place = place;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(obj instanceof Long) 
			return obj.equals(id);
		if (getClass() != obj.getClass())
			return false;
		UnitObject other = (UnitObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * @return the timeToNextMove
	 */
	public Date getTimeToNextMove() {
		return timeToNextMove;
	}

	/**
	 * @param timeToNextMove the timeToNextMove to set
	 */
	public void setTimeToNextMove(Date timeToNextMove) {
		this.timeToNextMove = timeToNextMove;
	}

	/**
	 * @return the movementObject
	 */
	public MovementObject getMovementObject() {
		return movementObject;
	}

	/**
	 * @param movementObject the movementObject to set
	 */
	public void setMovementObject(MovementObject movementObject) {
		this.movementObject = movementObject;
	}

	/**
	 * @return the targetId
	 */
	public Long getTargetId() {
		return targetId;
	}

	/**
	 * @param targetId the targetId to set
	 */
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
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

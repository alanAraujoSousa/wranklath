package br.com.engine.persistence.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.com.commons.enums.PlaceTypeEnum;
import br.com.commons.transport.interfaces.TransportObjectInterface;

@Entity
@Table(name = "place")
public class Place implements Serializable, TransportObjectInterface{

	private static final long serialVersionUID = 5866384802415971638L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private Long id;
	
	@Column(name = "x")
	private Long x;
	
	@Column(name = "y")
	private Long y;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "building_id")
	private Building building;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "unit_id")
	private Unit unit;
	
	@Column(name = "type")
	private Integer type;
	
	public Place() {
	}
	
	public Place(Long x, Long y) {
		this();
		this.x = x;
		this.y = y;
	}

	@Override
	public <T> T generateTransportObject() {
		// TODO Auto-generated method stub
		return null;
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
	 * @return the building
	 */
	public Building getBuilding() {
		return building;
	}

	/**
	 * @param building the building to set
	 */
	public void setBuilding(Building building) {
		this.building = building;
	}

	/**
	 * @return the x
	 */
	public Long getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Long x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public Long getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Long y) {
		this.y = y;
	}
	
	/**
	 * @return the type
	 */
	public PlaceTypeEnum getType() {
		return PlaceTypeEnum.getType(type);
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PlaceTypeEnum type) {
		if (type != null) {
			this.type = type.getId();
		}
	}

	/**
	 * @return the unit
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
}

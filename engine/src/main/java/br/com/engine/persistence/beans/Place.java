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
import br.com.commons.transport.PlaceObject;
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
	private Integer x;
	
	@Column(name = "y")
	private Integer y;

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
	
	public Place(Integer x, Integer y) {
		this();
		this.x = x;
		this.y = y;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PlaceObject generateTransportObject() {
		PlaceObject placeObject = new PlaceObject();
		placeObject.setX(getX());
		placeObject.setY(getY());
		placeObject.setType(getType());
		return placeObject;
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

	/**
	 * @return the x
	 */
	public Integer getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Integer x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public Integer getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Integer y) {
		this.y = y;
	}
}

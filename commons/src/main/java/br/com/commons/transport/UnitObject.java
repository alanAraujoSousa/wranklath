package br.com.commons.transport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.commons.enums.UnitTypeEnum;


@XmlRootElement(name = "unit")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitObject {
	
	private Long id;
	private PlaceObject place;
	private Integer type;

	
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
}

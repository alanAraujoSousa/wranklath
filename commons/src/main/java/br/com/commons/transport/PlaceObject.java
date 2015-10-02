package br.com.commons.transport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.commons.enums.PlaceTypeEnum;

@XmlRootElement(name = "place")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlaceObject {
	
	private Integer x;
	private Integer y;
	private Integer type;
	
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
}

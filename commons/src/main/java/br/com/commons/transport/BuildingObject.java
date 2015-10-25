package br.com.commons.transport;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.commons.enums.BuildingTypeEnum;

@XmlRootElement(name = "building")
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildingObject {

	private Long id;
	private Date conclusionDate;
	private Integer type;
	private PlaceObject place;
	private String userLogin;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the conclusionDate
	 */
	public Date getConclusionDate() {
		return conclusionDate;
	}

	/**
	 * @param conclusionDate
	 *            the conclusionDate to set
	 */
	public void setConclusionDate(Date conclusionDate) {
		this.conclusionDate = conclusionDate;
	}

	/**
	 * @return the type
	 */
	public BuildingTypeEnum getType() {
		return BuildingTypeEnum.getType(this.type);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(BuildingTypeEnum type) {
		if (type != null) {
			this.type = type.getId();
		}
	}
	/**
	 * @return the place
	 */
	public PlaceObject getPlace() {
		return place;
	}

	/**
	 * @param place
	 *            the place to set
	 */
	public void setPlace(PlaceObject place) {
		this.place = place;
	}

	/**
	 * @return the userLogin
	 */
	public String getUserLogin() {
		return userLogin;
	}

	/**
	 * @param userLogin the userLogin to set
	 */
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
}

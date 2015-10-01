package br.com.engine.persistence.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.commons.enums.BuildingTypeEnum;
import br.com.commons.transport.interfaces.TransportObjectInterface;

@Entity
@Table(name = "building")
public class Building implements Serializable, TransportObjectInterface{

	private static final long serialVersionUID = 4655810757200542148L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "place_id")
	private Place place;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date conclusionDate;
	
	@Column(name = "type")
	private Integer type;

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
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the type
	 */
	public BuildingTypeEnum getType() {
		return BuildingTypeEnum.getType(type);
	}

	/**
	 * @param type the type to set
	 */
	public void setType(BuildingTypeEnum type) {
		if (type != null) {
			this.type = type.getId();			
		}
	}

	/**
	 * @return the place
	 */
	public Place getPlace() {
		return place;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(Place place) {
		this.place = place;
	}

	/**
	 * @return the conclusionDate
	 */
	public Date getConclusionDate() {
		return conclusionDate;
	}

	/**
	 * @param conclusionDate the conclusionDate to set
	 */
	public void setConclusionDate(Date conclusionDate) {
		this.conclusionDate = conclusionDate;
	}

	
	
}

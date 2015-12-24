package br.com.engine.persistence.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.transport.BuildingObject;
import br.com.engine.persistence.beans.Building;
import br.com.engine.persistence.beans.Place;
import br.com.engine.persistence.beans.User;
import br.com.engine.persistence.core.GenericDAO;

public class BuildingDAO extends GenericDAO<Building> {

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Building findById(Long id) {
		return (Building) this.getCriteria().add(Restrictions.eq("id", id))
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<Building> listByUser(Long id) {
		return this.getCriteria().add(Restrictions.eq("user.id", id)).list();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(Building building) {
		super.delete(building);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(Building building) {
		super.update(building);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Building create(BuildingObject buildingObject, User user, Place place) {
		Building building = new Building();
		building.setUser(user);
		building.setPlace(place);
		place.setBuilding(building);
		building.setType(buildingObject.getType());
		building.setConclusionDate(buildingObject.getConclusionDate());

		save(building);
		return building;
	}

}

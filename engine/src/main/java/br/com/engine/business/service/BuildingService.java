package br.com.engine.business.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.commons.transport.BuildingObject;
import br.com.commons.transport.UserObject;
import br.com.engine.persistence.beans.Building;
import br.com.engine.persistence.dao.BuildingDAO;
import br.com.engine.persistence.dao.UserDAO;

public class BuildingService {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private BuildingDAO buildingDAO;

	public List<BuildingObject> listByUser(UserObject userObject) {
		List<Building> buildings = this.buildingDAO.listByUser(userObject
				.getId());
		List<BuildingObject> buildingObjects = new ArrayList<>();
		for (Building building : buildings) {
			buildingObjects.add(building.generateTransportObject());
		}
		return buildingObjects;
	}
}

package br.com.engine.business.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.commons.exceptions.InvalidArgumentException;
import br.com.commons.exceptions.MovementException;
import br.com.commons.transport.MovementObject;
import br.com.commons.transport.PlaceObject;
import br.com.commons.transport.UnitObject;
import br.com.commons.transport.UserObject;
import br.com.commons.utils.Utils;
import br.com.engine.persistence.beans.Unit;
import br.com.engine.persistence.cache.UnitCache;
import br.com.engine.persistence.dao.UnitDAO;
import br.com.engine.persistence.dao.UserDAO;

public class UnitService {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UnitDAO unitDAO;

	public void move(UserObject user, Long id, Deque<Integer> places) {

		Unit unit = this.unitDAO.findByIdAndUser(id, user.getId());
		if (unit == null)
			throw new InvalidArgumentException("Unit not found: " + id);

		UnitObject unitObject = UnitCache.getInstance().findUnitByIdOnMove(id);
		PlaceObject placeObject = unitObject.getPlace();
		Integer actualX = placeObject.getX();
		Integer actualY = placeObject.getY();

		boolean isValid = Utils.validateMovement(placeObject, places);
		if (!isValid)
			throw new MovementException("You choose a movement invalid!");

		Iterator<Integer> iterator = places.iterator();
		Integer nextX = iterator.next();
		Integer nextY = iterator.next();

		Integer moveBuff = Utils.calcMoveBuff(unitObject.getType(),
				placeObject.getType());
		Integer moveTime = Utils.DEFAULT_MOVE_TIME;

		// diagonal moviment
		if (!nextX.equals(actualX) && !nextY.equals(actualY))
			moveTime = Utils.DIAGONAL_MOVE_TIME;

		moveTime -= (moveTime * unitObject.getType().getVelocity()) / 100;
		moveTime -= (moveTime * moveBuff) / 100;

		Date now = new Date();
		now.setTime(now.getTime() + moveTime);
		unitObject.setTimeToNextMove(now);
		MovementObject movementObject = new MovementObject();
		movementObject.setMoves(places);
		unitObject.setMovementObject(movementObject);
	}

	public List<UnitObject> listByUser(UserObject userObject) {
		List<Unit> units = this.unitDAO.listByUser(userObject.getId());
		List<UnitObject> unitObjects = new ArrayList<>();
		for (Unit unit : units) {
			unitObjects.add(unit.generateTransportObject());
		}
		return unitObjects;
	}
}

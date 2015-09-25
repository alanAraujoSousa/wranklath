package br.com.engine.business.scheduler;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.enums.UnitIntentEnum;
import br.com.commons.enums.UnitTypeEnum;
import br.com.commons.transport.MovementObject;
import br.com.commons.transport.UnitObject;
import br.com.commons.utils.Utils;
import br.com.engine.persistence.beans.Building;
import br.com.engine.persistence.beans.Place;
import br.com.engine.persistence.beans.Unit;
import br.com.engine.persistence.cache.EngineCache;
import br.com.engine.persistence.core.HibernateUtil;
import br.com.engine.persistence.dao.PlaceDAO;
import br.com.engine.persistence.dao.UnitDAO;

public class MovementScheduler {

	private static final Logger LOGGER = Logger
			.getLogger(MovementScheduler.class);

	public static int DEFAULT_MOVE_TIME = 60000; // milliseconds
	public static int DIAGONAL_MOVE_TIME = 90000; // milliseconds
	
	public static int ORTOGONAL_COST = 1;
	public static double DIAGONAL_COST = 1.5;

	@Autowired
	private PlaceDAO placeDAO;

	@Autowired
	private UnitDAO unitDAO;

	/**
	 * Calculate movements every 1 seconds
	 */
	@Scheduled(fixedDelay = 1000 * 1)
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void runForestRun() {
		int cont = 0;
		EngineCache instance = EngineCache.getInstance();
		for (Iterator<Entry<UnitObject, MovementObject>> iterator = instance
				.getRepository().entrySet().iterator(); iterator.hasNext();) {

			Entry<UnitObject, MovementObject> entry = iterator.next();

			UnitObject unit = entry.getKey();
			MovementObject move = entry.getValue();

			// stop process if move list is empty.
			if (move.getMoves().isEmpty())
				continue;

			// Execute next moviment.
			boolean isNextMoveValid = execMovement(unit, move); 
			if (!isNextMoveValid)
				move.getMoves().clear();

			cont++;

			// To execute batch processing
			if (cont % 10 == 0) { // TODO Validate consistence.
				HibernateUtil.getInstance().currentSession().flush();
				HibernateUtil.getInstance().currentSession().clear();
			}
		}
	}

	public boolean execMovement(UnitObject unitObject, MovementObject move) {
		Date timeToNextMove = move.getTimeToNextMove();
		Date now = new Date();

		if (timeToNextMove.compareTo(now) <= 0) { // it's time to move
													// motherfucker

			// Get error margin.
			Integer diff = (int) (timeToNextMove.getTime() - now.getTime());
			
			MovementObject enemyMove = null;

			// if my intent is attack other
			// test attack
			if (move.getUnitIntent().equals(UnitIntentEnum.ATTACK)) { // ATTAAAAACK

				// Get my target Id
				Long enemyId = move.getTargetId();

				// Enemy actual position
				enemyMove = EngineCache.getInstance().findMovementByUnitId(
						enemyId);

				move = execAttack(unitObject, move, enemyMove);
			}

			// Get "place" by coordinates.
			Integer x = move.getMoves().pollFirst();
			Integer y = move.getMoves().pollFirst();

			Place place = placeDAO.findByCoordinates(x, y);

			// Verify if this place is passable or if have a building
			// constructed.
			boolean passable = place.getType().isPassable();
			Building building = place.getBuilding();
			if (building != null || !passable) {
				return false;
			}
			Unit unit = place.getUnit();
			if (unit != null) {
				return false;
			}
			
			// Calc time to next move.
			Integer moveBuff = place.getType().getMoveBuff();
			Integer moveTime = DEFAULT_MOVE_TIME;

			// diagonal moviment
			if (x != move.getActualX() && y != move.getActualY()) {
				moveTime = DIAGONAL_MOVE_TIME;
			}

			moveTime -= (moveTime * unitObject.getType().getVelocity()) / 100;
			moveTime -= (moveTime * moveBuff) / 100;
			moveTime -= diff; 
			
			timeToNextMove = new Date(now.getTime() + moveTime);
			move.setTimeToNextMove(timeToNextMove);

			// Run movement.
			Unit myUnit = this.unitDAO.findById(unitObject.getId());
			this.unitDAO.editPlace(myUnit, place);

			// Edit actual position in cache.
			// TODO Only edit cache after flush and clean session.
			move.setActualX(x);
			move.setActualX(y);

			// Test ATTACK again :)
			if (move.getUnitIntent().equals(UnitIntentEnum.ATTACK)) {
				move = execAttack(unitObject, move, enemyMove);
			}
		}
		return true;
	}

	private MovementObject execAttack(UnitObject unitObject,
			MovementObject move, MovementObject enemyMove) {

		Integer enemyActualX = enemyMove.getActualX();
		Integer enemyActualY = enemyMove.getActualY();

		// My actual position
		Integer actualX = move.getActualX();
		Integer actualY = move.getActualY();

		// My attack range
		Integer atkRange = unitObject.getType().getAtkRange();

		// My Enemy is close to my attack ?
		boolean isReachable = Utils.checkProximity(actualX, actualY,
				enemyActualX, enemyActualY, atkRange);

		if (isReachable) {

			// TODO Implement Combate.
			// Cache of combate, separate in another cache.

		} else {
			// My visibility range
			Integer visibilityRange = unitObject.getType().getVisibility();

			// My Enemy is visible ?
			boolean isVisible = Utils.checkProximity(actualX, actualY,
					enemyActualX, enemyActualY, visibilityRange);

			// If the enemy is visible.
			if (isVisible) {

				// TODO Recalculate to the best move to the target.

				// If the enemy is moving.
				if (!enemyMove.getMoves().isEmpty()) {
					// TODO Give some buff to pursuit. :)
				}
			}
		}

		return move;
	}
}

package br.com.engine.business.scheduler;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.enums.UnitIntentEnum;
import br.com.commons.transport.CombatObject;
import br.com.commons.transport.MovementObject;
import br.com.commons.transport.PlaceObject;
import br.com.commons.transport.UnitObject;
import br.com.commons.utils.Utils;
import br.com.engine.persistence.beans.Building;
import br.com.engine.persistence.beans.Place;
import br.com.engine.persistence.beans.Unit;
import br.com.engine.persistence.cache.CombatCache;
import br.com.engine.persistence.cache.MovementCache;
import br.com.engine.persistence.core.HibernateUtil;
import br.com.engine.persistence.dao.PlaceDAO;
import br.com.engine.persistence.dao.UnitDAO;

public class MovementScheduler {

	private static final Logger LOGGER = Logger
			.getLogger(MovementScheduler.class);

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
		MovementCache moveCache = MovementCache.getInstance();
		CombatCache lobbyCache = CombatCache.getInstance();

		for (Iterator<Entry<Long, UnitObject>> iterator = moveCache
				.getRepository().entrySet().iterator(); iterator.hasNext();) {

			Entry<Long, UnitObject> entry = iterator.next();
			UnitObject unit = entry.getValue();

			// if is correct time.
			Date now = new Date();
			Date timeToNextMove = unit.getTimeToNextMove();
			if (timeToNextMove.compareTo(now) <= 0 || timeToNextMove == null) {

				// Unit is in combat.
				if (unit.getCombatId() != null && !unit.getCombatId().isEmpty()) {

					CombatObject lobby = lobbyCache
							.getLobby(unit.getCombatId());

					Long targetId = unit.getTargetId();

					// Verify if my enemy is in my lobby.
					UnitObject enemy = null;
					Set<UnitObject> side;
					if (unit.getCombatId().endsWith("a")) {
						side = lobby.getSideB();
					} else {
						side = lobby.getSideA();
					}
					for (UnitObject unitObject : side) {
						if (unitObject.getId() == targetId) {
							enemy = unitObject;
							break;
						}
					}

					if (enemy == null) {
						enemy = moveCache.findUnitById(targetId);
						if (enemy == null) { // He died \o/.

						} else { // Register-me or create lobby with my enemy.
							execAttack(unit, enemy);
						}
					} else { // Fire attack motherfucker
						Integer unitQtd = unit.getQuantity();
						Integer atkPower = unit.getType().getAttack();
						PlaceObject place = unit.getPlace();

						Integer atkBuff = Utils.calcAttackBuff(unit.getType(),
								enemy.getType(), place.getType());
						
						atkPower = Utils.calcDamage(atkPower, unitQtd, atkBuff);
						
						Integer enemyQtd = enemy.getQuantity(); 
						Integer life = enemy.getType().getLife();
						Integer armour = enemy.getType().getArmour();
						
						// FIXME Work with Double
						Integer ratio = Utils.calcTotalLife(life, armour); 
						life = ratio * enemyQtd;
						life -= atkPower;
						if (life <= 0) {
							// The enemy has defeat \o/
						}
						enemyQtd = life / ratio;
						// reduzir a quantidade depois dessa rodada
					}
				}

				// If my intent is attack other
				UnitObject enemy = null;
				if (unit.getUnitIntent().equals(UnitIntentEnum.ATTACK)) { // ATTAAAAACK

					// Get my target Id
					Long enemyId = unit.getTargetId();

					// Enemy actual position
					enemy = MovementCache.getInstance().findUnitById(enemyId);

					boolean attackOccurred = execAttack(unit, enemy);
					if (attackOccurred) {
						// não executa movimentos.
					}
				}

				// Execute next moviment.
				if (!unit.getMovementObject().getMoves().isEmpty()) {
					boolean isNextMoveValid = execMovement(unit);
					if (!isNextMoveValid)
						unit.getMovementObject().getMoves().clear();

					// Test ATTACK again :)
					if (unit.getUnitIntent().equals(UnitIntentEnum.ATTACK)) {
						boolean attackOccurred = execAttack(unit, enemy);
						if (attackOccurred) {
							// return false;
						}
					}
				}

				cont++;

				// TODO Validate consistence.
				// Execute batch processing.
				if (cont % 10 == 0) {
					HibernateUtil.getInstance().currentSession().flush();
					HibernateUtil.getInstance().currentSession().clear();
				}
			}
		}
	}

	public boolean execMovement(UnitObject unitObject) {

		MovementObject unitMovement = unitObject.getMovementObject();
		Date timeToNextMove = unitObject.getTimeToNextMove();
		Date now = new Date();

		// Get error margin.
		Integer diff = (int) (timeToNextMove.getTime() - now.getTime());
		if (diff != 0) {
			LOGGER.debug("Error margin: " + diff + " on movement of: "
					+ unitObject.getId() + ", time: " + now);
		}

		// Get "place" by coordinates.
		Integer x = unitMovement.getMoves().pollFirst();
		Integer y = unitMovement.getMoves().pollFirst();

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
		Integer moveBuff = Utils.calcMoveBuff(unitObject.getType(),
				place.getType());
		Integer moveTime = Utils.DEFAULT_MOVE_TIME;

		// diagonal moviment
		if (x != unitObject.getPlace().getX()
				&& y != unitObject.getPlace().getY()) {
			moveTime = Utils.DIAGONAL_MOVE_TIME;
		}

		moveTime -= (moveTime * unitObject.getType().getVelocity()) / 100;
		moveTime -= (moveTime * moveBuff) / 100;

		timeToNextMove.setTime(timeToNextMove.getTime() + moveTime);
		unitObject.setTimeToNextMove(timeToNextMove);

		// Run movement.
		Unit myUnit = this.unitDAO.findById(unitObject.getId());
		this.unitDAO.editPlace(myUnit, place);

		// Edit actual position in cache.
		// Put place in unit to cache
		// TODO Only edit cache after flush and clean session.
		unitObject.setPlace(place.generateTransportObject());
		return true;
	}

	private boolean execAttack(UnitObject unitObject, UnitObject enemy) {

		MovementObject enemyMovement = enemy.getMovementObject();
		MovementObject unitMovement = unitObject.getMovementObject();

		// Enemy actual position
		Integer enemyActualX = enemy.getPlace().getX();
		Integer enemyActualY = enemy.getPlace().getY();

		// My actual position
		Integer actualX = unitObject.getPlace().getX();
		Integer actualY = unitObject.getPlace().getY();

		// My attack range
		Integer atkRange = unitObject.getType().getAtkRange();

		// My Enemy is close to my attack ?
		boolean isReachable = Utils.checkProximity(actualX, actualY,
				enemyActualX, enemyActualY, atkRange);

		if (isReachable) {

			long timeToNextAttack = unitObject.getTimeToNextMove().getTime()
					+ Utils.COMBAT_ROUND_TIME;

			// Verify if my enemy is already in lobby of combat.
			// Register on "Friend of enemies of my enemy." (:
			String combatId = enemy.getCombatId();
			if (combatId != null && !combatId.isEmpty()) {
				CombatObject lobby = CombatCache.getInstance().getLobby(
						combatId);
				if (lobby == null) {
					// TODO treat inconsistence.
					LOGGER.error("Detected inconsistence, army: "
							+ unitObject.getId() + ", on lobby: " + combatId);
				} else {
					// Register on correct lobby, the inverse lobby of my enemy.
					if (combatId.endsWith("a")) {
						lobby.getSideB().add(unitObject); // put in lobby
															// "b"
					} else {
						lobby.getSideA().add(unitObject); // put in lobby
															// "a"
					}
				}
			} else { // My enemy is "de boas".

				// Create Combat Lobby.
				UUID combatUUID = UUID.randomUUID();
				CombatObject combatObject = new CombatObject();
				combatObject.getSideA().add(unitObject);
				combatObject.getSideB().add(enemy);
				Date now = new Date();
				combatObject.setStartTime(now);
				CombatCache.getInstance().add(combatUUID, combatObject);

				// Save reference of combat in army's.
				unitObject.setCombatId(combatUUID.toString() + "a");
				enemy.setCombatId(combatUUID.toString() + "b");

				// If the enemy is stopped.
				if (!enemyMovement.getMoves().isEmpty()) {
					// Let my enemy attack as well.
					enemy.getTimeToNextMove().setTime(timeToNextAttack);
				}
			}

			// My time to attack.
			unitObject.getTimeToNextMove().setTime(timeToNextAttack);

			return true;
		} else { // Is not close to attack. >> PURSUIT >> .\ /.

			// My visibility range
			Integer visibilityRange = unitObject.getType().getVisibility();

			// My Enemy is visible ?
			boolean isVisible = Utils.checkProximity(actualX, actualY,
					enemyActualX, enemyActualY, visibilityRange);

			// If the enemy is visible.
			if (isVisible) {

				// TODO Recalculate the route for the best to the target.

				// If the enemy is moving.
				if (!enemyMovement.getMoves().isEmpty()) {
					// TODO Give some buff to pursuit. :)
				}
			} else {
				unitObject.setCombatId(null);

			}
			return false;
		}
	}
}

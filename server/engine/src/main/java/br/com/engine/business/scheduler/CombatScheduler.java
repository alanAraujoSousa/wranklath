package br.com.engine.business.scheduler;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.enums.UnitIntentEnum;
import br.com.commons.transport.PlaceObject;
import br.com.commons.transport.UnitObject;
import br.com.commons.utils.Utils;
import br.com.engine.persistence.beans.Unit;
import br.com.engine.persistence.cache.UnitCache;
import br.com.engine.persistence.core.HibernateUtil;
import br.com.engine.persistence.dao.UnitDAO;

public class CombatScheduler {

	private static final Logger LOGGER = Logger
			.getLogger(CombatScheduler.class);

	@Autowired
	private UnitDAO unitDAO;

	@Scheduled(fixedDelay = 1000 * 1)
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void thisIsSparta() {
		int cont = 0;
		UnitCache unitCache = UnitCache.getInstance();
		for (Iterator<Entry<Long, UnitObject>> iterator = unitCache
				.getCombatRepository().entrySet().iterator(); iterator
				.hasNext();) {
			
			Entry<Long, UnitObject> entry = iterator.next();
			UnitObject unit = entry.getValue();

			// if is correct time.
			Date now = new Date();
			Date timeToNextMove = unit.getTimeToNextMove();
			if (timeToNextMove.compareTo(now) <= 0 || timeToNextMove == null) {

				// Get error margin.
				Integer diff = (int) (now.getTime() - timeToNextMove.getTime());
				if (diff != 0) {
					LOGGER.debug("Error margin: " + diff + " on attack of: "
							+ unit.getId() + ", time: " + now);
				}

				Long targetId = unit.getTargetId();
				
				// Verify if my enemy is in lobby A or B.
				UnitObject enemy = null;
				Set<UnitObject> sideA = unit.getCombatObject().getSideA();
				Set<UnitObject> sideB = unit.getCombatObject().getSideB();

				Set<UnitObject> sideOfEnemies = null;
				if (sideA.contains(unit)) {
					sideOfEnemies = sideB;
				} else {
					sideOfEnemies = sideA;
				}

				for (UnitObject enemyObject : sideOfEnemies) {
					if (enemyObject.getId() == targetId) {
						enemy = enemyObject;
						break;
					}
				}

				if (enemy == null) {
					// Get other enemies on combat Side of enemies if my enemy is not found.
					if (!sideOfEnemies.isEmpty()) {
						enemy = sideOfEnemies.iterator().next();
					} else { 
						// If no more enemies.
						// This is not Sparta ):
						unit.setCombatObject(null);
						unit.setUnitIntent(UnitIntentEnum.MOVE);
						iterator.remove();
						continue;
					}
				}
				
				// Enemy actual position
				Integer enemyActualX = enemy.getPlace().getX();
				Integer enemyActualY = enemy.getPlace().getY();

				// My actual position
				Integer actualX = unit.getPlace().getX();
				Integer actualY = unit.getPlace().getY();

				// My attack range
				Integer atkRange = unit.getType().getAtkRange();

				// My Enemy is close to my attack ?
				boolean isReachable = Utils.checkProximity(actualX, actualY,
						enemyActualX, enemyActualY, atkRange);

				if (!isReachable) {
					// Is not close to attack. >> PURSUIT >> .\ /. >> 

					// My visibility range
					Integer visibilityRange = unit.getType().getVisibility();

					// My Enemy is visible ?
					boolean isVisible = Utils.checkProximity(actualX, actualY,
							enemyActualX, enemyActualY, visibilityRange);

					// If the enemy is visible.
					if (isVisible) {

						// TODO Recalculate the route for the best to the target.
						
						unit.setUnitIntent(UnitIntentEnum.ATTACK);
						unit.setTargetId(enemy.getId());
						iterator.remove();
					} else {
						// Ok, This time really is not Sparta ):
						unit.setCombatObject(null);
						unit.setUnitIntent(UnitIntentEnum.MOVE);
						iterator.remove();
						continue;
					}
				}


				// Fire attack motherfucker \o/ this is sparta.
				Integer unitQtd = unit.getQuantity();
				Integer atkPower = unit.getType().getAttack();
				PlaceObject place = unit.getPlace();

				Integer enemyQtd = enemy.getQuantity();
				Integer enemyPerUnitlife = enemy.getType().getLife();
				Integer armour = enemy.getType().getArmour();

				Integer atkBuff = Utils.calcAttackBuff(unit.getType(),
						enemy.getType(), place.getType());

				atkPower = Utils.calcAttackPowerPerUnit(atkPower, unitQtd,
						atkBuff);
				enemyPerUnitlife = Utils.calcTotalLifePerUnit(enemyPerUnitlife, armour);

				Integer totalLife = enemyPerUnitlife * enemyQtd;
				totalLife -= atkPower;
				Unit enemyDB = this.unitDAO.findById(enemy.getId());
				if (totalLife <= 0) {
					// The enemy has defeat \o/
					this.unitDAO.delete(enemyDB);
					UnitCache.getInstance().removeFromMove(enemy.getId());
					sideOfEnemies.remove(enemy);
					unitCache.removeFromCombat(targetId);
				} else {
					enemyQtd = totalLife / enemyPerUnitlife;
					if (enemyQtd <= 0) {
						// the enemy has defeat \o/
						this.unitDAO.delete(enemyDB);
						UnitCache.getInstance().removeFromMove(enemy.getId());
						sideOfEnemies.remove(enemy);
						unitCache.removeFromCombat(targetId);
					} else {
						LOGGER.debug("Enemy: " + enemy.getId() + " lost " + enemyQtd + " mens.");
						/*enemy.setQuantity(enemyQtd);
						enemyDB.setQuantity(enemyQtd);*/
						this.unitDAO.update(enemyDB);
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

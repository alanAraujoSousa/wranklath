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
		short cont = 0;
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
				Integer diff = (int) (timeToNextMove.getTime() - now.getTime());
				if (diff != 0) {
					LOGGER.debug("Error margin: " + diff + " on movement of: "
							+ unit.getId() + ", time: " + now);
				}

				Long targetId = unit.getTargetId();

				// Verify if my enemy is in lobby.
				UnitObject enemy = null;
				Set<UnitObject> sideA = unit.getCombatObject().getSideA();
				Set<UnitObject> sideB = unit.getCombatObject().getSideB();

				Set<UnitObject> sideOfEnemies = sideA;
				for (UnitObject enemyObject : sideOfEnemies) {
					if (enemyObject.getId() == targetId) {
						enemy = enemyObject;
						break;
					}
				}
				if (enemy == null) {
					sideOfEnemies = sideB;
					for (UnitObject enemyObject : sideOfEnemies) {
						if (enemyObject.getId() == targetId) {
							enemy = enemyObject;
							break;
						}
					}
				}

				// Get other enemies on combat Side of enemies.
				if (enemy == null) {
					if (sideA.contains(unit)) {
						sideOfEnemies = sideB;
					} else {
						sideOfEnemies = sideA;
					}
					if (!sideOfEnemies.isEmpty()) {
						enemy = sideOfEnemies.iterator().next();
					} else { // no more enemies
						unit.setCombatObject(null);
						iterator.remove();
					}
				}

				// Fire attack motherfucker
				Integer unitQtd = unit.getQuantity();
				Integer atkPower = unit.getType().getAttack();
				PlaceObject place = unit.getPlace();

				Integer enemyQtd = enemy.getQuantity();
				Integer life = enemy.getType().getLife();
				Integer armour = enemy.getType().getArmour();

				Integer atkBuff = Utils.calcAttackBuff(unit.getType(),
						enemy.getType(), place.getType());

				atkPower = Utils.calcAttackPowerPerUnit(atkPower, unitQtd, atkBuff);
				life = Utils.calcTotalLifePerUnit(life, armour);

				Integer totalLife = life * enemyQtd;
				totalLife -= atkPower;
				Unit enemyDB = this.unitDAO.findById(enemy.getId());
				if (life <= 0) {
					// The enemy has defeat \o/
					this.unitDAO.delete(enemyDB);
					unit.setCombatObject(null);
					iterator.remove();
					UnitCache.getInstance().removeFromMove(enemy.getId());
					continue;
				} else {
					enemyQtd = totalLife / life;
					if (enemyQtd <= 0) {
						// the enemy has defeat \o/
						this.unitDAO.delete(enemyDB);
						unit.setCombatObject(null);
						iterator.remove();
						UnitCache.getInstance().removeFromMove(enemy.getId());
						continue;
					} else {
						enemy.setQuantity(enemyQtd);
						this.unitDAO.update(enemyDB);
					}
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

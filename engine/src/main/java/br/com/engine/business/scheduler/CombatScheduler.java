package br.com.engine.business.scheduler;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.transport.PlaceObject;
import br.com.commons.transport.UnitObject;
import br.com.commons.utils.Utils;
import br.com.engine.persistence.beans.Unit;
import br.com.engine.persistence.cache.UnitCache;
import br.com.engine.persistence.dao.UnitDAO;

public class CombatScheduler {

	@Autowired
	private UnitDAO unitDAO;

	@Scheduled(fixedDelay = 1000 * 1)
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void thisIsSparta() {

		UnitCache moveCache = UnitCache.getInstance();
		for (Iterator<Entry<Long, UnitObject>> iterator = moveCache
				.getCombatRepository().entrySet().iterator(); iterator
				.hasNext();) {

			Entry<Long, UnitObject> entry = iterator.next();
			UnitObject unit = entry.getValue();

			// if is correct time.
			Date now = new Date();
			Date timeToNextMove = unit.getTimeToNextMove();
			if (timeToNextMove.compareTo(now) <= 0 || timeToNextMove == null) {

				Long targetId = unit.getTargetId();

				// Verify if my enemy is in my lobby.
				UnitObject enemy = null;
				Set<UnitObject> side = unit.getCombatObject().getSideA();
				for (UnitObject enemyObject : side) {
					if (enemyObject.getId() == targetId) {
						enemy = enemyObject;
						break;
					}
				}
				side = unit.getCombatObject().getSideB();
				for (UnitObject enemyObject : side) {
					if (enemyObject.getId() == targetId) {
						enemy = enemyObject;
						break;
					}
				}

				if (enemy == null) {
					// TODO get other enemies on combat Side of enemies.
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

				atkPower = Utils.calcDamage(atkPower, unitQtd, atkBuff);

				// FIXME Work with Double ?
				Integer ratio = Utils.calcTotalLife(life, armour);
				life = ratio * enemyQtd;
				life -= atkPower;
				Unit unitDB = this.unitDAO.findById(unit.getId());
				if (life <= 0) {
					// The enemy has defeat \o/
					this.unitDAO.delete(unitDB);
					iterator.remove(); 
					UnitCache.getInstance().removeFromMove(enemy.getId());
					continue;
				} else {
					enemyQtd = life / ratio;
					if (enemyQtd <= 0) {
						// the enemy has defeat \o/
						this.unitDAO.delete(unitDB);
						iterator.remove();
						UnitCache.getInstance().removeFromMove(enemy.getId());
						continue;
					} else {
						unit.setQuantity(enemyQtd);
						this.unitDAO.update(unitDB);
					}
				}
			}
		}
	}
}

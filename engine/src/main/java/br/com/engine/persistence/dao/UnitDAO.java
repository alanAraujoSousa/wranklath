package br.com.engine.persistence.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.commons.transport.UnitObject;
import br.com.engine.persistence.beans.Place;
import br.com.engine.persistence.beans.Unit;
import br.com.engine.persistence.beans.User;
import br.com.engine.persistence.core.GenericDAO;

public class UnitDAO extends GenericDAO<Unit> {

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Unit findById(Long id) {
		return (Unit) this.getCriteria().add(Restrictions.eq("id", id))
				.uniqueResult();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Unit findByIdAndUser(Long unitId, Long userId) {
		return (Unit) getCriteria().add(Restrictions.eq("id", unitId))
				.add(Restrictions.eq("user.id", userId)).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, readOnly = true)
	public List<Unit> list(){
		return getCriteria().list();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void editPlace(Unit unit, Place place) {
		unit.setPlace(place);
		this.update(unit);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(Unit unit) {
		super.delete(unit);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(Unit unit) {
		super.update(unit);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public Unit create(UnitObject unitObject, User user, Place place) {
		Unit unit = new Unit();
		unit.setUser(user);
		unit.setPlace(place);
		unit.setType(unitObject.getType());
		unit.setQuantity(unitObject.getQuantity());
		
		save(unit);
		return unit;
	}
}

package br.com.engine.persistence.cache;

import java.util.concurrent.ConcurrentHashMap;

import br.com.commons.transport.UnitObject;

public class UnitCache {

	private static UnitCache instance;
	private static ConcurrentHashMap<Long, UnitObject> onMoviment;
	private static ConcurrentHashMap<Long, UnitObject> onCombat;
	
	private UnitCache() {
		onMoviment = new ConcurrentHashMap<Long, UnitObject>();
		onCombat = new ConcurrentHashMap<Long, UnitObject>();
	}
	
	public static UnitCache getInstance() {
		if (instance == null) {
			instance = new UnitCache();
		}
		return instance;
	}
	
	public void addToMoviments(UnitObject army) {
		getMoveRepository().put(army.getId(), army);
	}
	
	
	public void addToCombats(UnitObject army) {
		getMoveRepository().put(army.getId(), army);
	}
	
	public UnitObject findUnitByIdOnMove(Long id){
		UnitObject unitObject = this.getMoveRepository().get(id);
		return unitObject;
	}

	public UnitObject findUnitByIdOnCombat(Long id){
		UnitObject unitObject = this.getCombatRepository().get(id);
		return unitObject;
	}

	/**
	 * @return the repository
	 */
	public ConcurrentHashMap<Long, UnitObject> getMoveRepository() {
		return onMoviment;
	}
	
	/**
	 * @return the repository
	 */
	public ConcurrentHashMap<Long, UnitObject> getCombatRepository() {
		return onCombat;
	}

	public void removeFromMove(Long id) {
		this.getMoveRepository().remove(id);
	}
	
	public void removeFromCombat(Long id) {
		this.getCombatRepository().remove(id);
	}
}

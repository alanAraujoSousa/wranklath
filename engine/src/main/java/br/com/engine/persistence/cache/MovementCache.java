package br.com.engine.persistence.cache;

import java.util.concurrent.ConcurrentHashMap;

import br.com.commons.transport.UnitObject;

public class MovementCache {

	private static MovementCache instance;
	private static ConcurrentHashMap<Long, UnitObject> repository;
	
	private MovementCache() {
		repository = new ConcurrentHashMap<Long, UnitObject>();
	}
	
	public static MovementCache getInstance() {
		if (instance == null) {
			instance = new MovementCache();
		}
		return instance;
	}
	
	public void add(UnitObject army) {
		getRepository().put(army.getId(), army);
	}
	
	public UnitObject findUnitById(Long id){
		UnitObject unitObject = this.getRepository().get(id);
		return unitObject;
	}

	/**
	 * @return the repository
	 */
	public ConcurrentHashMap<Long, UnitObject> getRepository() {
		return repository;
	}
}
